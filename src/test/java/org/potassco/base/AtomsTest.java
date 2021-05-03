package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.base.Clingo;
import org.potassco.base.Control;
import org.potassco.enums.TermType;
import org.potassco.jna.SymbolCallbackT;

import com.sun.jna.Pointer;

public class AtomsTest {

	@Test
	public void testSymbolicAtoms() {
		String name = "base";
		Clingo clingo = Clingo.getInstance(); 
		Control control = clingo.control(name, "a. b. c. d. e.");
		control.ground(name);
		Pointer atoms = control.symbolicAtoms();
		assertEquals(5, clingo.symbolicAtomsSize(atoms));
//		Pointer signature = clingo.signatureCreate(c, 0, true);
		Pointer iteratorBegin = clingo.symbolicAtomsBegin(atoms, null);
		long symbol1 = clingo.symbolicAtomsSymbol(atoms, iteratorBegin);
		assertEquals("a", clingo.symbolName(symbol1));
		assertTrue(clingo.symbolicAtomsIsFact(atoms, iteratorBegin));
		Pointer iteratorNext = clingo.symbolicAtomsNext(atoms, iteratorBegin);
		Pointer iteratorNextNext = clingo.symbolicAtomsNext(atoms, iteratorNext);
		long symbol2 = clingo.symbolicAtomsSymbol(atoms, iteratorNextNext);
		assertEquals("c", clingo.symbolName(symbol2));
		Pointer iteratorEnd = clingo.symbolicAtomsEnd(atoms);
		String[] strArray = { "a", "b", "c", "d", "e" };
		int j = 0;
		for (Pointer i = clingo.symbolicAtomsBegin(atoms, null);
				!clingo.symbolicAtomsIteratorIsEqualTo(atoms, i, iteratorEnd);
				i = clingo.symbolicAtomsNext(atoms, i)) {
			long s = clingo.symbolicAtomsSymbol(atoms, i);
			assertEquals(strArray[j], clingo.symbolName(s));
			j++;
		}
//		long symbol3 = clingo.symbolicAtomsSymbol(atoms, iteratorEnd);
//		assertEquals("e", clingo.symbolName(symbol3));
//		Pointer iteratorFind = clingo.symbolicAtomsFind(atoms, symbol2);
		
		
	}
// TODO
// 	public long symbolicAtomsIsExternal(Pointer atoms, Pointer iterator) {
// 	public Pointer symbolicAtomsLiteral(Pointer atoms, Pointer iterator) {
// 	public long symbolicAtomsSignaturesSize(Pointer atoms) {
// 	public Pointer symbolicAtomsSignatures(Pointer atoms, long size) {
// 	public boolean symbolicAtomsIsValid(Pointer atoms, Pointer iterator) {
	
	@Test
	public void testCallback() {
		SymbolCallbackT sb = new SymbolCallbackT() {
			
			@Override
			public boolean call(Pointer symbols, Pointer symbolsSize, Pointer data) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	/**
	 * {@link https://github.com/potassco/clingo/blob/master/libpyclingo/clingo/tests/test_atoms.py}
	 */
	@Test
	public void testTheoryAtoms() {
		String name = "base";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control(name,
				"#theory test { "
				+ "    t { }; "
				+ "    &a/0 : t, head; "
				+ "    &b/0 : t, {=}, t, head "
				+ "}.");
		control.add(name, null, 0L, "{a; b}.");
		control.add(name, null, 0L, "&a { 1; 2,3: a,b }.");
		control.ground(name);
		Pointer theoryAtoms = control.theoryAtoms();
		assertEquals(1, clingo.theoryAtomsSize(theoryAtoms));
		assertEquals(TermType.SYMBOL, clingo.theoryAtomsTermType(theoryAtoms, 0));
		assertEquals(0, clingo.theoryAtomsTermNumber(theoryAtoms, 0));
		assertEquals("a", clingo.theoryAtomsTermName(theoryAtoms, 0));
		long[] args = clingo.theoryAtomsTermArguments(theoryAtoms, 0);
		assertEquals(null, args);
		long stringSize = clingo.theoryAtomsTermToStringSize(theoryAtoms, 0);
		assertEquals(2, stringSize);
		assertEquals("a", clingo.theoryAtomsTermToString(theoryAtoms, 0, stringSize).trim());
	}

	@Test
	public void testConstants() {
		String name = "base";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control(name,
				"#const n=6. "
				+ "like(1,2; 3,4). "
				+ "dislike(2,3; 1,3).");
		control.ground(name);
		String constName = "n";
		assertTrue(control.hasConst(constName));
		int symbol = control.getConst(constName);
		assertEquals(symbol, control.getConst(constName));
	}
}
