package uk.ac.cam.db538.dexter.dex;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.DexFile;

import uk.ac.cam.db538.dexter.aux.InternalClassAnnotation;
import uk.ac.cam.db538.dexter.aux.InternalMethodAnnotation;
import uk.ac.cam.db538.dexter.aux.MethodCallHelper;
import uk.ac.cam.db538.dexter.aux.SafeHashMap;
import uk.ac.cam.db538.dexter.aux.TaintConstants;
import uk.ac.cam.db538.dexter.dex.field.DexStaticField;
import uk.ac.cam.db538.dexter.dex.method.DexMethod;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.InterfaceDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class AuxiliaryDex extends Dex {

	@Getter private final DexMethod method_TaintGet; 
	@Getter private final DexMethod method_TaintSet; 

	@Getter private final DexMethod method_QueryTaint; 
	@Getter private final DexMethod method_ServiceTaint; 
	
	@Getter private final DexStaticField field_CallParamTaint;
	@Getter private final DexStaticField field_CallResultTaint;
	
	@Getter private final InterfaceDefinition anno_InternalClass;
	@Getter private final InterfaceDefinition anno_InternalMethod;
	
	public AuxiliaryDex(DexFile dexAux, RuntimeHierarchy hierarchy, ClassRenamer renamer) {
		super(dexAux, hierarchy, null, renamer);
		
		// ObjectTaintStorage class
		val clsObjTaint = getDexClass(hierarchy, renamer, CLASS_OBJTAINT);
		
		this.method_TaintGet = findStaticMethodByName(clsObjTaint, "get");
		this.method_TaintSet = findStaticMethodByName(clsObjTaint, "set");
		
		// TaintConstants class
		val clsTaintConsts = getDexClass(hierarchy, renamer, CLASS_TAINTCONSTANTS);
		
		this.method_QueryTaint = findStaticMethodByName(clsTaintConsts, "queryTaint");
		this.method_ServiceTaint = findStaticMethodByName(clsTaintConsts, "serviceTaint");
		
		// MethodCallHelper class
		val clsMethodCallHelper = getDexClass(hierarchy, renamer, CLASS_METHODCALLHELPER);
		
		this.field_CallParamTaint = findStaticFieldByName(clsMethodCallHelper, "ARGS");
		this.field_CallResultTaint = findStaticFieldByName(clsMethodCallHelper, "RES");
		
		// Annotations
		this.anno_InternalClass = getAnnoDef(hierarchy, renamer, CLASS_INTERNALCLASS);
		this.anno_InternalMethod = getAnnoDef(hierarchy, renamer, CLASS_INTERNALMETHOD);
	}
	
	private static DexMethod findStaticMethodByName(DexClass clsDef, String name) {
		for (val method : clsDef.getMethods())
			if (method.getMethodDef().getMethodId().getName().equals(name) &&
				method.getMethodDef().isStatic())
				return method;
		throw new Error("Failed to locate an auxiliary method");
	}
	
	private static DexStaticField findStaticFieldByName(DexClass clsDef, String name) {
		for (val field : clsDef.getStaticFields())
			if (field.getFieldDef().getFieldId().getName().equals(name))
				return field;
		throw new Error("Failed to locate an auxiliary static field");
	}

	private DexClass getDexClass(RuntimeHierarchy hierarchy, ClassRenamer classRenamer, String className) {
		val classDef = hierarchy.getClassDefinition(new DexClassType(classRenamer.applyRules(className)));
		for (val cls : this.getClasses())
			if (classDef.equals(cls.getClassDef()))
				return cls;
		throw new Error("Auxiliary class was not found");
	}
	
	private InterfaceDefinition getAnnoDef(RuntimeHierarchy hierarchy, ClassRenamer classRenamer, String className) {
		return hierarchy.getInterfaceDefinition(new DexClassType(classRenamer.applyRules(className)));
	}

	private static final String CLASS_OBJTAINT = 
			DexClassType.jvm2dalvik(SafeHashMap.class.getName());
	private static final String CLASS_METHODCALLHELPER = 
			DexClassType.jvm2dalvik(MethodCallHelper.class.getName());
	private static final String CLASS_INTERNALCLASS = 
			DexClassType.jvm2dalvik(InternalClassAnnotation.class.getName());
	private static final String CLASS_INTERNALMETHOD =
			DexClassType.jvm2dalvik(InternalMethodAnnotation.class.getName());
	private static final String CLASS_TAINTCONSTANTS =
			DexClassType.jvm2dalvik(TaintConstants.class.getName());
}
