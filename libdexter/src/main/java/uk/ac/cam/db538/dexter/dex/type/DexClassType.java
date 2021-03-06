package uk.ac.cam.db538.dexter.dex.type;

import lombok.val;

public class DexClassType extends DexReferenceType {

    private static final long serialVersionUID = 1L;

    private final String descriptor;

    public DexClassType(String descriptor) {
        this.descriptor = descriptor;
    }

    public static boolean isClassDescriptor(String typeDescriptor) {
        return typeDescriptor.startsWith("L") && typeDescriptor.endsWith(";");
    }

    public static DexClassType parse(String typeDescriptor, DexTypeCache cache) {
        if (!isClassDescriptor(typeDescriptor))
            throw new UnknownTypeException(typeDescriptor);

        val classRenamer = cache.getClassRenamer();
        if (classRenamer != null)
            typeDescriptor = classRenamer.applyRules(typeDescriptor);

        DexClassType type = cache.getCachedType_Class(typeDescriptor);
        if (type == null) {
            type = new DexClassType(typeDescriptor);
            cache.putCachedType_Class(typeDescriptor, type);
        }

        return type;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getPrettyName() {
        return descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
    }

    public String getShortName() {
        val prettyName = getPrettyName();
        int lastDot = prettyName.lastIndexOf('.');
        if (lastDot == -1)
            return prettyName;
        else
            return prettyName.substring(lastDot + 1);
    }

    public String getPackageName() {
        val prettyName = getPrettyName();
        int lastDot = prettyName.lastIndexOf('.');
        if (lastDot == -1)
            return null;
        else
            return prettyName.substring(0, lastDot);
    }

    public static String jvm2dalvik(String jvmName) {
        if (jvmName.startsWith("L") && jvmName.endsWith(";"))
            return jvmName.replace('.', '/');
        else
            return "L" + jvmName.replace('.', '/') + ";";
    }
}
