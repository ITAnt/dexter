// Generated by delombok at Fri Jul 19 13:12:59 UTC 2013
package uk.ac.cam.db538.dexter.dex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jf.dexlib.CodeItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.Util.ByteArrayAnnotatedOutput;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class Dex {
	final RuntimeHierarchy hierarchy;
	final AuxiliaryDex auxiliaryDex;
	private final Set<DexClass> _classes;
	private final Set<DexClass> classes;
	
//  @Getter private DexClass externalStaticFieldTaint_Class;
//  @Getter private DexMethodWithCode externalStaticFieldTaint_Clinit;
	/*
   * Creates an empty Dex
   */
	public Dex(RuntimeHierarchy hierarchy, AuxiliaryDex dexAux) {
		
		this.hierarchy = hierarchy;
		this.auxiliaryDex = dexAux;
		this._classes = new HashSet<DexClass>();
		this.classes = Collections.unmodifiableSet(this._classes);
	}
	/*
   * Creates a new Dex and parses all classes inside the given DexFile
   */
	public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux) {
		this(hierarchy, dexAux);
		this._classes.addAll(parseAllClasses(dex));
	}
	/*
   * This constructor applies a descriptor renamer on the classes parsed from given DexFile
   */
	public Dex(DexFile dex, RuntimeHierarchy hierarchy, AuxiliaryDex dexAux, ClassRenamer renamer) {
		this(dex, setRenamer(hierarchy, renamer), dexAux);
		unsetRenamer(hierarchy);
	}
	
	private static RuntimeHierarchy setRenamer(RuntimeHierarchy hierarchy, ClassRenamer renamer) {
		hierarchy.getTypeCache().setClassRenamer(renamer);
		return hierarchy;
	}
	
	private static void unsetRenamer(RuntimeHierarchy hierarchy) {
		hierarchy.getTypeCache().setClassRenamer(null);
	}
	
	public DexTypeCache getTypeCache() {
		return hierarchy.getTypeCache();
	}
	
	private List<DexClass> parseAllClasses(DexFile file) {
		final java.util.List<org.jf.dexlib.ClassDefItem> dexClsInfos = file.ClassDefsSection.getItems();
		final java.util.ArrayList<uk.ac.cam.db538.dexter.dex.DexClass> classList = new ArrayList<DexClass>(dexClsInfos.size());
		for (final org.jf.dexlib.ClassDefItem dexClsInfo : dexClsInfos) classList.add(new DexClass(this, dexClsInfo));
		return classList;
	}
	
//  private List<DexClass> generateExtraClasses() {
//    val parsingCache = getTypeCache();
//
//    externalStaticFieldTaint_Class = new DexClass(
//      this,
//      generateClassType(),
//      DexClassType.parse("Ljava/lang/Object;", parsingCache),
//      EnumSet.of(AccessFlags.PUBLIC),
//      null,
//      null,
//      null,
//      null);
//
//    val clinitCode = new DexCode();
//    clinitCode.add(new DexInstruction_ReturnVoid(clinitCode));
//
//    externalStaticFieldTaint_Clinit = new DexDirectMethod(
//      externalStaticFieldTaint_Class,
//      "<clinit>",
//      EnumSet.of(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR),
//      new DexPrototype(DexVoid.parse("V", parsingCache), null),
//      clinitCode,
//      null, null);
//    externalStaticFieldTaint_Class.addMethod(externalStaticFieldTaint_Clinit);
//
//    return Arrays.asList(new DexClass[] { externalStaticFieldTaint_Class });
//  }
	public void instrument(boolean debug) {
//    val cache = new DexInstrumentationCache(this, debug);
//
//    val extraClassesLinked = parseExtraClasses();
//    val extraClassesGenerated = generateExtraClasses();
//
//    for (val cls : classes)
//      cls.instrument(cache);
//
//    classes.addAll(extraClassesLinked);
//    classes.addAll(extraClassesGenerated);
//
//    return cache.getWarnings();
	}
	
	public byte[] writeToFile() {
		final org.jf.dexlib.DexFile outFile = new DexFile();
		final org.jf.dexlib.Util.ByteArrayAnnotatedOutput out = new ByteArrayAnnotatedOutput();
		final uk.ac.cam.db538.dexter.dex.DexAssemblingCache asmCache = new DexAssemblingCache(outFile, hierarchy);
		for (final uk.ac.cam.db538.dexter.dex.DexClass cls : classes) cls.writeToFile(outFile, asmCache);
		// Apply jumbo-instruction fix requires ReferencedItem being 
		// placed first, after which the code needs to be placed again
		// because jumbo instruction is wider.
		// The second pass shoudn't change ReferencedItem's placement 
		// (because they are ordered deterministically by its content)
		// otherwise we'll be in trouble.
		outFile.place();
		fixInstructions(outFile);
		outFile.place();
		outFile.writeTo(out);
		final byte[] bytes = out.toByteArray();
		DexFile.calcSignature(bytes);
		DexFile.calcChecksum(bytes);
		return bytes;
	}
	
	private void fixInstructions(DexFile outFile) {
		for (CodeItem codeItem : outFile.CodeItemsSection.getItems()) {
			codeItem.fixInstructions(true, true);
		}
	}
	
	@java.lang.SuppressWarnings("all")
	public RuntimeHierarchy getHierarchy() {
		return this.hierarchy;
	}
	
	@java.lang.SuppressWarnings("all")
	public AuxiliaryDex getAuxiliaryDex() {
		return this.auxiliaryDex;
	}
	
	@java.lang.SuppressWarnings("all")
	public Set<DexClass> getClasses() {
		return this.classes;
	}
}