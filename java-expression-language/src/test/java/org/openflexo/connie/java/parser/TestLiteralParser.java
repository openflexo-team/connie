package org.openflexo.connie.java.parser;

import org.openflexo.connie.java.expr.JavaConstant.BooleanConstant;
import org.openflexo.connie.java.expr.JavaConstant.CharConstant;
import org.openflexo.connie.java.expr.JavaConstant.DoubleConstant;
import org.openflexo.connie.java.expr.JavaConstant.FloatConstant;
import org.openflexo.connie.java.expr.JavaConstant.IntegerConstant;
import org.openflexo.connie.java.expr.JavaConstant.LongConstant;
import org.openflexo.connie.java.expr.JavaConstant.ObjectSymbolicConstant;
import org.openflexo.connie.java.expr.JavaConstant.StringConstant;
import org.openflexo.connie.java.expr.JavaUnaryOperatorExpression;

public class TestLiteralParser extends ParserTestCase {

	public void testNull() {
		tryToParse("null", "null", ObjectSymbolicConstant.class, null, false);
	}

	public void testTrue() {
		tryToParse("true", "true", BooleanConstant.class, true, false);
	}

	public void testFalse() {
		tryToParse("false", "false", BooleanConstant.class, false, false);
	}

	public void testSimpleString() {
		tryToParse("\"aString\"", "\"aString\"", StringConstant.class, "aString", false);
	}

	public void testSimpleCharacter() {
		tryToParse("'a'", "'a'", CharConstant.class, 'a', false);
	}

	public void testSimpleInteger() {
		tryToParse("42", "42", IntegerConstant.class, 42, false);
	}

	public void testLongIntegerWithFinalL() {
		tryToParse("42L", "42", LongConstant.class, 42L, false);
	}

	public void testLongIntegerWithFinall() {
		tryToParse("42l", "42", LongConstant.class, 42L, false);
	}

	public void testLongInteger() {
		tryToParse("4242424242242424242L", "4242424242242424242", LongConstant.class, 4242424242242424242L, false);
	}

	public void testHexIntegerWithx() {
		tryToParse("0xFF", "255", IntegerConstant.class, 255, false);
	}

	public void testHexIntegerWithX() {
		tryToParse("0xFE", "254", IntegerConstant.class, 254, false);
	}

	public void testOctalInteger() {
		tryToParse("0123776", "43006", IntegerConstant.class, 0123776, false);
	}

	public void testSimpleFloat() {
		tryToParse("3.1415", "3.1415", DoubleConstant.class, 3.1415, false);
	}

	public void testSimpleFloatWithF() {
		tryToParse("3.1415F", null, FloatConstant.class, null, false);
	}

	public void testSimpleFloatWithf() {
		tryToParse("3.1415f", null, FloatConstant.class, null, false);
	}

	public void testSimpleDoubleWithD() {
		tryToParse("3.1415D", "3.1415", DoubleConstant.class, 3.1415D, false);
	}

	public void testSimpleDoubleWithd() {
		tryToParse("3.1415d", "3.1415", DoubleConstant.class, 3.1415d, false);
	}

	public void testExpNumber1() {
		tryToParse("1e42", "1.0E42", DoubleConstant.class, 1e42, false);
	}

	public void testExpNumber2() {
		tryToParse("1.786e-42", "1.786E-42", DoubleConstant.class, 1.786e-42, false);
	}

	public void testNumericValue1() {
		tryToParse("34", "34", IntegerConstant.class, 34, false);
	}

	public void testNumericValue2() {
		tryToParse("7.8", "7.8", DoubleConstant.class, 7.8, false);
	}

	public void testNumericValue3() {
		tryToParse("1.876E12", "1.876E12", DoubleConstant.class, 1.876E12, false);
	}

	public void testNumericValue4() {
		tryToParse("0.876e-9", "8.76E-10", DoubleConstant.class, 8.76E-10, false);
	}

	public void testNegativeInteger() {
		tryToParse("-89", "-89", JavaUnaryOperatorExpression.class, -89, false);
	}

	public void testExplicitPositiveInteger() {
		tryToParse("+89", "89", JavaUnaryOperatorExpression.class, 89, false);
	}

	public void testExplicitPositiveFloat() {
		tryToParse("+89.7856", "89.7856", JavaUnaryOperatorExpression.class, 89.7856, false);
	}

}
