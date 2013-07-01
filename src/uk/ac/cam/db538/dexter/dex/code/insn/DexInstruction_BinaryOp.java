package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction12x;
import org.jf.dexlib.Code.Format.Instruction23x;

import uk.ac.cam.db538.dexter.analysis.coloring.ColorRange;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode_AssemblingState;
import uk.ac.cam.db538.dexter.dex.code.DexCode_InstrumentationState;
import uk.ac.cam.db538.dexter.dex.code.DexCode_ParsingState;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.code.insn.macro.DexMacro_SetObjectTaint;

public class DexInstruction_BinaryOp extends DexInstruction {

  // CAREFUL: produce /addr2 instructions if target and first
  // registers are equal; for commutative instructions,
  // check the second as well

  @Getter private final DexRegister regTarget;
  @Getter private final DexRegister regSourceA;
  @Getter private final DexRegister regSourceB;
  @Getter private final Opcode_BinaryOp insnOpcode;
  
  public DexInstruction_BinaryOp(DexCode methodCode, DexRegister target, DexRegister sourceA, DexRegister sourceB, Opcode_BinaryOp opcode) {
    super(methodCode);

    regTarget = target;
    regSourceA = sourceA;
    regSourceB = sourceB;
    insnOpcode = opcode;
  }

  public DexInstruction_BinaryOp(DexCode methodCode, Instruction insn, DexCode_ParsingState parsingState) throws InstructionParsingException {
    super(methodCode);

    int regA, regB, regC;

    if (insn instanceof Instruction23x && Opcode_BinaryOp.convert(insn.opcode) != null) {

      val insnBinaryOp = (Instruction23x) insn;
      regA = insnBinaryOp.getRegisterA();
      regB = insnBinaryOp.getRegisterB();
      regC = insnBinaryOp.getRegisterC();

    } else if (insn instanceof Instruction12x && Opcode_BinaryOp.convert(insn.opcode) != null) {

      val insnBinaryOp2addr = (Instruction12x) insn;
      regA = regB = insnBinaryOp2addr.getRegisterA();
      regC = insnBinaryOp2addr.getRegisterB();

    } else
      throw FORMAT_EXCEPTION;

    regTarget = parsingState.getRegister(regA);
    regSourceA = parsingState.getRegister(regB);
    regSourceB = parsingState.getRegister(regC);
    insnOpcode = Opcode_BinaryOp.convert(insn.opcode);
  }

  @Override
  public String getOriginalAssembly() {
    return insnOpcode.getAssemblyName() + " " + regTarget.getOriginalIndexString() +
           ", " + regSourceA.getOriginalIndexString() + ", " + regSourceB.getOriginalIndexString();
  }

  @Override
  public void instrument(DexCode_InstrumentationState state) {
    val code = getMethodCode();
    val insnCombineTaint = new DexInstruction_BinaryOp(code, state.getTaintRegister(regTarget), state.getTaintRegister(regSourceA), state.getTaintRegister(regSourceB), Opcode_BinaryOp.OrInt);

    if (insnOpcode == Opcode_BinaryOp.DivInt || insnOpcode == Opcode_BinaryOp.RemInt) {
      val regException = new DexRegister();
      val insnAssignTaintToException = new DexMacro_SetObjectTaint(code, regException, state.getTaintRegister(regSourceB));

      code.replace(this, throwingInsn_GenerateSurroundingCatchBlock(
                     new DexCodeElement[] { this, insnCombineTaint },
                     new DexCodeElement[] { insnAssignTaintToException },
                     regException));
    } else
      code.replace(this, new DexCodeElement[] { this, insnCombineTaint });

  }

  @Override
  public Instruction[] assembleBytecode(DexCode_AssemblingState state) {
    val regAlloc = state.getRegisterAllocation();
    int rTarget = regAlloc.get(regTarget);
    int rSourceA = regAlloc.get(regSourceA);
    int rSourceB = regAlloc.get(regSourceB);

    if (rTarget == rSourceA && fitsIntoBits_Unsigned(rTarget, 4) && fitsIntoBits_Unsigned(rSourceB, 4))
      return new Instruction[] { new Instruction12x(Opcode_BinaryOp.convert2addr(insnOpcode), (byte) rTarget, (byte) rSourceB) };
    else if (fitsIntoBits_Unsigned(rTarget, 8) && fitsIntoBits_Unsigned(rSourceA, 8) && fitsIntoBits_Unsigned(rSourceB, 8))
      return new Instruction[] { new Instruction23x(Opcode_BinaryOp.convert(insnOpcode), (short) rTarget, (short) rSourceA, (short) rSourceB)	};
    else
      return throwNoSuitableFormatFound();
  }

  @Override
  public Set<DexRegister> lvaDefinedRegisters() {
    return createSet(regTarget);
  }

  @Override
  public Set<DexRegister> lvaReferencedRegisters() {
    return createSet(regSourceA, regSourceB);
  }

  @Override
  public gcRegType gcReferencedRegisterType(DexRegister reg) {
    if (reg.equals(regSourceA) || reg.equals(regSourceB))
      return gcRegType.PrimitiveSingle;
    else
      return super.gcReferencedRegisterType(reg);
  }

  @Override
  public gcRegType gcDefinedRegisterType(DexRegister reg) {
    if (reg.equals(regTarget))
      return gcRegType.PrimitiveSingle;
    else
      return super.gcDefinedRegisterType(reg);
  }

  @Override
  public Set<GcRangeConstraint> gcRangeConstraints() {
    return createSet(
             new GcRangeConstraint(regTarget, ColorRange.RANGE_8BIT),
             new GcRangeConstraint(regSourceA, ColorRange.RANGE_8BIT),
             new GcRangeConstraint(regSourceB, ColorRange.RANGE_8BIT));
  }

  @Override
  protected DexCodeElement gcReplaceWithTemporaries(Map<DexRegister, DexRegister> mapping, boolean toRefs, boolean toDefs) {
    val newTarget = (toDefs) ? mapping.get(regTarget) : regTarget;
    val newSourceA = (toRefs) ? mapping.get(regSourceA) : regSourceA;
    val newSourceB = (toRefs) ? mapping.get(regSourceB) : regSourceB;
    return new DexInstruction_BinaryOp(getMethodCode(), newTarget, newSourceA, newSourceB, insnOpcode);
  }

  @Override
  public void accept(DexInstructionVisitor visitor) {
	visitor.visit(this);
  }
  
  @Override
  protected DexClassType[] throwsExceptions() {
	if (insnOpcode == Opcode_BinaryOp.DivInt || insnOpcode == Opcode_BinaryOp.RemInt) {
		return getParentFile().getParsingCache().LIST_Error_ArithmeticException;
	} else
		return null;
  }
}
