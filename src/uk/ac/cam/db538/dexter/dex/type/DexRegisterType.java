package uk.ac.cam.db538.dexter.dex.type;


public abstract class DexRegisterType extends DexType {

	private static final long serialVersionUID = 1L;

	public static enum DexRegisterTypeSize {
		SINGLE,
		WIDE;

		public int getRegisterCount() {
			switch (this) {
			case SINGLE:
				return 1;
			case WIDE:
				return 2;
			}
			throw new RuntimeException("Unknown register size");
		}
	}
  
	protected DexRegisterType() { }

	public static DexRegisterType parse(String typeDescriptor, DexTypeCache cache) throws UnknownTypeException {
		try {
			return DexPrimitiveType.parse(typeDescriptor, cache);
		} catch (UnknownTypeException e) {
		}

		try {
			return DexReferenceType.parse(typeDescriptor, cache);
		} catch (UnknownTypeException e) {
		}

		throw new UnknownTypeException(typeDescriptor);
	}
  
	public abstract DexRegisterTypeSize getTypeSize();

	public boolean isWide() {
		return getTypeSize() == DexRegisterTypeSize.WIDE;
	}

	public int getRegisters() {
		return getTypeSize().getRegisterCount();
	}
}
