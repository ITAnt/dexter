package uk.ac.cam.db538.dexter.dex.code;

import lombok.Getter;

public class DexInstruction_IfTest extends DexInstruction {

  public static enum Operation {
    eq,
    ne,
    lt,
    ge,
    gt,
    le
  }

  @Getter private final DexRegister RegA;
  @Getter private final DexRegister RegB;
  @Getter private final DexLabel Target;
  @Getter private final Operation Type;

  public DexInstruction_IfTest(DexRegister regA, DexRegister regB, DexLabel target, Operation type) {
    RegA = regA;
    RegB = regB;
    Target = target;
    Type = type;
  }

  @Override
  public String getOriginalAssembly() {
    return "if-" + Type.name() + " v" + RegA.getOriginalId() +
           ", v" + RegB.getOriginalId() + ", L" + Target.getOriginalOffset();
  }
}