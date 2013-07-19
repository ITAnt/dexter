package uk.ac.cam.db538.dexter.dex.code;

import org.jf.dexlib.CodeItem;
import org.jf.dexlib.Code.Instruction;
import uk.ac.cam.db538.dexter.dex.code.CodeParser.Fragment;
import uk.ac.cam.db538.dexter.dex.code.CodeParser.FragmentList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCatch;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCatchAll;
import uk.ac.cam.db538.dexter.dex.code.elem.DexLabel;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleOriginalRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexWideOriginalRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexWideRegister;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.utils.Cache;
import uk.ac.cam.db538.dexter.utils.Pair;

public class CodeParserState {
	private final CodeItem codeItem;
	private final RuntimeHierarchy hierarchy;
	private final Cache<Integer, DexSingleRegister> cacheSingleReg;
	private final Cache<Integer, DexWideRegister> cacheWideReg;
	private final Cache<Long, DexLabel> cacheLabels;
	private final Cache<Pair<Long, DexClassType>, DexCatch> cacheCatches;
	private final Cache<Long, DexCatchAll> cacheCatchAlls;
	
	public CodeParserState(CodeItem codeItem, RuntimeHierarchy hierarchy) {
		
		this.codeItem = codeItem;
		this.hierarchy = hierarchy;
		this.cacheSingleReg = new Cache<Integer, DexSingleRegister>(){
			
			
			@Override
			protected DexSingleRegister createNewEntry(Integer key) {
				return new DexSingleOriginalRegister(key);
			}
		};
		this.cacheWideReg = new Cache<Integer, DexWideRegister>(){
			
			
			@Override
			protected DexWideRegister createNewEntry(Integer key) {
				return new DexWideOriginalRegister(key);
			}
		};
		this.cacheLabels = new Cache<Long, DexLabel>(){
			
			private int counter = 1;
			
			protected DexLabel createNewEntry(Long absoluteOffset) {
				return new DexLabel(counter++);
			}
		};
		this.cacheCatches = new Cache<Pair<Long, DexClassType>, DexCatch>(){
			
			private int counter = 1;
			
			@Override
			protected DexCatch createNewEntry(Pair<Long, DexClassType> offsetTypePair) {
				return new DexCatch(counter++, offsetTypePair.getValB(), CodeParserState.this.hierarchy);
			}
		};
		this.cacheCatchAlls = new Cache<Long, DexCatchAll>(){
			
			private int counter = 1;
			
			@Override
			protected DexCatchAll createNewEntry(Long absoluteOffset) {
				return new DexCatchAll(counter++);
			}
		};
	}
	
	private int getOffsetOfInstruction(Instruction insn) {
		int offset = 0;
		for (final org.jf.dexlib.Code.Instruction current : codeItem.getInstructions()) if (current.equals(insn)) return offset; else offset += current.getSize(0);
		throw new ArrayIndexOutOfBoundsException();
	}
	
	private Instruction getInstructionAtOffset(int offset) {
		int currentOffset = 0;
		for (final org.jf.dexlib.Code.Instruction insn : codeItem.getInstructions()) if (currentOffset == offset) return insn; else if (currentOffset > offset) throw new Error("Given offset does not correspond to a beginning of an instruction"); else currentOffset += insn.getSize(0);
		throw new ArrayIndexOutOfBoundsException();
	}
	
	public Instruction getDexlibInstructionAt(int offset, Instruction relativeTo) {
		return getInstructionAtOffset(offset + getOffsetOfInstruction(relativeTo));
	}
	
	public DexSingleRegister getSingleRegister(int id) {
		return cacheSingleReg.getCachedEntry(id);
	}
	
	public DexWideRegister getWideRegister(int id) {
		return cacheWideReg.getCachedEntry(id);
	}
	
	public DexLabel getLabel(long absoluteOffset) {
		return cacheLabels.getCachedEntry(absoluteOffset);
	}
	
	public DexLabel getLabel(long offset, Instruction relativeTo) {
		return getLabel(offset + getOffsetOfInstruction(relativeTo));
	}
	
	public DexCatchAll getCatchAll(long absoluteHandlerOffset) {
		return cacheCatchAlls.getCachedEntry(absoluteHandlerOffset);
	}
	
	public DexCatch getCatch(long absoluteHandlerOffset, DexClassType exceptionType) {
		return cacheCatches.getCachedEntry(new Pair<Long, DexClassType>(absoluteHandlerOffset, exceptionType));
	}
	
	public FragmentList<DexLabel> getListOfLabels() {
		final uk.ac.cam.db538.dexter.dex.code.CodeParser.FragmentList<uk.ac.cam.db538.dexter.dex.code.elem.DexLabel> list = new FragmentList<DexLabel>();
		for (final java.util.Map.Entry<java.lang.Long, uk.ac.cam.db538.dexter.dex.code.elem.DexLabel> entry : cacheLabels.entrySet()) list.add(new Fragment<DexLabel>(entry.getKey(), entry.getValue()));
		return list;
	}
	
	public FragmentList<DexCatch> getListOfCatches() {
		final uk.ac.cam.db538.dexter.dex.code.CodeParser.FragmentList<uk.ac.cam.db538.dexter.dex.code.elem.DexCatch> list = new FragmentList<DexCatch>();
		for (final java.util.Map.Entry<uk.ac.cam.db538.dexter.utils.Pair<java.lang.Long, uk.ac.cam.db538.dexter.dex.type.DexClassType>, uk.ac.cam.db538.dexter.dex.code.elem.DexCatch> entry : cacheCatches.entrySet()) list.add(new Fragment<DexCatch>(entry.getKey().getValA(), entry.getValue()));
		return list;
	}
	
	public FragmentList<DexCatchAll> getListOfCatchAlls() {
		final uk.ac.cam.db538.dexter.dex.code.CodeParser.FragmentList<uk.ac.cam.db538.dexter.dex.code.elem.DexCatchAll> list = new FragmentList<DexCatchAll>();
		for (final java.util.Map.Entry<java.lang.Long, uk.ac.cam.db538.dexter.dex.code.elem.DexCatchAll> entry : cacheCatchAlls.entrySet()) list.add(new Fragment<DexCatchAll>(entry.getKey(), entry.getValue()));
		return list;
	}
	
	@java.lang.SuppressWarnings("all")
	public RuntimeHierarchy getHierarchy() {
		return this.hierarchy;
	}
}