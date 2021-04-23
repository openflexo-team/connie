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

public class TestBindingEvaluator extends EvaluatorTestCase {

	public void testString1() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString", thisIsATest, thisIsATest);
	}

	public void testString2() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()", thisIsATest, thisIsATest);
	}

	public void testString3() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,8)", thisIsATest, "llo wo");
	}

	public void testString4() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,5*2-2)", thisIsATest, "llo wo");
	}

	public void testString5() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+toString()", thisIsATest, "Hello world, this is a testHello world, this is a test");
	}

	public void testString6() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+\" hash=\"+object.hashCode()", thisIsATest, thisIsATest + " hash=" + thisIsATest.hashCode());
	}

	public void testString7() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(0,5)+' '+substring(23,27).toUpperCase()", thisIsATest, "Hello TEST");
	}

	public void testString8() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(3,length()-2)+\" hash=\"+hashCode()", thisIsATest, "lo world, this is a te hash=" + thisIsATest.hashCode());
	}

	public void testInteger() {
		genericTest("object*2-7", 10, 13);
	}

	public void testObjectManipulations1() {
		TestObject object = new TestObject();
		genericTest("object.getValue(object)", object, 0);
		genericTest("object.setValue(10,object)", object, null);
		genericTest("object.getValue(object)", object, 10);
		genericTest("object.setValue(object.getValue(object),object)", object, null);
	}

	public void testObjectManipulations2() {
		TestObject object = new TestObject();
		genericTest("object.setValue(object.getValue(object)+1,object)", object, null);
		genericTest("object.getValue(object)", object, 1);
	}

	public void testCallMethod() {
		TestObject object = new TestObject();
		genericTest("object.method()", object, null);
		assertTrue(object.methodWasCalled);
	}

	public static class TestObject {

		private int value = 0;
		public boolean methodWasCalled = false;

		public int getValue(TestObject o) {
			return value;
		}

		public void setValue(int aValue, TestObject o) {
			System.out.println("sets value with " + aValue);
			value = aValue;
		}

		public void method() {
			System.out.println("Called method");
			methodWasCalled = true;
		}

	}
}
