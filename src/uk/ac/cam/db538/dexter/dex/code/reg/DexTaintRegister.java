package uk.ac.cam.db538.dexter.dex.code.reg;

import lombok.Getter;

public class DexTaintRegister extends DexRegister {

	@Getter private final DexOriginalRegister originalRegister;
	
	public DexTaintRegister(DexOriginalRegister origReg) {
		this.originalRegister = origReg;
	}

	@Override
	public String toString() {
		return "t" + originalRegister.getPlainId();
	}

	@Override
	public DexTaintRegister getTaintRegister() {
		throw new Error("Cannot get the taint register of a taint register.");
	}

	@Override
	public RegisterWidth getWidth() {
		return RegisterWidth.SINGLE;
	}
}
