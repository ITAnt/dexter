package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11x;
import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.Utils;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyTest;

public class DexInstruction_MoveException_Test extends HierarchyTest {

    @Test
    public void testParse() {
        Utils.parseAndCompare(new Instruction11x(Opcode.MOVE_EXCEPTION, (short) 255),
                              "move-exception v255",
                              this.hierarchy);
    }

}
