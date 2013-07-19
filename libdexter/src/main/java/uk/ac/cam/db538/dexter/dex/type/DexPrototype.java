// Generated by delombok at Fri Jul 19 13:12:59 UTC 2013
package uk.ac.cam.db538.dexter.dex.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.ProtoIdItem;
import uk.ac.cam.db538.dexter.dex.DexAssemblingCache;
import uk.ac.cam.db538.dexter.utils.Cache;
import uk.ac.cam.db538.dexter.utils.Utils;

public class DexPrototype implements Serializable {
	private static final long serialVersionUID = 1L;
	private final DexType returnType;
	private final List<DexRegisterType> parameterTypes;
	private final int hashcode;
	
	public DexPrototype(DexType returnType, List<DexRegisterType> argTypes) {
		
		this.returnType = returnType;
		this.parameterTypes = Utils.finalList(argTypes);
		// precompute hashcode
		int result = 31 + parameterTypes.hashCode();
		result = 31 * result + returnType.hashCode();
		this.hashcode = result;
	}
	
	public static DexPrototype parse(DexType returnType, List<DexRegisterType> argumentTypes, DexTypeCache cache) {
		final uk.ac.cam.db538.dexter.dex.type.DexPrototype proto = new DexPrototype(returnType, argumentTypes);
		return cache.getCachedPrototype(proto); // will return 'proto' if not cached
	}
	
	public static DexPrototype parse(ProtoIdItem proto, DexTypeCache cache) {
		return parse(parseReturnType(proto, cache), parseArgumentTypes(proto, cache), cache);
	}
	
	private static DexType parseReturnType(ProtoIdItem proto, DexTypeCache typeCache) {
		final org.jf.dexlib.TypeIdItem item = proto.getReturnType();
		return DexType.parse(item.getTypeDescriptor(), typeCache);
	}
	
	private static List<DexRegisterType> parseArgumentTypes(ProtoIdItem proto, DexTypeCache typeCache) {
		final org.jf.dexlib.TypeListItem params = proto.getParameters();
		if (params != null) {
			final java.util.ArrayList<uk.ac.cam.db538.dexter.dex.type.DexRegisterType> list = new ArrayList<DexRegisterType>(params.getTypeCount());
			for (final org.jf.dexlib.TypeIdItem type : params.getTypes()) list.add(DexRegisterType.parse(type.getTypeDescriptor(), typeCache));
			return list;
		}
		return Collections.emptyList();
	}
	
	public int countParamWords(boolean isStatic) {
		int totalWords = 0;
		if (!isStatic) totalWords += DexClassType.TypeSize.getRegisterCount();
		for (final uk.ac.cam.db538.dexter.dex.type.DexRegisterType param : parameterTypes) totalWords += param.getRegisters();
		return totalWords;
	}
	
	public int getParameterCount(boolean isStatic) {
		return parameterTypes.size() + (isStatic ? 0 : 1);
	}
	
	public DexRegisterType getParameterType(int paramId, boolean isStatic, DexReferenceType clazz) {
		if (!isStatic) {
			if (paramId == 0) return clazz; else paramId--;
		}
		return parameterTypes.get(paramId);
	}
	
	public boolean hasPrimitiveArgument() {
		for (final uk.ac.cam.db538.dexter.dex.type.DexRegisterType paramType : parameterTypes) if (paramType instanceof DexPrimitiveType) return true;
		return false;
	}
	
	public String getDescriptor() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (final uk.ac.cam.db538.dexter.dex.type.DexRegisterType parameter : parameterTypes) {
			sb.append(parameter.getDescriptor());
		}
		sb.append(")");
		sb.append(returnType.getDescriptor());
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getDescriptor();
	}
	
	@Override
	public int hashCode() {
		return hashcode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof DexPrototype)) return false;
		DexPrototype other = (DexPrototype)obj;
		return this.returnType.equals(other.returnType) && this.parameterTypes.equals(other.parameterTypes);
	}
	
	public static Cache<DexPrototype, ProtoIdItem> createAssemblingCache(final DexAssemblingCache cache, final DexFile outFile) {
		return new Cache<DexPrototype, ProtoIdItem>(){
			
			
			@Override
			protected ProtoIdItem createNewEntry(DexPrototype prototype) {
				return ProtoIdItem.internProtoIdItem(outFile, cache.getType(prototype.returnType), cache.getTypeList(prototype.parameterTypes));
			}
		};
	}
	
	@java.lang.SuppressWarnings("all")
	public DexType getReturnType() {
		return this.returnType;
	}
}