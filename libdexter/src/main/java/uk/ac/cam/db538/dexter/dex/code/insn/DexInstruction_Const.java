package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11n;
import org.jf.dexlib.Code.Format.Instruction21h;
import org.jf.dexlib.Code.Format.Instruction21s;
import org.jf.dexlib.Code.Format.Instruction31i;
import org.jf.dexlib.Code.Format.Instruction51l;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_Const extends DexInstruction {
	private final DexRegister regTo;
	private final long value;
	// CAREFUL: if Value is 32-bit and bottom 16-bits are zero,
	//          turn it into const/high16 instruction
	public DexInstruction_Const(DexRegister to, long value, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regTo = to;
		this.value = value;
		if (this.regTo.getWidth() == RegisterWidth.SINGLE && !fitsIntoBits_Signed(this.value, 32)) throw new Error("Constant too big for a single-width const instruction");
	}
	
	public static DexInstruction_Const parse(Instruction insn, CodeParserState parsingState) {
		DexStandardRegister regTo;
		long value;
		if (insn instanceof Instruction11n && insn.opcode == Opcode.CONST_4) {
			final org.jf.dexlib.Code.Format.Instruction11n insnConst4 = (Instruction11n)insn;
			regTo = parsingState.getSingleRegister(insnConst4.getRegisterA());
			value = insnConst4.getLiteral();
		} else if (insn instanceof Instruction21s && insn.opcode == Opcode.CONST_16) {
			final org.jf.dexlib.Code.Format.Instruction21s insnConst16 = (Instruction21s)insn;
			regTo = parsingState.getSingleRegister(insnConst16.getRegisterA());
			value = insnConst16.getLiteral();
		} else if (insn instanceof Instruction31i && insn.opcode == Opcode.CONST) {
			final org.jf.dexlib.Code.Format.Instruction31i insnConst = (Instruction31i)insn;
			regTo = parsingState.getSingleRegister(insnConst.getRegisterA());
			value = insnConst.getLiteral();
		} else if (insn instanceof Instruction21h && insn.opcode == Opcode.CONST_HIGH16) {
			final org.jf.dexlib.Code.Format.Instruction21h insnConstHigh16 = (Instruction21h)insn;
			regTo = parsingState.getSingleRegister(insnConstHigh16.getRegisterA());
			value = insnConstHigh16.getLiteral() << 16;
		} else if (insn instanceof Instruction21s && insn.opcode == Opcode.CONST_WIDE_16) {
			final org.jf.dexlib.Code.Format.Instruction21s insnConstWide16 = (Instruction21s)insn;
			regTo = parsingState.getWideRegister(insnConstWide16.getRegisterA());
			value = insnConstWide16.getLiteral();
		} else if (insn instanceof Instruction31i && insn.opcode == Opcode.CONST_WIDE_32) {
			final org.jf.dexlib.Code.Format.Instruction31i insnConstWide32 = (Instruction31i)insn;
			regTo = parsingState.getWideRegister(insnConstWide32.getRegisterA());
			value = insnConstWide32.getLiteral();
		} else if (insn instanceof Instruction51l && insn.opcode == Opcode.CONST_WIDE) {
			final org.jf.dexlib.Code.Format.Instruction51l insnConstWide = (Instruction51l)insn;
			regTo = parsingState.getWideRegister(insnConstWide.getRegisterA());
			value = insnConstWide.getLiteral();
		} else if (insn instanceof Instruction21h && insn.opcode == Opcode.CONST_WIDE_HIGH16) {
			final org.jf.dexlib.Code.Format.Instruction21h insnConstHigh16 = (Instruction21h)insn;
			regTo = parsingState.getWideRegister(insnConstHigh16.getRegisterA());
			value = insnConstHigh16.getLiteral() << 48;
		} else throw FORMAT_EXCEPTION;
		return new DexInstruction_Const(regTo, value, parsingState.getHierarchy());
	}
	
	@Override
	public String toString() {
		if (this.regTo.getWidth() == RegisterWidth.SINGLE) return "const " + regTo.toString() + ", #" + value; else return "const-wide " + regTo.toString() + ", #" + value;
	}
	
	@Override
	public void instrument() {
//    getMethodCode().replace(this,
//                            new DexCodeElement[] {
//                              this,
//                              new DexInstruction_Const(
//                                this.getMethodCode(),
//                                state.getTaintRegister(regTo),
//                                (value == 0xdec0ded) ? 1 : 0)
//                            });
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTo);
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@java.lang.SuppressWarnings("all")
	public DexRegister getRegTo() {
		return this.regTo;
	}
	
	@java.lang.SuppressWarnings("all")
	public long getValue() {
		return this.value;
	}
}