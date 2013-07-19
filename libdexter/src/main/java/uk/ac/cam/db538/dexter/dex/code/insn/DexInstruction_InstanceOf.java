package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22c;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_InstanceOf extends DexInstruction {
	private final DexSingleRegister regTo;
	private final DexSingleRegister regObject;
	private final DexReferenceType value;
	
	public DexInstruction_InstanceOf(DexSingleRegister to, DexSingleRegister object, DexReferenceType value, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regTo = to;
		this.regObject = object;
		this.value = value;
	}
	
	public static DexInstruction_InstanceOf parse(Instruction insn, CodeParserState parsingState) {
		if (insn instanceof Instruction22c && insn.opcode == Opcode.INSTANCE_OF) {
			final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy hierarchy = parsingState.getHierarchy();
			final org.jf.dexlib.Code.Format.Instruction22c insnInstanceOf = (Instruction22c)insn;
			return new DexInstruction_InstanceOf(parsingState.getSingleRegister(insnInstanceOf.getRegisterA()), parsingState.getSingleRegister(insnInstanceOf.getRegisterB()), DexReferenceType.parse(((TypeIdItem)insnInstanceOf.getReferencedItem()).getTypeDescriptor(), hierarchy.getTypeCache()), hierarchy);
		} else throw FORMAT_EXCEPTION;
	}
	
	@Override
	public String toString() {
		return "instance-of " + regTo.toString() + ", " + regObject.toString() + ", " + value.getDescriptor();
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTo);
	}
	
	@Override
	public Set<? extends DexRegister> lvaReferencedRegisters() {
		return Sets.newHashSet(regObject);
	}
	
	@Override
	public void instrument() {
//    // copy the taint across
//    val code = getMethodCode();
//    code.replace(this,
//                 new DexCodeElement[] {
//                   new DexMacro_GetObjectTaint(code, state.getTaintRegister(regTo), regObject),
//                   this
//                 });
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	protected DexClassType[] throwsExceptions() {
		return this.hierarchy.getTypeCache().LIST_Error;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexSingleRegister getRegTo() {
		return this.regTo;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexSingleRegister getRegObject() {
		return this.regObject;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexReferenceType getValue() {
		return this.value;
	}
}