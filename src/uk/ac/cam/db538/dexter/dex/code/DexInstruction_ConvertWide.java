package uk.ac.cam.db538.dexter.dex.code;

import lombok.Getter;

public class DexInstruction_ConvertWide extends DexInstruction {

  public static enum Opcode {
    LongToDouble("long-to-double"),
    DoubleToLong("double-to-long");

    @Getter private final String AssemblyName;

    private Opcode(String assemblyName) {
      AssemblyName = assemblyName;
    }

    public static Opcode convert(org.jf.dexlib.Code.Opcode opcode) {
      switch (opcode) {
      case LONG_TO_DOUBLE:
        return LongToDouble;
      case DOUBLE_TO_LONG:
        return DoubleToLong;
      default:
        return null;
      }
    }

    public static org.jf.dexlib.Code.Opcode convert(Opcode opcode) {
      switch (opcode) {
      case LongToDouble:
        return org.jf.dexlib.Code.Opcode.LONG_TO_DOUBLE;
      case DoubleToLong:
        return org.jf.dexlib.Code.Opcode.DOUBLE_TO_LONG;
      default:
        return null;
      }
    }
  }

  @Getter private final DexRegister RegTo1;
  @Getter private final DexRegister RegTo2;
  @Getter private final DexRegister RegFrom1;
  @Getter private final DexRegister RegFrom2;
  @Getter private final Opcode InsnOpcode;

  public DexInstruction_ConvertWide(DexRegister to1, DexRegister to2, DexRegister from1, DexRegister from2, Opcode opcode) {
    RegTo1 = to1;
    RegTo2 = to2;
    RegFrom1 = from1;
    RegFrom2 = from2;
    InsnOpcode = opcode;
  }

  @Override
  public String getOriginalAssembly() {
    return InsnOpcode.getAssemblyName() + " v" + RegTo1.getOriginalId() + ", v" + RegFrom1.getOriginalId();
  }
}