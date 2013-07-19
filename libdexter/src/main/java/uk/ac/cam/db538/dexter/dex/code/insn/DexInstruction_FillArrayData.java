package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction;
import org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction.ArrayElement;
import org.jf.dexlib.Code.Format.Instruction31t;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.utils.Utils;
import com.google.common.collect.Sets;

public class DexInstruction_FillArrayData extends DexInstruction {
	private final DexSingleRegister regArray;
	private final List<byte[]> elementData;
	
	public DexInstruction_FillArrayData(DexSingleRegister array, List<byte[]> elementData, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regArray = array;
		this.elementData = Utils.finalList(elementData);
	}
	
	public static DexInstruction_FillArrayData parse(Instruction insn, CodeParserState parsingState) {
		if (insn instanceof Instruction31t && insn.opcode == Opcode.FILL_ARRAY_DATA) {
			final org.jf.dexlib.Code.Format.Instruction31t insnFillArrayData = (Instruction31t)insn;
			// find the target pseudo instruction containing the data
			final org.jf.dexlib.Code.Instruction insnTarget = parsingState.getDexlibInstructionAt(insnFillArrayData.getTargetAddressOffset(), insnFillArrayData);
			if (!(insnTarget instanceof ArrayDataPseudoInstruction)) throw FORMAT_EXCEPTION;
			final org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction insnDataTable = (ArrayDataPseudoInstruction)insnTarget;
			// parse array register
			final uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister regArray = parsingState.getSingleRegister(insnFillArrayData.getRegisterA());
			// parse the data table 
			final java.util.ArrayList<byte[]> elementData = new ArrayList<byte[]>(insnDataTable.getElementCount());
			for (Iterator<ArrayElement> arrayIter = insnDataTable.getElements(); arrayIter.hasNext(); ) {
				final org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction.ArrayElement current = arrayIter.next();
				final byte[] currentData = new byte[current.elementWidth];
				System.arraycopy(current.buffer, current.bufferIndex, currentData, 0, current.elementWidth);
				elementData.add(currentData);
			}
			// return
			return new DexInstruction_FillArrayData(regArray, elementData, parsingState.getHierarchy());
		} else throw FORMAT_EXCEPTION;
	}
	
	@Override
	public String toString() {
		return "fill-array-data " + regArray.toString() + ", <data>";
	}
	
	@Override
	public void instrument() {
	}
	
	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		return Sets.newHashSet(regArray);
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@java.lang.SuppressWarnings("all")
	public DexSingleRegister getRegArray() {
		return this.regArray;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<byte[]> getElementData() {
		return this.elementData;
	}
}