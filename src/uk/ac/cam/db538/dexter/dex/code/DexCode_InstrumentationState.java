package uk.ac.cam.db538.dexter.dex.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.cam.db538.dexter.dex.DexInstrumentationCache;
import uk.ac.cam.db538.dexter.dex.code.insn.pseudo.invoke.ContentResolverInstrumentor;
import uk.ac.cam.db538.dexter.dex.code.insn.pseudo.invoke.ExternalCallInstrumentor;
import uk.ac.cam.db538.dexter.dex.code.insn.pseudo.invoke.String_GetBytesInstrumentor;
import uk.ac.cam.db538.dexter.dex.code.insn.pseudo.invoke.Cursor_GetStringInstrumentor;

import lombok.Getter;
import lombok.val;

public class DexCode_InstrumentationState {
  private final Map<DexRegister, DexRegister> registerMap;
  private final int idOffset;

  @Getter private final DexInstrumentationCache cache;
  @Getter private final boolean needsCallInstrumentation;
  @Getter private final DexRegister internalClassAnnotationRegister;

  public DexCode_InstrumentationState(DexCode code, DexInstrumentationCache cache) {
    this.cache = cache;

    val parentMethod = code.getParentMethod();
    this.needsCallInstrumentation = parentMethod != null && !parentMethod.isAbstract();
    this.internalClassAnnotationRegister = (parentMethod != null && parentMethod.isVirtual()) ? new DexRegister() : null;

    registerMap = new HashMap<DexRegister, DexRegister>();

    // find the maximal register id in the code
    // this is strictly for GUI purposes
    // actual register allocation happens later;
    int maxId = -1;
    for (val reg : code.getUsedRegisters()) {
      val id = reg.getOriginalIndex();
      if (maxId < id)
        maxId = id;
    }
    idOffset = maxId + 1;
  }

  public DexRegister getTaintRegister(DexRegister reg) {
    if (reg == null)
      return null;

    val taintReg = registerMap.get(reg);
    if (taintReg == null) {
      val newReg = new DexRegister(reg.getOriginalIndex() + idOffset);
      registerMap.put(reg, newReg);
      return newReg;
    } else
      return taintReg;
  }

  public List<ExternalCallInstrumentor> getExternalCallInstrumentors() {
    val instrumentors = new ArrayList<ExternalCallInstrumentor>();
    instrumentors.add(new ContentResolverInstrumentor());
    return instrumentors;
  }
}