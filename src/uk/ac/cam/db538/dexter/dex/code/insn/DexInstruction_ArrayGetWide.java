package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction23x;

import uk.ac.cam.db538.dexter.analysis.coloring.ColorRange;
import uk.ac.cam.db538.dexter.dex.DexAssemblingCache;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.DexCode_ParsingState;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.type.UnknownTypeException;

public class DexInstruction_ArrayGetWide extends DexInstruction {

  @Getter private final DexRegister regTo1;
  @Getter private final DexRegister regTo2;
  @Getter private final DexRegister regArray;
  @Getter private final DexRegister regIndex;

  public DexInstruction_ArrayGetWide(DexCode methodCode, DexRegister to1, DexRegister to2, DexRegister array, DexRegister index) {
    super(methodCode);

    this.regTo1 = to1;
    this.regTo2 = to2;
    this.regArray = array;
    this.regIndex = index;
  }

  public DexInstruction_ArrayGetWide(DexCode methodCode, Instruction insn, DexCode_ParsingState parsingState) throws InstructionParsingException, UnknownTypeException {
    super(methodCode);

    if (insn instanceof Instruction23x && insn.opcode == Opcode.AGET_WIDE) {

      val insnStaticGet = (Instruction23x) insn;
      regTo1 = parsingState.getRegister(insnStaticGet.getRegisterA());
      regTo2 = parsingState.getRegister(insnStaticGet.getRegisterA() + 1);
      regArray = parsingState.getRegister(insnStaticGet.getRegisterB());
      regIndex = parsingState.getRegister(insnStaticGet.getRegisterC());

    } else
      throw new InstructionParsingException("Unknown instruction format or opcode");
  }

  @Override
  public String getOriginalAssembly() {
    return "aget-wide v" + regTo1.getOriginalIndexString() + ", {v" + regArray.getOriginalIndexString() + "}[v" + regIndex.getOriginalIndexString() + "]";
  }

  @Override
  public Set<DexRegister> lvaDefinedRegisters() {
    val definedRegs = new HashSet<DexRegister>();
    definedRegs.add(regTo1);
    definedRegs.add(regTo2);
    return definedRegs;
  }

  @Override
  public Set<DexRegister> lvaReferencedRegisters() {
    val definedRegs = new HashSet<DexRegister>();
    definedRegs.add(regArray);
    definedRegs.add(regIndex);
    return definedRegs;
  }

  @Override
  public Set<GcFollowConstraint> gcFollowConstraints() {
    val constraints = new HashSet<GcFollowConstraint>();
    constraints.add(new GcFollowConstraint(regTo1, regTo2));
    return constraints;
  }

  @Override
  public Set<GcRangeConstraint> gcRangeConstraints() {
    val constraints = new HashSet<GcRangeConstraint>();
    constraints.add(new GcRangeConstraint(regTo1, ColorRange.RANGE_8BIT));
    constraints.add(new GcRangeConstraint(regArray, ColorRange.RANGE_8BIT));
    constraints.add(new GcRangeConstraint(regIndex, ColorRange.RANGE_8BIT));
    return constraints;
  }

  @Override
  protected DexCodeElement gcReplaceWithTemporaries(Map<DexRegister, DexRegister> mapping) {
    return new DexInstruction_ArrayGetWide(getMethodCode(), mapping.get(regTo1), mapping.get(regTo2), mapping.get(regArray), mapping.get(regIndex));
  }

  @Override
  public Instruction[] assembleBytecode(Map<DexRegister, Integer> regAlloc, DexAssemblingCache cache) {
    int rTo1 = regAlloc.get(regTo1);
    int rTo2 = regAlloc.get(regTo2);
    int rArray = regAlloc.get(regArray);
    int rIndex = regAlloc.get(regIndex);

    if (fitsIntoBits_Unsigned(rTo1, 8) && rTo1 + 1 == rTo2 && fitsIntoBits_Unsigned(rArray, 8) && fitsIntoBits_Unsigned(rIndex, 8)) {
      return new Instruction[] {
               new Instruction23x(Opcode.AGET_WIDE, (short) rTo1, (short) rArray, (short) rIndex)
             };
    } else
      return throwCannotAssembleException("No suitable instruction format found");
  }
}
