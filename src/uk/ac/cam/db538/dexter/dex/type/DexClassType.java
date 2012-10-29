package uk.ac.cam.db538.dexter.dex.type;

import uk.ac.cam.db538.dexter.dex.DexParsingCache;
import lombok.Getter;
import lombok.val;

public class DexClassType extends DexReferenceType {

  @Getter	private final String ShortName;
  @Getter	private final String PackageName;

  public DexClassType(String descriptor) {
    super(descriptor, descriptor.substring(1, descriptor.length() - 1).replace('/', '.'), 1);

    val prettyName = getPrettyName();
    int lastDot = prettyName.lastIndexOf('.');
    if (lastDot == -1) {
      ShortName = prettyName;
      PackageName = null;
    } else {
      ShortName = prettyName.substring(lastDot + 1);
      PackageName = prettyName.substring(0, lastDot);
    }
  }

  public static DexClassType parse(String typeDescriptor, DexParsingCache cache) throws UnknownTypeException {
    if (!typeDescriptor.startsWith("L") || !typeDescriptor.endsWith(";"))
      throw new UnknownTypeException(typeDescriptor);

    if (cache != null) {
      val res = cache.getClassTypes().get(typeDescriptor);
      if (res != null)
        return res;
    }

    val newType = new DexClassType(typeDescriptor);
    if (cache != null)
      cache.getClassTypes().put(typeDescriptor, newType);
    return newType;
  }

}
