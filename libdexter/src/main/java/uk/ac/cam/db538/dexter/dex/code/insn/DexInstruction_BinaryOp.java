package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction12x;
import org.jf.dexlib.Code.Format.Instruction23x;

import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_BinaryOp.Arg;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexWideRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

import com.google.common.collect.Sets;

public class DexInstruction_BinaryOp extends DexInstruction {

    // CAREFUL: produce /addr2 instructions if target and first
    // registers are equal; for commutative instructions,
    // check the second as well

    @Getter private final DexRegister regTo;
    @Getter private final DexRegister regArgA;
    @Getter private final DexRegister regArgB;
    @Getter private final Opcode_BinaryOp insnOpcode;

    private DexInstruction_BinaryOp(DexRegister target, DexRegister sourceA, DexRegister sourceB, Opcode_BinaryOp opcode, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        regTo = target;
        regArgA = sourceA;
        regArgB = sourceB;
        insnOpcode = opcode;

        // checks that the opcode is allowed as well
        insnOpcode.checkRegisterType(target, Arg.RESULT);
        insnOpcode.checkRegisterType(sourceA, Arg.SOURCE_1);
        insnOpcode.checkRegisterType(sourceB, Arg.SOURCE_2);
    }

    public DexInstruction_BinaryOp(DexSingleRegister target, DexSingleRegister sourceA, DexSingleRegister sourceB, Opcode_BinaryOp opcode, RuntimeHierarchy hierarchy) {
        this((DexRegister) target, sourceA, sourceB, opcode, hierarchy);
    }

    public DexInstruction_BinaryOp(DexWideRegister target, DexWideRegister sourceA, DexWideRegister sourceB, Opcode_BinaryOp opcode, RuntimeHierarchy hierarchy) {
        this((DexRegister) target, sourceA, sourceB, opcode, hierarchy);
    }

    public static DexInstruction_BinaryOp parse(Instruction insn, CodeParserState parsingState) {
        val opcode = Opcode_BinaryOp.convert(insn.opcode);
        int regA, regB, regC;

        if (insn instanceof Instruction23x && opcode != null) {

            val insnBinaryOp = (Instruction23x) insn;
            regA = insnBinaryOp.getRegisterA();
            regB = insnBinaryOp.getRegisterB();
            regC = insnBinaryOp.getRegisterC();

        } else if (insn instanceof Instruction12x && opcode != null) {

            val insnBinaryOp2addr = (Instruction12x) insn;
            regA = regB = insnBinaryOp2addr.getRegisterA();
            regC = insnBinaryOp2addr.getRegisterB();

        } else
            throw FORMAT_EXCEPTION;

        DexStandardRegister sregA, sregB, sregC;

        if (opcode.getWidthResult() == RegisterWidth.SINGLE)
            sregA = parsingState.getSingleRegister(regA);
        else
            sregA = parsingState.getWideRegister(regA);

        if (opcode.getWidthArgA() == RegisterWidth.SINGLE)
            sregB = parsingState.getSingleRegister(regB);
        else
            sregB = parsingState.getWideRegister(regB);

        if (opcode.getWidthArgB() == RegisterWidth.SINGLE)
            sregC = parsingState.getSingleRegister(regC);
        else
            sregC = parsingState.getWideRegister(regC);

        return new DexInstruction_BinaryOp(
                   sregA, sregB, sregC,
                   opcode,
                   parsingState.getHierarchy());
    }

    @Override
    public String toString() {
        return insnOpcode.getAssemblyName() + " " + regTo.toString() + ", " + regArgA.toString() + ", " + regArgB.toString();
    }

    @Override
    public Set<? extends DexRegister> lvaDefinedRegisters() {
        return Sets.newHashSet(regTo);
    }

    @Override
    public Set<? extends DexRegister> lvaReferencedRegisters() {
        return Sets.newHashSet(regArgA, regArgB);
    }

    @Override
    public void accept(DexInstructionVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isDividing() {
        return
            insnOpcode == Opcode_BinaryOp.DivInt || insnOpcode == Opcode_BinaryOp.RemInt ||
            insnOpcode == Opcode_BinaryOp.DivLong || insnOpcode == Opcode_BinaryOp.RemLong;
    }

    @Override
    protected DexClassType[] throwsExceptions() {
        if (isDividing())
            return this.hierarchy.getTypeCache().LIST_Error_ArithmeticException;
        else
            return null;
    }
}
