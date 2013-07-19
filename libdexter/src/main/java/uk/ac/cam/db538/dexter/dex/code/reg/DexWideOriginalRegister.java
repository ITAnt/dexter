// Generated by delombok at Fri Jul 19 13:12:59 UTC 2013
package uk.ac.cam.db538.dexter.dex.code.reg;

public class DexWideOriginalRegister extends DexWideRegister {
	private final int id;
	private final DexTaintRegister taintRegister;
	
	public DexWideOriginalRegister(int id) {
		
		this.id = id;
		this.taintRegister = new DexTaintRegister(this);
	}
	
	@Override
	String getPlainId() {
		return Integer.toString(id) + "|" + Integer.toString(id + 1);
	}
	
	@Override
	public DexTaintRegister getTaintRegister() {
		return this.taintRegister;
	}
	
	@java.lang.SuppressWarnings("all")
	public int getId() {
		return this.id;
	}
}