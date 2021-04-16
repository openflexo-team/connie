package org.openflexo.connie.java.parser;

import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.CastExpression;
import org.openflexo.connie.java.expr.JavaCastExpression;

public class TestExpressionWithTypesParser extends ParserTestCase {

	// Test instanceof

	/*public void testInstanceOf() {
		tryToParse("a instanceof Toto", "toto--", BindingValue.class, null, false);
	}*/

	// Test new instance

	/*public void testNewInstance() {
		tryToParse("new List()", "toto--", BindingValue.class, null, false);
	}
	
	public void testNewInstance2() {
		tryToParse("(Map)(new java.util.HashTable(1,2))", "toto--", BindingValue.class, null, false);
	}*/

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
		tryToParse("(List<Tutu>)toto", "(List<Tutu>)toto", CastExpression.class, null, false);
	}

	public void testParameteredCast() {
		tryToParse("(java.util.List<java.lang.String>)data.list", "(List<String>)data.list", CastExpression.class, null, false);
	}

	public void testParameteredCast2() {
		tryToParse("(java.util.Hashtable<java.lang.String,java.util.List<java.lang.String>>)data.map", "(Hashtable<String,String>)data.map",
				CastExpression.class, null, false);
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

}
