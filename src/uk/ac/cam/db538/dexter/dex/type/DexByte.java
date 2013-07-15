package uk.ac.cam.db538.dexter.dex.type;

import uk.ac.cam.db538.dexter.dex.code.reg.RegisterWidth;
import uk.ac.cam.db538.dexter.utils.Pair;

public class DexByte extends DexPrimitiveType {
    
	private static final long serialVersionUID = 1L;

	private static String DESCRIPTOR = "B";
	private static String NAME = "byte";
	
	DexByte() { }
    
    @Override
	public RegisterWidth getTypeWidth() {
    	return RegisterWidth.SINGLE;
	}

	@Override
	public String getDescriptor() {
		return DESCRIPTOR;
	}

	@Override
	public String getPrettyName() {
		return NAME;
	}

	@Override
    public Pair<DexClassType, String> getPrimitiveClassConstantField(DexTypeCache cache) {
      return new Pair<DexClassType, String>(DexClassType.parse("Ljava/lang/Byte;", cache), "TYPE");
    }

    public static DexByte parse(String typeDescriptor, DexTypeCache cache) {
    	if (!typeDescriptor.equals(DESCRIPTOR))
    		throw new UnknownTypeException(typeDescriptor);
    	else
    		return cache.getCachedType_Byte();
    }
    
	public static String jvm2dalvik(String javaName) {
		if (javaName.equals(NAME))
			return DESCRIPTOR;
		else
			throw new UnknownTypeException(javaName);
	}
}