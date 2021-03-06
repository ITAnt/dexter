package uk.ac.cam.db538.dexter.hierarchy;

import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_Invoke;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;

public class UnresolvedInterfaceDefinition extends InterfaceDefinition {

    private static final long serialVersionUID = 1L;

    public UnresolvedInterfaceDefinition(DexClassType type) {
        super(type, AccessFlags.INTERFACE.getValue(), false);
    }

    public CallDestinationType getMethodDestinationType(DexMethodId methodId, Opcode_Invoke opcode) {
        return CallDestinationType.External;
    }    

	@Override
	public void checkUsedAs(BaseClassDefinition refType) {
		try {
			super.checkUsedAs(refType);
		} catch (Throwable t) {
			refineSuperclassLink(refType);
		}
	}    
}
