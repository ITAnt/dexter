package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction12x;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_Convert extends DexInstruction {
	private final DexStandardRegister regTo;
	private final DexStandardRegister regFrom;
	private final Opcode_Convert insnOpcode;
	
	public DexInstruction_Convert(DexStandardRegister to, DexStandardRegister from, Opcode_Convert opcode, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		regTo = to;
		regFrom = from;
		insnOpcode = opcode;
	}
	
	public static DexInstruction_Convert parse(Instruction insn, CodeParserState parsingState) {
		final uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Convert opcode = Opcode_Convert.convert(insn.opcode);
		if (insn instanceof Instruction12x && opcode != null) {
			final org.jf.dexlib.Code.Format.Instruction12x insnConvert = (Instruction12x)insn;
			DexStandardRegister regTo;
			DexStandardRegister regFrom;
			if (opcode.getWidthTo() == RegisterWidth.SINGLE) regTo = parsingState.getSingleRegister(insnConvert.getRegisterA()); else regTo = parsingState.getWideRegister(insnConvert.getRegisterA());
			if (opcode.getWidthFrom() == RegisterWidth.SINGLE) regFrom = parsingState.getSingleRegister(insnConvert.getRegisterB()); else regFrom = parsingState.getWideRegister(insnConvert.getRegisterB());
			return new DexInstruction_Convert(regTo, regFrom, opcode, parsingState.getHierarchy());
		} else throw FORMAT_EXCEPTION;
	}
	
	@Override
	public String toString() {
		return insnOpcode.getAssemblyName() + " " + regTo.toString() + ", " + regFrom.toString();
	}
	
	@Override
	public void instrument() {
//    // need to copy to taint across
//    val code = getMethodCode();
//    code.replace(this,
//                 new DexCodeElement[] {
//                   this,
//                   new DexInstruction_Move(code, state.getTaintRegister(regTo), state.getTaintRegister(regFrom), false)
//                 });
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTo);
	}
	
	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		return Sets.newHashSet(regFrom);
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStandardRegister getRegTo() {
		return this.regTo;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexStandardRegister getRegFrom() {
		return this.regFrom;
	}
	
	@java.lang.SuppressWarnings("all")
	public Opcode_Convert getInsnOpcode() {
		return this.insnOpcode;
	}
}