package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public abstract class DexInstruction extends DexCodeElement {
	protected final RuntimeHierarchy hierarchy;
	
	protected DexInstruction(RuntimeHierarchy hierarchy) {
		
		this.hierarchy = hierarchy;
	}
	// PARSING
	protected static final InstructionParseError FORMAT_EXCEPTION = new InstructionParseError("Unknown instruction format or opcode");
	// INSTRUCTION INSTRUMENTATION
	public void instrument() {
		throw new UnsupportedOperationException("Instruction " + this.getClass().getSimpleName() + " doesn\'t have instrumentation implemented");
	}
	// THROWING INSTRUCTIONS
	@Override
	public final boolean cfgExitsMethod(InstructionList code) {
		final java.util.Set<? extends uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement> jumpTargets = this.cfgJumpTargets(code);
		if (jumpTargets.isEmpty()) return true;
		final uk.ac.cam.db538.dexter.dex.type.DexClassType[] exceptions = this.throwsExceptions();
		if (exceptions != null && exceptions.length > 0) // assume every exception can jump out (doesn't really matter)
		 return true;
		return false;
	}
	
	public abstract void accept(DexInstructionVisitor visitor);
	// Subclasses should overrides this if the instruction may throw exceptions during execution.
	protected DexClassType[] throwsExceptions() {
		return null;
	}
	
	@Override
	public boolean cfgEndsBasicBlock() {
		DexClassType[] exceptions = throwsExceptions();
		return exceptions != null && exceptions.length > 0;
	}
	
	@Override
	public final Set<DexCodeElement> cfgGetSuccessors(InstructionList code) {
		// uses the DexCodeElement definition of cfgGetSuccessors
		// (non-throwing semantics) but adds behavior after exceptions
		// are thrown
		final java.util.Set<uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement> set = super.cfgGetSuccessors(code);
		set.addAll(cfgGetExceptionSuccessors(code));
		return set;
	}
	
	@Override
	public final List<? extends DexCodeElement> cfgGetExceptionSuccessors(InstructionList code) {
		final java.util.ArrayList<uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement> list = new ArrayList<DexCodeElement>();
		DexClassType[] exceptions = throwsExceptions();
		if (exceptions != null) {
			//for(DexClassType exception : exceptions)
			//  set.addAll(throwingInsn_CatchHandlers(exception));
			// Instead of finding out precisely which catch handlers may 
			// catch the thrown exceptions by the instruction, we assume 
			// that it is possible for every catch handlers to catch all 
			// throwing instructions. This is consistent with what dx is doing.
			// Also, as an example, the following is VALID generated dalvik code, 
			// but would not be handled correctly (*sigh*) if we track the 
			// exception catchers more precisely 
			// try {
			//     const-string xxx (will only throw java.lang.Error)
			// } catch (java.lang.Exception) {
			//    // Dead code if we analyze it precisely 
			// }
			list.addAll(getTryBlockCatchHandlers(getSurroundingTryBlock(code)));
		}
		return list;
	}
	
	private DexTryStart getSurroundingTryBlock(InstructionList code) {
		final int index = code.getIndex(this);
		final java.util.List<uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart> tryStarts = code.getAllTryBlocks();
		for (final uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart tryStart : tryStarts) if (code.isBetween(tryStart, tryStart.getEndMarker(), index)) return tryStart;
		return null;
	}
	
	private List<? extends DexCodeElement> getTryBlockCatchHandlers(DexTryStart tryBlock) {
		if (tryBlock == null) return Collections.emptyList();
		final java.util.ArrayList<uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement> list = new ArrayList<DexCodeElement>();
		list.addAll(tryBlock.getCatchHandlers());
		final uk.ac.cam.db538.dexter.dex.code.elem.DexCatchAll catchAll = tryBlock.getCatchAllHandler();
		if (catchAll != null) list.add(catchAll);
		return list;
	}
	
	static boolean fitsIntoBits_Signed(long value, int bits) {
		assert bits > 0;
		assert bits <= 64;
		long upperBound = 1L << bits - 1;
		return (value < upperBound) && (value >= -upperBound);
	}
}