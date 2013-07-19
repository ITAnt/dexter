package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction23x;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexWideRegister;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_Compare extends DexInstruction {
	private final DexSingleRegister regTo;
	private final DexStandardRegister regSourceA;
	private final DexStandardRegister regSourceB;
	private final Opcode_Compare opcode;
	
	public DexInstruction_Compare(DexSingleRegister target, DexSingleRegister sourceA, DexSingleRegister sourceB, boolean ltBias, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regTo = target;
		this.regSourceA = sourceA;
		this.regSourceB = sourceB;
		this.opcode = ltBias ? Opcode_Compare.CmplFloat : Opcode_Compare.CmpgFloat;
	}
	
	public DexInstruction_Compare(DexSingleRegister target, DexWideRegister sourceA, DexWideRegister sourceB, boolean ltBias, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regTo = target;
		this.regSourceA = sourceA;
		this.regSourceB = sourceB;
		this.opcode = ltBias ? Opcode_Compare.CmplDouble : Opcode_Compare.CmpgDouble;
	}
	
	public DexInstruction_Compare(DexSingleRegister target, DexWideRegister sourceA, DexWideRegister sourceB, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regTo = target;
		this.regSourceA = sourceA;
		this.regSourceB = sourceB;
		this.opcode = Opcode_Compare.CmpLong;
	}
	
	public static DexInstruction_Compare parse(Instruction insn, CodeParserState parsingState) {
		if (insn instanceof Instruction23x && (insn.opcode == Opcode.CMPL_FLOAT || insn.opcode == Opcode.CMPG_FLOAT)) {
			final org.jf.dexlib.Code.Format.Instruction23x insnCompare = (Instruction23x)insn;
			return new DexInstruction_Compare(parsingState.getSingleRegister(insnCompare.getRegisterA()), parsingState.getSingleRegister(insnCompare.getRegisterB()), parsingState.getSingleRegister(insnCompare.getRegisterC()), insnCompare.opcode == Opcode.CMPL_FLOAT, parsingState.getHierarchy());
		} else if (insn instanceof Instruction23x && (insn.opcode == Opcode.CMPL_DOUBLE || insn.opcode == Opcode.CMPG_DOUBLE)) {
			final org.jf.dexlib.Code.Format.Instruction23x insnCompare = (Instruction23x)insn;
			return new DexInstruction_Compare(parsingState.getSingleRegister(insnCompare.getRegisterA()), parsingState.getWideRegister(insnCompare.getRegisterB()), parsingState.getWideRegister(insnCompare.getRegisterC()), insnCompare.opcode == Opcode.CMPL_DOUBLE, parsingState.getHierarchy());
		} else if (insn instanceof Instruction23x && insn.opcode == Opcode.CMP_LONG) {
			final org.jf.dexlib.Code.Format.Instruction23x insnCompare = (Instruction23x)insn;
			return new DexInstruction_Compare(parsingState.getSingleRegister(insnCompare.getRegisterA()), parsingState.getWideRegister(insnCompare.getRegisterB()), parsingState.getWideRegister(insnCompare.getRegisterC()), parsingState.getHierarchy());
		} else throw FORMAT_EXCEPTION;
	}
	
	@Override
	public String toString() {
		return opcode.getAsmName() + " " + regTo.toString() + ", " + regSourceA.toString() + ", " + regSourceB.toString();
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTo);
	}
	
	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		return Sets.newHashSet(regSourceA, regSourceB);
	}
	
	@Override
	public void instrument() {
//    // need to combine the taint of the two compared registers and assign that to the operation result
//    val code = getMethodCode();
//    code.replace(this,
//                 new DexCodeElement[] {
//                   this,
//                   new DexInstruction_BinaryOp(code, state.getTaintRegister(regTo), state.getTaintRegister(regSourceA), state.getTaintRegister(regSourceB), Opcode_BinaryOp.OrInt),
//                 });
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@java.lang.SuppressWarnings("all")
	public DexSingleRegister getRegTo() {
		return this.regTo;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStandardRegister getRegSourceA() {
		return this.regSourceA;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStandardRegister getRegSourceB() {
		return this.regSourceB;
	}
	
	@java.lang.SuppressWarnings("all")
	public Opcode_Compare getOpcode() {
		return this.opcode;
	}
}