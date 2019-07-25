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

package org.openflexo.connie.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import org.openflexo.connie.BindingEvaluator;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.kvc.InvalidKeyValuePropertyException;

public class TestBindingEvaluator {

	public static void genericTest(String bindingPath, Object object, Object expectedResult) {

		System.out.println("Evaluate " + bindingPath);
		Object evaluatedResult = null;
		try {
			evaluatedResult = BindingEvaluator.evaluateBinding(bindingPath, object);
		} catch (InvalidKeyValuePropertyException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			fail();
		} catch (NullReferenceException e) {
			fail();
		} catch (InvocationTargetException e) {
			fail();
		}
		System.out.println("Evaluated as " + evaluatedResult);

		if (expectedResult instanceof Number) {
			if (evaluatedResult instanceof Number) {
				assertEquals(((Number) expectedResult).doubleValue(), ((Number) evaluatedResult).doubleValue());
			}
			else {
				fail("Evaluated value is not a number (expected: " + expectedResult + ") but " + evaluatedResult);
			}
		}
		else {
			assertEquals(expectedResult, evaluatedResult);
		}
	}

	@Test
	public void test1() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString", thisIsATest, thisIsATest);
	}

	@Test
	public void test2() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()", thisIsATest, thisIsATest);
	}

	@Test
	public void test3() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,8)", thisIsATest, "llo wo");
	}

	@Test
	public void test4() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,5*2-2)", thisIsATest, "llo wo");
	}

	@Test
	public void test5() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+toString()", thisIsATest, "Hello world, this is a testHello world, this is a test");
	}

	@Test
	public void test6() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+' hash='+object.hashCode()", thisIsATest, thisIsATest + " hash=" + thisIsATest.hashCode());
	}

	@Test
	public void test7() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(0,5)+' '+substring(23,27).toUpperCase()", thisIsATest, "Hello TEST");
	}

	@Test
	public void test8() {
		genericTest("object*2-7", 10, 13);
	}

	@Test
	public void test9() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(3,length()-2)+' hash='+hashCode()", thisIsATest, "lo world, this is a te hash=" + thisIsATest.hashCode());
	}

	@Test
	public void test10() {
		TestObject object = new TestObject();
		genericTest("object.getValue(object)", object, 0);
		genericTest("object.setValue(10,object)", object, null);
		genericTest("object.getValue(object)", object, 10);
		genericTest("object.setValue(object.getValue(object),object)", object, null);
	}

	@Test
	public void test11() {
		TestObject object = new TestObject();
		genericTest("object.setValue(object.getValue(object)+1,object)", object, null);
		genericTest("object.getValue(object)", object, 1);
	}

	@Test
	public void test12() {
		TestObject object = new TestObject();
		genericTest("object.method()", object, null);
	}

	public static class TestObject {

		private int value = 0;

		public int getValue(TestObject o) {
			return value;
		}

		public void setValue(int aValue, TestObject o) {
			System.out.println("sets value with " + aValue);
			value = aValue;
		}

		public void method() {
			System.out.println("Called method");
		}
	}
}
