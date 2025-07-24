package org.openflexo.connie.java.parser;

import java.util.List;

import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.BindingPath;
import org.openflexo.connie.expr.CastExpression;
import org.openflexo.connie.java.expr.JavaCastExpression;
import org.openflexo.connie.java.expr.JavaInstanceOfExpression;
import org.openflexo.connie.type.ParameterizedTypeImpl;

public class TestExpressionWithTypesParser extends ParserTestCase {

	// Test instanceof

	public void testInstanceOf() {
		tryToParse("a instanceof Toto", "a instanceof Toto", JavaInstanceOfExpression.class, null, false);
	}

	public void testInstanceOfInteger() {
		tryToParse("2 instanceof Integer", "true", JavaInstanceOfExpression.class, true, false);
	}

	public void testInstanceOfInteger2() {
		tryToParse("2.2 instanceof Integer", "false", JavaInstanceOfExpression.class, false, false);
	}

	// Test new instance

	public void testNewInstance() {
		tryToParse("new ArrayList()", "new ArrayList()", BindingPath.class, null, false);
	}

	public void testNewInstance2() {
		tryToParse("new a.A()", "new a.A()", BindingPath.class, null, false);
	}

	public void testNewInstance3() {
		tryToParse("new a.A(new a.B(),new a.C())", "new a.A(new a.B(),new a.C())", BindingPath.class, null, false);
	}

	public void testNewInstance4() {
		tryToParse("new java.util.Hashtable<String,java.util.List<String>>()", "new Hashtable<String,List<String>>()", BindingPath.class,
				null, false);
	}

	public void testNewInstance5() {
		tryToParse("(Map)(new java.util.Hashtable(1,2))", "(Map)new Hashtable(1,2)", JavaCastExpression.class, null, false);
	}

	public void testCombo() {
		tryToParse("new Object().toString()", "new Object().toString()", BindingPath.class, null, false);
	}

	public void testInnerNewInstance() {
		tryToParse("a.b.new c.d.E()", "a.b.new c.d.E()", BindingPath.class, null, false);
	}

	// Test cast

	public void testVoidCast() {
		tryToParse("(void)2", "(void)2", JavaCastExpression.class, null, false);
	}

	public void testIntCast() {
		tryToParse("(int)2", "(int)2", JavaCastExpression.class, null, false);
	}

	public void testShortCast() {
		tryToParse("(short)2", "(short)2", JavaCastExpression.class, null, false);
	}

	public void testByteCast() {
		tryToParse("(byte)2", "(byte)2", JavaCastExpression.class, null, false);
	}

	public void testLongCast() {
		tryToParse("(long)2", "(long)2", JavaCastExpression.class, null, false);
	}

	public void testFloatCast() {
		tryToParse("(float)2", "(float)2", JavaCastExpression.class, null, false);
	}

	public void testDoubleCast() {
		tryToParse("(double)2", "(double)2", JavaCastExpression.class, null, false);
	}

	public void testCharCast() {
		tryToParse("(char)2", "(char)2", JavaCastExpression.class, null, false);
	}

	public void testBooleanCast() {
		tryToParse("(boolean)a", "(boolean)a", JavaCastExpression.class, null, false);
	}

	public void testCastWithClass() {
		tryToParse("(AType)2", "(AType)2", CastExpression.class, null, false);
	}

	public void testCast2() {
		tryToParse("(java.lang.Integer)2", "(Integer)2", CastExpression.class, null, false);
	}

	public void testCast3() {
		tryToParse("(int)2+((float)2+(double)2)", "(int)2 + ((float)2 + (double)2)", BinaryOperatorExpression.class, null, false);
	}

	public void testCast4() {
		tryToParse("(java.util.List<Tutu>)toto", "(List<Tutu>)toto", CastExpression.class, null, false);
	}

	public void testCast5() {
		CastExpression e = (CastExpression) tryToParse("(List<Tutu>)toto", "(List<Tutu>)toto", CastExpression.class, null, false);
		assertEquals(((ParameterizedTypeImpl) e.getCastType()).getRawType(), List.class);
	}

	public void testCast6() {
		tryToParse("(Foo<Tutu>)toto", "(Foo<Tutu>)toto", CastExpression.class, null, false);
	}

	public void testParameteredCast() {
		tryToParse("(java.util.List<java.lang.String>)data.list", "(List<String>)data.list", CastExpression.class, null, false);
	}

	public void testParameteredCast2() {
		tryToParse("(java.util.Hashtable<java.lang.String,java.util.List<java.lang.String>, Boolean>)data.map",
				"(Hashtable<String,List<String>,Boolean>)data.map", CastExpression.class, null, false);
	}

	// Test with Shr syntax
	public void testParameteredCast3() {
		tryToParse("(java.util.Hashtable<java.lang.String,java.util.List<java.lang.String>>)data.map",
				"(Hashtable<String,List<String>>)data.map", CastExpression.class, null, false);
	}

	// Test with Ushr syntax
	public void testParameteredCast4() {
		tryToParse("(A<B<C<D>>>)a.path", "(A<B<C<D>>>)a.path", CastExpression.class, null, false);
	}

	public void testWilcardUpperBound() {
		tryToParse("(List<? extends Tutu>)toto", "(List<? extends Tutu>)toto", CastExpression.class, null, false);
	}

	public void testWilcardLowerBound() {
		tryToParse("(List<? extends Tutu>)toto", "(List<? extends Tutu>)toto", CastExpression.class, null, false);
	}

	public void testWilcardUpperBounds() {
		tryToParse("(Map<? extends Key, ? extends Value>)toto", "(Map<? extends Key,? extends Value>)toto", CastExpression.class, null,
				false);
	}

	public void testImportType() {
		tryToParse("(java.lang.reflect.Type)object", "(Type)object", JavaCastExpression.class, null, false);
		assertTrue(getTypingSpace().isTypeImported(java.lang.reflect.Type.class));
	}

	// Test class methods

	public void testClassMethod1() {
		tryToParse("Class.forName(\"Foo\")", "Class.forName(\"Foo\")", BindingPath.class, null, false);
	}

	public void testClassMethod2() {
		tryToParse("java.lang.Class.forName(\"Foo\")", "Class.forName(\"Foo\")", BindingPath.class, null, false);
	}

}
