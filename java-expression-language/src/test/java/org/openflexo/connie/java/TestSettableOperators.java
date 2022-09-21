/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.connie.java;

public class TestSettableOperators extends EvaluatorTestCase {

	public void testPreInc() {
		TestObject object = new TestObject(42);
		genericTest("++object.value", object, 43);
		assertEquals(43, object.value);
	}

	public void testPreDec() {
		TestObject object = new TestObject(42);
		genericTest("--object.value", object, 41);
		assertEquals(41, object.value);
	}

	public void testPostInc() {
		TestObject object = new TestObject(42);
		genericTest("object.value++", object, 42);
		assertEquals(43, object.value);
	}

	public void testPostDec() {
		TestObject object = new TestObject(42);
		genericTest("object.value--", object, 42);
		assertEquals(41, object.value);
	}

	public static class TestObject {

		private int value = 0;

		public TestObject(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int aValue) {
			System.out.println("sets value with " + aValue);
			value = aValue;
		}

	}
}
