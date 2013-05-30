package com.rx201.dx.translator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.jf.baksmali.Adaptors.Format.InstructionMethodItem;
import org.jf.dexlib.ClassDataItem.EncodedMethod;
import org.jf.dexlib.CodeItem;
import org.jf.dexlib.DebugInfoItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.TypeListItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Analysis.AnalyzedInstruction;
import org.jf.dexlib.Code.Analysis.MethodAnalyzer;
import org.jf.dexlib.CodeItem.EncodedCatchHandler;
import org.jf.dexlib.CodeItem.TryItem;
import org.jf.dexlib.Util.AccessFlags;
import org.jf.util.IndentingWriter;

import uk.ac.cam.db538.dexter.dex.code.DexCode;

import com.android.dx.cf.code.ConcreteMethod;
import com.android.dx.cf.code.LocalVariableList;
import com.android.dx.cf.code.Ropper;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CodeStatistics;
import com.android.dx.dex.cf.OptimizerOptions;
import com.android.dx.dex.code.DalvCode;
import com.android.dx.dex.code.PositionList;
import com.android.dx.dex.code.RopTranslator;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;
import com.android.dx.rop.annotation.Annotations;
import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.DexTranslationAdvice;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.InsnList;
import com.android.dx.rop.code.LocalVariableExtractor;
import com.android.dx.rop.code.LocalVariableInfo;
import com.android.dx.rop.code.PlainCstInsn;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.code.Rops;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.rop.code.TranslationAdvice;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.ssa.Optimizer;
import com.android.dx.util.Hex;
import com.android.dx.util.IntList;
import com.rx201.dx.translator.util.MethodParameter;
import com.rx201.dx.translator.util.MethodPrototype;

public class Translator {

	private DexFile dexFile;
	private DexOptions dexOptions;

	public Translator() {
		dexOptions = new DexOptions();
	    dexOptions.targetApiLevel = 10;
		dexFile = new DexFile(dexOptions);
	}
	/*
	public void addClass() {
        DirectClassFile cf =
                new DirectClassFile(bytes, filePath, cfOptions.strictNameCheck);

            cf.setAttributeFactory(StdAttributeFactory.THE_ONE);
            cf.getMagic();

            OptimizerOptions.loadOptimizeLists(cfOptions.optimizeListFile,
                    cfOptions.dontOptimizeListFile);

            // Build up a class to output.

            CstType thisClass = cf.getThisClass();
            int classAccessFlags = cf.getAccessFlags() & ~AccessFlags.ACC_SUPER;
            CstString sourceFile = (cfOptions.positionInfo == PositionList.NONE) ? null :
                cf.getSourceFile();
            ClassDefItem out =
                new ClassDefItem(thisClass, classAccessFlags,
                        cf.getSuperclass(), cf.getInterfaces(), sourceFile);

            Annotations classAnnotations =
                AttributeTranslator.getClassAnnotations(cf, cfOptions);
            if (classAnnotations.size() != 0) {
                out.setClassAnnotations(classAnnotations);
            }

            processFields(cf, out);
            processMethods(cf, cfOptions, dexOptions, out);

            dexFile.add(out);
	}
	*/
	private void processMethod(EncodedMethod method) {
		CodeItem code = method.codeItem;
		if (code == null) 
			return;
		int paramSize = code.getInWords();
		boolean isStatic = (method.accessFlags & AccessFlags.STATIC.getValue()) != 0;

		RopMethod rmeth = toRop(code);
		System.out.println("==== Before Optimization ====");
		dump(rmeth);
		
        rmeth = Optimizer.optimize(rmeth, paramSize, isStatic, false, DexTranslationAdvice.THE_ONE);
		System.out.println("==== After Optimization ====");
		dump(rmeth);
		
        DalvCode dcode = RopTranslator.translate(rmeth, PositionList.NONE, null, paramSize, dexOptions);
	}
	
	public void write() {
		Writer humanOut = new OutputStreamWriter(System.out);
        try {
			byte[] outArray = dexFile.toDex(humanOut, true);
	        humanOut.flush();
	        
	        FileOutputStream output = new FileOutputStream(new File("result.dex"));
	        output.write(outArray);
	        output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private RopMethod toRop(CodeItem method) {
		MethodAnalyzer analyzer = new MethodAnalyzer(method.getParent(), false, null);
		analyzer.analyze();
        HashMap<Instruction, AnalyzedInstruction> analysisResult = new HashMap<Instruction, AnalyzedInstruction>();
        for(AnalyzedInstruction analyzedInst : analyzer.getInstructions()) {
                analysisResult.put(analyzedInst.getInstruction(), analyzedInst);
        }
        
        // Build basic blocks
        ArrayList<ArrayList<AnalyzedInstruction>> basicBlocks = buildBasicBlocks(analyzer);
        
        Converter converter = new Converter(analyzer, method);

        IndentingWriter writer = new IndentingWriter(new OutputStreamWriter(System.out));
        try {
			writer.write(String.format("%s total reg: %d param reg: %d\n", method.getParent().method.getShortMethodString(), 
					method.getRegisterCount(),
					method.getParent().method.getPrototype().getParameterRegisterCount()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        // Convert basicBlocks, hold the result in the temporary map. It is indexed by the basic block's first AnalyzedInst.
        HashMap<AnalyzedInstruction, ArrayList<Insn>> convertedBasicBlocks = new HashMap<AnalyzedInstruction, ArrayList<Insn>>();
        HashMap<AnalyzedInstruction, ConvertedResult> convertedBasicBlocksInfo = new HashMap<AnalyzedInstruction, ConvertedResult>();
        HashSet<AnalyzedInstruction> auxAdded = new HashSet<AnalyzedInstruction>();
        for(int bi=0; bi< basicBlocks.size(); bi++) 
    		convertedBasicBlocks.put(basicBlocks.get(bi).get(0), new ArrayList<Insn>());
        	
        for(int bi=0; bi< basicBlocks.size(); bi++) {
        	ArrayList<AnalyzedInstruction> basicBlock = basicBlocks.get(bi);
        	AnalyzedInstruction bbIndex = basicBlock.get(0);
        	
        	// Process instruction in the basic block as a whole, 
        	ArrayList<Insn> insnBlock = convertedBasicBlocks.get(bbIndex);
        	ConvertedResult lastInsn = null;
        	for(int i = 0; i < basicBlock.size(); i++) {
        		AnalyzedInstruction inst = basicBlock.get(i);
        		if (inst.getInstruction() != null) {
        			InstructionMethodItem<Instruction> x = new InstructionMethodItem<Instruction>(method, 0, inst.getInstruction()) ;
					try {
						x.writeTo(writer);
						writer.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        		lastInsn = converter.convert(inst);
        		insnBlock.addAll(lastInsn.insns);
        		
        		if (i != basicBlock.size() - 1) {
        			assert lastInsn.auxInsns.size() == 0;
        		} else if (lastInsn.auxInsns.size() != 0) { // Propagate auxInsns to primary successors
        			AnalyzedInstruction s = lastInsn.primarySuccessor;
    				assert !auxAdded.contains(s);
    				for(int ai = 0; ai < lastInsn.auxInsns.size(); ai++)
    					convertedBasicBlocks.get(s).add(ai, lastInsn.auxInsns.get(ai));
    				auxAdded.add(s);
        		}
        			
				try {
					writer.write("\n    --> ");
					writer.write(lastInsn.insns.get(0).toHuman());
					if (lastInsn.insns.size() > 1)
						writer.write("...");
					writer.write("\n");
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
            // Add move-params to the beginning of the first block
        	if (bi == 0) {
        		for(MethodParameter param : new MethodPrototype(method)) {
	                Type one = Type.intern(param.getType().getTypeDescriptor());
	                Insn insn = new PlainCstInsn(Rops.opMoveParam(one), SourcePosition.NO_INFO, RegisterSpec.make(param.getAbsoluteRegIndex(), one),
	                                             RegisterSpecList.EMPTY,
	                                             CstInteger.make(param.getIndex()));
	                insnBlock.add(param.getIndex(), insn);
                }
        	}
        	
        	convertedBasicBlocksInfo.put(bbIndex, lastInsn);
        }
        
        
        // Finally convert to ROP's BasicBlockList form from convertedBasicBlocks
        BasicBlockList ropBasicBlocks = new BasicBlockList(convertedBasicBlocks.size());
        int bbIndex = 0;
       
        for(AnalyzedInstruction head : convertedBasicBlocks.keySet()) {
        	ArrayList<Insn> insnBlock = convertedBasicBlocks.get(head); 
        	ConvertedResult lastInsn = convertedBasicBlocksInfo.get(head);
        	
           	// then convert them to InsnList
        	InsnList insns = new InsnList(insnBlock.size());
        	for(int i=0 ;i<insnBlock.size(); i++)
        		insns.set(i, insnBlock.get(i));
        	insns.setImmutable();
        	
        	IntList successors = new IntList();
        	for(AnalyzedInstruction s : lastInsn.successors) 
        		successors.add(s.getInstructionIndex());
        	successors.setImmutable();
        	
        	int label = head.getInstructionIndex();
        	BasicBlock ropBasicBlock = new BasicBlock(label, insns, successors, lastInsn.primarySuccessor != null ? lastInsn.primarySuccessor.getInstructionIndex() : -1);
        	ropBasicBlocks.set(bbIndex++, ropBasicBlock);
        }


        return new SimpleRopMethod(ropBasicBlocks, analyzer.getStartOfMethod().getSuccesors().get(0).getInstructionIndex());
	}
	
	private ArrayList<ArrayList<AnalyzedInstruction>> buildBasicBlocks(MethodAnalyzer analyzer) {
        ArrayList<ArrayList<AnalyzedInstruction>> basicBlocks = new ArrayList<ArrayList<AnalyzedInstruction>>();
        
        Stack<AnalyzedInstruction> leads = new Stack<AnalyzedInstruction>();
        assert analyzer.getStartOfMethod().getSuccesors().size() == 0;
        leads.push(analyzer.getStartOfMethod().getSuccesors().get(0));
        HashSet<Integer> visited = new HashSet<Integer>();
        
        while(!leads.empty()) {
        	AnalyzedInstruction first = leads.pop();
        	int id = first.getInstructionIndex();
        	if (visited.contains(id)) continue; // Already visited this basic block before.
        	visited.add(id);
        	
        	ArrayList<AnalyzedInstruction> block = new ArrayList<AnalyzedInstruction>(); 
        	// Extend this basic block as far as possible
        	AnalyzedInstruction current = first; // Always refer to latest-added instruction in the bb
        	block.add(current);
        	while(current.getSuccessorCount() == 1  && (!current.getInstruction().opcode.canThrow())) { 
        		// Condition 1: current has only one successor
        		// Condition 2: next instruction has only one predecessor
        		// Condition 3: current cannot throw
        		AnalyzedInstruction next = current.getSuccesors().get(0);
        		if (next.getPredecessorCount() == 1) {
        			block.add(next);
        			current = next;
        		} else
        			break;
        	}
        	
        	// Add successors of current to the to-be-visit stack
        	for(AnalyzedInstruction i : current.getSuccesors())
        		leads.push(i);
        	
        	basicBlocks.add(block);
       }
        
        return basicBlocks;
	}

	
	private static void dump(RopMethod rmeth) {
		StringBuilder sb = new StringBuilder();
		
        BasicBlockList blocks = rmeth.getBlocks();
        int[] order = blocks.getLabelsInOrder();

        sb.append("first " + Hex.u2(rmeth.getFirstLabel()) + "\n");

        for (int label : order) {
            BasicBlock bb = blocks.get(blocks.indexOfLabel(label));
            sb.append("block ");
            sb.append(Hex.u2(label));
            sb.append("\n");

            IntList preds = rmeth.labelToPredecessors(label);
            int psz = preds.size();
            for (int i = 0; i < psz; i++) {
                sb.append("  pred ");
                sb.append(Hex.u2(preds.get(i)));
                sb.append("\n");
            }

            InsnList il = bb.getInsns();
            int ilsz = il.size();
            for (int i = 0; i < ilsz; i++) {
                Insn one = il.get(i);
                sb.append("  ");
                sb.append(il.get(i).toHuman());
                sb.append("\n");
            }

            IntList successors = bb.getSuccessors();
            int ssz = successors.size();
            if (ssz == 0) {
                sb.append("  returns\n");
            } else {
                int primary = bb.getPrimarySuccessor();
                for (int i = 0; i < ssz; i++) {
                    int succ = successors.get(i);
                    sb.append("  next ");
                    sb.append(Hex.u2(succ));

                    if ((ssz != 1) && (succ == primary)) {
                        sb.append(" *");
                    }

                    sb.append("\n");
                }
            }
        }
        System.out.println(sb.toString());
	}

}
