package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.SolveMode;
import org.potassco.jna.Part;
import org.potassco.jna.Size;

import com.sun.jna.Pointer;

public class MultipleModelsTest {

	@Test
	public void test() {
		String name = "base";
		String program = "1 {p(1..3)} 2.";
		Clingo clingo = new Clingo();
		String[] arguments = { "0" }; // enumerate all models
		Pointer control = clingo.control(arguments);
		clingo.controlAdd(control, name, null, program);
		Part[] parts = new Part[1];
		parts[0] = new Part(name, null, new Size(0));
		clingo.controlGround(control, parts, new Size(1), null, null);
		Pointer handle = clingo.controlSolve(control, SolveMode.YIELD, null, 0, null, null);
		boolean modelExits = true;
		int i = 0;
		while (modelExits) {
			Pointer model = clingo.solveHandleModel(handle);
			if (model != null) {
				long mn = clingo.modelNumber(model);
				clingo.solveHandleResume(handle);
				i++;
			} else {
				modelExits = false;
			}
		}
		assertEquals(6, i);
	}

}
