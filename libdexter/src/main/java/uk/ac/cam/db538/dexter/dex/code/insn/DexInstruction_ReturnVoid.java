package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Collections;
import java.util.Set;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction10x;

import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class DexInstruction_ReturnVoid extends DexInstruction {

    public DexInstruction_ReturnVoid(RuntimeHierarchy hierarchy) {
        super(hierarchy);
    }

    public static DexInstruction_ReturnVoid parse(Instruction insn, CodeParserState parsingState) {
        if (insn instanceof Instruction10x && insn.opcode == Opcode.RETURN_VOID)
            return new DexInstruction_ReturnVoid(parsingState.getHierarchy());
        else
            throw FORMAT_EXCEPTION;
    }

    @Override
    public String toString() {
        return "return-void";
    }

    @Override
    public Set<? extends DexCodeElement> cfgJumpTargets(InstructionList code) {
        return Collections.emptySet();
    }

    @Override
    public void accept(DexInstructionVisitor visitor) {
        visitor.visit(this);
    }
}
