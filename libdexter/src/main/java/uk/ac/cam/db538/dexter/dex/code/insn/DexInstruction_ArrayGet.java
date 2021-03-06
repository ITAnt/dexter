package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;

import lombok.Getter;
import lombok.val;

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

    @Getter private final DexRegister regTo;
    @Getter private final DexSingleRegister regArray;
    @Getter private final DexSingleRegister regIndex;
    @Getter private final Opcode_GetPut opcode;

    public DexInstruction_ArrayGet(DexRegister to, DexSingleRegister array, DexSingleRegister index, Opcode_GetPut opcode, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        this.regTo = to;
        this.regArray = array;
        this.regIndex = index;
        this.opcode = opcode;

        Opcode_GetPut.checkRegisterWidth(regTo, this.opcode);
    }

    public static DexInstruction_ArrayGet parse(Instruction insn, CodeParserState parsingState) {
        val opcode = Opcode_GetPut.convert_AGET(insn.opcode);
        if (insn instanceof Instruction23x && opcode != null) {

            val insnArrayGet = (Instruction23x) insn;

            DexStandardRegister regTo;
            if (opcode == Opcode_GetPut.Wide)
                regTo = parsingState.getWideRegister(insnArrayGet.getRegisterA());
            else
                regTo = parsingState.getSingleRegister(insnArrayGet.getRegisterA());

            return new DexInstruction_ArrayGet(
                       regTo,
                       parsingState.getSingleRegister(insnArrayGet.getRegisterB()),
                       parsingState.getSingleRegister(insnArrayGet.getRegisterC()),
                       opcode,
                       parsingState.getHierarchy());

        } else
            throw FORMAT_EXCEPTION;
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
    public void accept(DexInstructionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected DexClassType[] throwsExceptions() {
        return this.hierarchy.getTypeCache().LIST_Error_Null_ArrayIndexOutOfBounds;
    }

}
