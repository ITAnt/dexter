package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction23x;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_ArrayGet extends DexInstruction {
	private final DexStandardRegister regTo;
	private final DexSingleRegister regArray;
	private final DexSingleRegister regIndex;
	private final Opcode_GetPut opcode;
	
	public DexInstruction_ArrayGet(DexStandardRegister to, DexSingleRegister array, DexSingleRegister index, Opcode_GetPut opcode, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regTo = to;
		this.regArray = array;
		this.regIndex = index;
		this.opcode = opcode;
	}
	
	public static DexInstruction_ArrayGet parse(Instruction insn, CodeParserState parsingState) {
		final uk.ac.cam.db538.dexter.dex.code.insn.Opcode_GetPut opcode = Opcode_GetPut.convert_AGET(insn.opcode);
		if (insn instanceof Instruction23x && opcode != null) {
			final org.jf.dexlib.Code.Format.Instruction23x insnArrayGet = (Instruction23x)insn;
			DexStandardRegister regTo;
			if (opcode == Opcode_GetPut.Wide) regTo = parsingState.getWideRegister(insnArrayGet.getRegisterA()); else regTo = parsingState.getSingleRegister(insnArrayGet.getRegisterA());
			return new DexInstruction_ArrayGet(regTo, parsingState.getSingleRegister(insnArrayGet.getRegisterB()), parsingState.getSingleRegister(insnArrayGet.getRegisterC()), opcode, parsingState.getHierarchy());
		} else throw FORMAT_EXCEPTION;
	}
	
	@Override
	public String toString() {
		return "aget" + opcode.getAsmSuffix() + " " + regTo.toString() + ", {" + regArray.toString() + "}[" + regIndex.toString() + "]";
	}
	
	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		return Sets.newHashSet(regArray, regIndex);
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTo);
	}
	
	@Override
	public void instrument() {
//    // need to combine the taint of the array object and the index
//    val code = getMethodCode();
//    val regArrayTaint = (regTo == regArray) ? new DexRegister() : state.getTaintRegister(regArray);
//    if (opcode != Opcode_GetPut.Object) {
//      code.replace(this,
//                   new DexCodeElement[] {
//                     new DexMacro_GetObjectTaint(code, regArrayTaint, regArray),
//                     this,
//                     new DexInstruction_BinaryOp(code, state.getTaintRegister(regTo), regArrayTaint, state.getTaintRegister(regIndex), Opcode_BinaryOp.OrInt)
//                   });
//    } else {
//      val regTotalTaint = new DexRegister();
//      code.replace(this,
//                   new DexCodeElement[] {
//                     new DexMacro_GetObjectTaint(code, regArrayTaint, regArray),
//                     new DexInstruction_BinaryOp(code, regTotalTaint, regArrayTaint, state.getTaintRegister(regIndex), Opcode_BinaryOp.OrInt),
//                     this,
//                     new DexMacro_SetObjectTaint(code, regTo, regTotalTaint)
//                   });
//    }
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	protected DexClassType[] throwsExceptions() {
		return this.hierarchy.getTypeCache().LIST_Error_Null_ArrayIndexOutOfBounds;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStandardRegister getRegTo() {
		return this.regTo;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexSingleRegister getRegArray() {
		return this.regArray;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexSingleRegister getRegIndex() {
		return this.regIndex;
	}
	
	@java.lang.SuppressWarnings("all")
	public Opcode_GetPut getOpcode() {
		return this.opcode;
	}
}