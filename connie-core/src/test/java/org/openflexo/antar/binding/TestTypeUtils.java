/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.antar.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Test type utils
 */
public class TestTypeUtils {

	static final Logger logger = Logger.getLogger(TypeUtils.class.getPackage().getName());

	@Test
	public void testNumberAsPrimitives() {
		assertFalse(TypeUtils.isTypeAssignableFrom(Short.TYPE, Double.TYPE));
	}

	@Test
	public void testNumberAsClasses() {
		assertFalse(TypeUtils.isTypeAssignableFrom(Short.class, Double.class));
	}

	@Test
	public void testRawType() {
		assertTrue(TypeUtils.isTypeAssignableFrom(Vector.class, MyVector.class));
		assertFalse(TypeUtils.isTypeAssignableFrom(MyVector.class, Vector.class));
	}

	@Test
	public void testGenerics() throws SecurityException, NoSuchMethodException {
		// MyClass5<?, ?>
		Type type1 = TestInstantiatedTypes.class.getMethod("retrieveType11").getGenericReturnType();
		// MyClass5<?, ? extends Number>
		Type type2 = TestInstantiatedTypes.class.getMethod("retrieveType12").getGenericReturnType();
		assertTrue(TypeUtils.isTypeAssignableFrom(type1, type2));
		assertTrue(TypeUtils.isTypeAssignableFrom(type2, type1));
	}

	// Test that t2 is NOT an instance of t1
	public static interface ShouldFail {
		public void test4(short t1, double t2);

		public void test10(Vector t1, List<String> t2);

		public void test11(String t1, List<String> t2);

		public void test12(ArrayList<String> t1, List<String> t2);

		public void test14(Vector<String> t1, List<String> t2);

		public void test15(Vector<String> t1, ArrayList<String> t2);
	}

	@Test
	public void testAssignability1() {
		Class<ShouldFail> c = ShouldFail.class;
		for (Method m : c.getDeclaredMethods()) {
			checkFail(m);
		}

	}

	// Test that t2 is an instance of t1
	public static interface ShouldSucceed {
		public void test1(Object t1, Object t2);

		public void test2(int t1, Integer t2);

		public void test3(float t1, int t2);

		public void test11(List t1, Vector<String> t2);

		public void test12(Vector<String> t1, Vector<String> t2);

		public void test13(List<String> t1, Vector<String> t2);

		public void test14(List<String> t1, ArrayList<String> t2);

	}

	@Test
	public void testAssignability2() {
		Class<ShouldSucceed> c = ShouldSucceed.class;
		for (Method m : c.getDeclaredMethods()) {
			checkSucceed(m);
		}

	}

	// Test that super type of t1 is t2
	public static interface TestSuperType {
		public void test20(MyClass2<Integer, Boolean> t1, MyClass1<Boolean> t2);

		public void test21(MyClass2<Integer, List<Boolean>> t1, MyClass1<List<Boolean>> t2);

		public void test22(MyClass3<Integer> t1, MyClass1<List<Integer>> t2);

	}

	@Test
	public void testSuperTypes() {
		Class<TestSuperType> c = TestSuperType.class;
		for (Method m : c.getDeclaredMethods()) {
			checkSuperType(m);
		}

	}

	public static interface TestInstantiatedTypes {
		public MyClass2<String, Boolean> retrieveType1();

		public MyClass3<String> retrieveType2();

		public List<String> retrieveType3();

		public List<MyClass1<String>> retrieveType4();

		public MyClass1<String> retrieveType5();

		public MyClass5<String, Integer> retrieveType6();

		public List<? extends MyClass2<String, Number>> retrieveType7();

		public List<? extends Bound2> retrieveType8();

		public List<? extends Bound3<?>> retrieveType9();

		public List<? extends Number> retrieveType10();

		public MyClass5<?, ?> retrieveType11();

		public MyClass5<?, ? extends Number> retrieveType12();

	}

	@Test
	public void testInstantiatedTypes1() {
		TypeVariable e = Vector.class.getTypeParameters()[0];
		assertEquals(String.class, TypeUtils.makeInstantiatedType(e, MyVector.class));
	}

	@Test
	public void testInstantiatedTypes2() throws SecurityException, NoSuchMethodException {

		// MyClass2<String, Boolean>
		Type genericType = TestInstantiatedTypes.class.getMethod("retrieveType1").getGenericReturnType();

		TypeVariable a = MyClass1.class.getTypeParameters()[0];
		assertEquals(Boolean.class, TypeUtils.makeInstantiatedType(a, genericType));
		TypeVariable b = MyClass2.class.getTypeParameters()[0];
		assertEquals(String.class, TypeUtils.makeInstantiatedType(b, genericType));
		TypeVariable d = MyClass2.class.getTypeParameters()[1];
		assertEquals(Boolean.class, TypeUtils.makeInstantiatedType(d, genericType));
	}

	@Test
	public void testInstantiatedTypes3() throws SecurityException, NoSuchMethodException {

		// MyClass3<String>
		Type genericType = TestInstantiatedTypes.class.getMethod("retrieveType2").getGenericReturnType();
		// List<String>
		Type expectedType = TestInstantiatedTypes.class.getMethod("retrieveType3").getGenericReturnType();

		TypeVariable a = MyClass1.class.getTypeParameters()[0];
		assertEquals(expectedType, TypeUtils.makeInstantiatedType(a, genericType));
		TypeVariable c = MyClass3.class.getTypeParameters()[0];
		assertEquals(String.class, TypeUtils.makeInstantiatedType(c, genericType));
	}

	@Test
	public void testInstantiatedTypes4() throws SecurityException, NoSuchMethodException {

		// MyClass4
		Type genericType = MyClass4.class;
		// List<MyClass1<String>>
		Type expectedType1 = TestInstantiatedTypes.class.getMethod("retrieveType4").getGenericReturnType();
		// MyClass1<String>
		Type expectedType2 = TestInstantiatedTypes.class.getMethod("retrieveType5").getGenericReturnType();

		TypeVariable a = MyClass1.class.getTypeParameters()[0];
		assertEquals(expectedType1, TypeUtils.makeInstantiatedType(a, genericType));

		TypeVariable c = MyClass3.class.getTypeParameters()[0];
		assertEquals(expectedType2, TypeUtils.makeInstantiatedType(c, genericType));
	}

	@Test
	public void testInstantiatedTypes5() throws SecurityException, NoSuchMethodException {

		// MyClass5<String, Integer>
		Type genericType = TestInstantiatedTypes.class.getMethod("retrieveType6").getGenericReturnType();

		// List<MyClass1<String>>
		Type expectedType1 = TestInstantiatedTypes.class.getMethod("retrieveType4").getGenericReturnType();
		// MyClass1<String>
		Type expectedType2 = TestInstantiatedTypes.class.getMethod("retrieveType5").getGenericReturnType();

		TypeVariable d5 = MyClass5.class.getTypeParameters()[0];
		assertEquals(String.class, TypeUtils.makeInstantiatedType(d5, genericType));
		TypeVariable b5 = MyClass5.class.getTypeParameters()[1];
		assertEquals(Integer.class, TypeUtils.makeInstantiatedType(b5, genericType));

		TypeVariable b2 = MyClass2.class.getTypeParameters()[0];
		assertEquals(Integer.class, TypeUtils.makeInstantiatedType(b2, genericType));
		TypeVariable d2 = MyClass2.class.getTypeParameters()[1];
		assertEquals(String.class, TypeUtils.makeInstantiatedType(d2, genericType));

		TypeVariable a = MyClass1.class.getTypeParameters()[0];
		assertEquals(String.class, TypeUtils.makeInstantiatedType(a, genericType));
	}

	@Test
	public void testInstantiatedTypes6() throws SecurityException, NoSuchMethodException {

		Type listGenericType = TestInstantiatedTypes.class.getMethod("retrieveType7").getGenericReturnType();
		// ? extends MyClass2<String, Number>
		Type genericType = ((ParameterizedType) listGenericType).getActualTypeArguments()[0];

		System.out.println(TypeUtils.simpleRepresentation(genericType) + " of " + genericType.getClass());

		TypeVariable b = MyClass2.class.getTypeParameters()[0];
		assertEquals(String.class, TypeUtils.makeInstantiatedType(b, genericType));
		TypeVariable d = MyClass2.class.getTypeParameters()[1];
		assertEquals(Number.class, TypeUtils.makeInstantiatedType(d, genericType));

	}

	private static void checkFail(Method m) {
		Type t1 = m.getGenericParameterTypes()[0];
		Type t2 = m.getGenericParameterTypes()[1];
		System.out.println("checkFail " + (TypeUtils.isTypeAssignableFrom(t1, t2, true) ? "NOK " : "OK  ") + "Method " + m.getName()
				+ " t1: " + t1 + " of " + t1.getClass().getSimpleName() + " t2: " + t2 + " of " + t2.getClass().getSimpleName());
		assertFalse(TypeUtils.isTypeAssignableFrom(t1, t2, true));
	}

	private static void checkSucceed(Method m) {
		Type t1 = m.getGenericParameterTypes()[0];
		Type t2 = m.getGenericParameterTypes()[1];
		System.out.println("checkSucceed " + (TypeUtils.isTypeAssignableFrom(t1, t2, true) ? "OK  " : "NOK ") + "Method " + m.getName()
				+ " t1: " + t1 + " of " + t1.getClass().getSimpleName() + " t2: " + t2 + " of " + t2.getClass().getSimpleName());
		assertTrue(TypeUtils.isTypeAssignableFrom(t1, t2, true));
	}

	private static void checkSuperType(Method m) {
		Type t1 = m.getGenericParameterTypes()[0];
		Type t2 = m.getGenericParameterTypes()[1];
		System.out.println(">>> checkSuperType " + (TypeUtils.getSuperType(t1).equals(t2) ? "OK  " : "NOK ") + "Method " + m.getName()
				+ " type: " + TypeUtils.simpleRepresentation(t1) + " super type: " + TypeUtils.simpleRepresentation(t2));
		// return TypeUtils.isTypeAssignableFrom(t2, TypeUtils.getSuperType(t1), true);
		assertEquals(TypeUtils.getSuperType(t1), t2);

	}

	public static class MyClass1<A> {

	}

	public static abstract class MyClass2<B, D> extends MyClass1<D> {

	}

	public static class MyClass3<C> extends MyClass1<List<C>> {

	}

	public static class MyClass4 extends MyClass3<MyClass1<String>> {

	}

	public static abstract class MyClass5<D, B extends Number> extends MyClass2<B, D> {

	}

	public static class MyVector extends Vector<String> {

	}

	public static abstract class ClassA<A extends Bound1<?>> extends MyClass1<A> {

		public abstract List<? extends A> getSomeAs();
	}

	public static abstract class ClassB<B extends Bound1<?>> extends ClassA<B> {

	}

	public static abstract class ClassC extends ClassB<Bound2> {

	}

	public static abstract class Bound1<E> {

		public abstract E getE();
	}

	public static abstract class Bound2 extends Bound1<String> {

	}

	public static abstract class Bound3<N extends Number> extends Bound1<N> {

	}

	@Test
	public void testInstantiatedTypes7() throws SecurityException, NoSuchMethodException {

		// ClassC
		Type genericType = ClassC.class;

		TypeVariable a = ClassA.class.getTypeParameters()[0];
		assertEquals(Bound2.class, TypeUtils.makeInstantiatedType(a, genericType));

		TypeVariable b = ClassB.class.getTypeParameters()[0];
		assertEquals(Bound2.class, TypeUtils.makeInstantiatedType(b, genericType));

		Type methodType = ClassA.class.getMethods()[0].getGenericReturnType();
		System.out.println("methodType = " + methodType);

		// List<? extends Bound2>
		Type expectedType1 = TestInstantiatedTypes.class.getMethod("retrieveType8").getGenericReturnType();

		assertEquals(expectedType1, TypeUtils.makeInstantiatedType(methodType, genericType));

		try {
			Method getMethod = List.class.getMethod("get", Integer.TYPE);
			// System.out.println("getMethod=" + getMethod);
			Type methodType2 = getMethod.getGenericReturnType();
			// System.out.println("methodType2=" + methodType2);

			// ? extends Bound2
			Type expectedType2 = ((ParameterizedType) expectedType1).getActualTypeArguments()[0];
			assertEquals(expectedType2, TypeUtils.makeInstantiatedType(methodType2, expectedType1));

			Method getEMethod = Bound1.class.getMethod("getE");
			// System.out.println("getEMethod=" + getEMethod);
			Type methodType3 = getEMethod.getGenericReturnType();
			// System.out.println("methodType3=" + methodType3);

			assertEquals(String.class, TypeUtils.makeInstantiatedType(methodType3, expectedType2));

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testInstantiatedTypes8() throws SecurityException, NoSuchMethodException {

		// List<? extends Bound3<?>
		Type expectedType1 = TestInstantiatedTypes.class.getMethod("retrieveType9").getGenericReturnType();
		// ? extends Bound3<?>
		Type genericType = ((ParameterizedType) expectedType1).getActualTypeArguments()[0];

		try {
			Method getEMethod = Bound1.class.getMethod("getE");
			Type methodType3 = getEMethod.getGenericReturnType();

			WildcardType result = (WildcardType) TypeUtils.makeInstantiatedType(methodType3, genericType);

			// List<? extends Number>
			Type listOfExpectedType2 = TestInstantiatedTypes.class.getMethod("retrieveType10").getGenericReturnType();
			// ? extends Number
			WildcardType expectedType2 = (WildcardType) ((ParameterizedType) listOfExpectedType2).getActualTypeArguments()[0];

			assertEquals(expectedType2, TypeUtils.makeInstantiatedType(methodType3, genericType));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
}
