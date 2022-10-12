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

import java.util.ArrayList;

public class TestBindingEvaluatorWithTypes extends EvaluatorTestCase {

	public void testInstanceof() throws ClassNotFoundException {
		TestObject object = new TestObject();
		genericTest("object instanceof org.openflexo.connie.java.TestBindingEvaluatorWithTypes$TestObject", object, true);
	}

	public void testNewInstance() throws ClassNotFoundException {
		staticGenericTest("new org.openflexo.connie.java.TestBindingEvaluatorWithTypes$TestObject()", new TestObject());
	}

	public void testNewFullQualifiedArrayList() throws ClassNotFoundException {
		staticGenericTest("new java.util.ArrayList()", new ArrayList());
	}

	public void testNewArrayList() throws ClassNotFoundException {
		staticGenericTest("new ArrayList()", new ArrayList());
	}

	public void testNewArrayList2() throws ClassNotFoundException {
		staticGenericTest("new ArrayList<String>().size", 0);
	}

	public void testNewStaticInnerClassInstance() throws ClassNotFoundException {
		staticGenericTest("new org.openflexo.connie.java.TestBindingEvaluatorWithTypes$TestObject$StaticInnerClass().toString()",
				"StaticInnerClass in TestObject");
	}

	public void testNewInnerClassInstance() throws ClassNotFoundException {
		TestObject object = new TestObject();
		genericTest("object.new org.openflexo.connie.java.TestBindingEvaluatorWithTypes$TestObject$InnerClass().toString()", object,
				"TestObject/InnerClass in TestObject");
	}

	public void testNewInnerClassInstance2() throws ClassNotFoundException {
		TestObject2 object = new TestObject2(7);
		genericTest("object.new org.openflexo.connie.java.TestBindingEvaluatorWithTypes$TestObject2$InnerClass2(9).toString()", object,
				"InnerClass2/63");
	}

	public static class TestObject {

		private boolean foo;

		public void method() {
			System.out.println("Called method");
		}

		public static class StaticInnerClass {

			@Override
			public String toString() {
				return "StaticInnerClass in TestObject";
			}
		}

		public class InnerClass {

			public InnerClass() {
				// TODO Auto-generated constructor stub
				System.out.println("Passe-t-on la ???");
				Thread.dumpStack();
			}

			@Override
			public String toString() {
				return TestObject.this.toString() + "/InnerClass in TestObject";
			}
		}

		@Override
		public String toString() {
			return "TestObject";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (foo ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestObject other = (TestObject) obj;
			if (foo != other.foo)
				return false;
			return true;
		}

	}

	public static class TestObject2 {

		private int value;

		public TestObject2(int aValue) {
			this.value = aValue;
		}

		@Override
		public String toString() {
			return "TestObject2/" + value;
		}

		public class InnerClass2 {

			private int innerValue;

			public InnerClass2(int anInnerValue) {
				this.innerValue = anInnerValue;
			}

			@Override
			public String toString() {
				return "InnerClass2/" + innerValue * value;
			}
		}

	}

}
