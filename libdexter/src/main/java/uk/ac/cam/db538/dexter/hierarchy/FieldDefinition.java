package uk.ac.cam.db538.dexter.hierarchy;

import java.io.Serializable;
import java.util.EnumSet;
import org.jf.dexlib.Util.AccessFlags;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;

public abstract class FieldDefinition implements Serializable {
	private static final long serialVersionUID = 1L;
	private final BaseClassDefinition parentClass;
	private final DexFieldId fieldId;
	private final int accessFlags;
	
	public FieldDefinition(BaseClassDefinition cls, DexFieldId fieldId, int accessFlags) {
		
		this.parentClass = cls;
		this.fieldId = fieldId;
		this.accessFlags = accessFlags;
	}
	
	public EnumSet<AccessFlags> getAccessFlags() {
		AccessFlags[] flags = AccessFlags.getAccessFlagsForField(accessFlags);
		if (flags.length == 0) return EnumSet.noneOf(AccessFlags.class); else return EnumSet.of(flags[0], flags);
	}
	
	public boolean isStatic() {
		return getAccessFlags().contains(AccessFlags.STATIC);
	}
	
	@Override
	public String toString() {
		return parentClass.getType().getDescriptor() + "->" + fieldId.getName() + ":" + fieldId.getType().getDescriptor();
	}
	
	@java.lang.SuppressWarnings("all")
	public BaseClassDefinition getParentClass() {
		return this.parentClass;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexFieldId getFieldId() {
		return this.fieldId;
	}
}