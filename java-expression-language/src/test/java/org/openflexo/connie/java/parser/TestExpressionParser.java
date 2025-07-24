package org.openflexo.connie.java.parser;

import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.BindingPath;
import org.openflexo.connie.expr.ConditionalExpression;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.java.expr.JavaBinaryOperatorExpression;
import org.openflexo.connie.java.expr.JavaBooleanBinaryOperator;
import org.openflexo.connie.java.expr.JavaConditionalExpression;
import org.openflexo.connie.java.expr.JavaConstant.BooleanConstant;
import org.openflexo.connie.java.expr.JavaUnaryOperatorExpression;

public class TestExpressionParser extends ParserTestCase {

	// Test Conditional

	public void testSimpleConditional() {
		tryToParse("1 < 2 ? true : false", "true", JavaConditionalExpression.class, true, false);
	}

	public void testSymbolicConditional() {
		tryToParse("a > b ? c : d", "(a > b ? c : d)", JavaConditionalExpression.class, null, false);
	}

	// Test comparison

	public void testSimpleEq() {
		tryToParse("2 == 2 ? true : false", "true", JavaConditionalExpression.class, true, false);
	}

	public void testSymbolicEq() {
		tryToParse("a == b", "a == b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleNeq() {
		tryToParse("1 != 2 ? true : false", "true", JavaConditionalExpression.class, true, false);
	}

	public void testSymbolicNeq() {
		tryToParse("a != b", "a != b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleLt() {
		tryToParse("1 < 2 ? true : false", "true", JavaConditionalExpression.class, true, false);
	}

	public void testSymbolicLt() {
		tryToParse("a < b", "a < b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleGt() {
		tryToParse("2 > 1 ? true : false", "true", JavaConditionalExpression.class, true, false);
	}

	public void testSymbolicGt() {
		tryToParse("a > b", "a > b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleLtEq() {
		tryToParse("2 <= 2 ? true : false", "true", JavaConditionalExpression.class, true, false);
	}

	public void testSymbolicLtEq() {
		tryToParse("a <= b", "a <= b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleGtEq() {
		tryToParse("1 >= 1 ? true : false", "true", JavaConditionalExpression.class, true, false);
	}

	public void testSymbolicGtEq() {
		tryToParse("a >= b", "a >= b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleNumberAddition() {
		tryToParse("1+1", "2", JavaBinaryOperatorExpression.class, 2, false);
	}

	public void testSimpleSymbolicAddition() {
		tryToParse("a+b", "a + b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleSubstraction() {
		tryToParse("7-8", "-1", JavaBinaryOperatorExpression.class, -1, false);
	}

	public void testSymbolicEquals() {
		tryToParse("a==b", "a == b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleEquals() {
		tryToParse("1==2", "false", JavaBinaryOperatorExpression.class, false, false);
	}

	public void testSymbolicShiftLeft() {
		tryToParse("a<<b", "a << b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleShiftLeft1() {
		tryToParse("1<<2", "4", JavaBinaryOperatorExpression.class, 1 << 2, false);
	}

	public void testSimpleShiftLeft2() {
		tryToParse("109675<<265", "56153600", JavaBinaryOperatorExpression.class, 109675 << 265, false);
	}

	public void testSymbolicShiftRight() {
		tryToParse("a>>b", "a >> b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleShiftRight1() {
		tryToParse("1>>3", "0", JavaBinaryOperatorExpression.class, 1 >> 3, false);
	}

	public void testSimpleShiftRight2() {
		tryToParse("256>>3", "32", JavaBinaryOperatorExpression.class, 256 >> 3, false);
	}

	public void testSymbolicUShiftRight() {
		tryToParse("a>>>b", "a >>> b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleUShiftRight1() {
		tryToParse("1>>>3", "0", JavaBinaryOperatorExpression.class, 1 >>> 3, false);
	}

	public void testSimpleUShiftRight2() {
		tryToParse("256>>>3", "32", JavaBinaryOperatorExpression.class, 256 >>> 3, false);
	}

	public void testSimpleIntegerMultiplication() {
		tryToParse("2*2", "4", JavaBinaryOperatorExpression.class, 4, false);
	}

	public void testSimpleDoubleMultiplication() {
		tryToParse("3.1415*3.1415", "9.86902225", JavaBinaryOperatorExpression.class, 3.1415 * 3.1415, false);
	}

	public void testSimpleSymbolicMultiplication() {
		tryToParse("a*b", "a * b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleIntegerDivision() {
		tryToParse("27/3", "9.0", JavaBinaryOperatorExpression.class, 9.0, false);
	}

	public void testSimpleDoubleDivision() {
		tryToParse("3.1415/2.1", "1.495952380952381", JavaBinaryOperatorExpression.class, 3.1415 / 2.1, false);
	}

	public void testSimpleSymbolicDivision() {
		tryToParse("a/b", "a / b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleModDivision() {
		tryToParse("32%3", "2.0", JavaBinaryOperatorExpression.class, 2.0, false);
	}

	public void testSimpleSymbolicModDivision() {
		tryToParse("a%b", "a % b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPreIncrement() {
		tryToParse("++a", "++a", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPreIncrement2() {
		tryToParse("++a.b", "++a.b", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPreDecrement() {
		tryToParse("--a", "--a", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPreDecrement2() {
		tryToParse("--a.b", "--a.b", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPostIncrement() {
		tryToParse("a++", "a++", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPostIncrement2() {
		tryToParse("a.b++", "a.b++", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPostDecrement() {
		tryToParse("a--", "a--", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicPostDecrement2() {
		tryToParse("a.b--", "a.b--", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleSymbolicBitwiseComplement() {
		tryToParse("~a", "~a", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleBitwiseComplement() {
		tryToParse("~123", "-124", JavaUnaryOperatorExpression.class, ~123, false);
	}

	public void testSimpleSymbolicNot() {
		tryToParse("!a", "!a", JavaUnaryOperatorExpression.class, null, false);
	}

	public void testSimpleNot() {
		tryToParse("!true", "false", JavaUnaryOperatorExpression.class, false, false);
	}

	public void testSimpleSymbolicBitwiseAnd() {
		tryToParse("a & b", "a & b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleBitwiseAnd() {
		tryToParse("255 & 13", "13", JavaBinaryOperatorExpression.class, 255 & 13, false);
	}

	public void testSimpleSymbolicBitwiseOr() {
		tryToParse("a | b", "a | b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleBitwiseOr() {
		tryToParse("128 | 15", "143", JavaBinaryOperatorExpression.class, 128 | 15, false);
	}

	public void testSimpleSymbolicBitwiseXOr() {
		tryToParse("a ^ b", "a ^ b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleBitwiseXOr() {
		tryToParse("8 ^ 15", "7", JavaBinaryOperatorExpression.class, 8 ^ 15, false);
	}

	public void testSimpleSymbolicOr() {
		tryToParse("a || b", "a || b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleOr() {
		tryToParse("(1<2)||(2<1)", "true", JavaBinaryOperatorExpression.class, true, false);
	}

	public void testSimpleSymbolicAnd() {
		tryToParse("a && b", "a && b", JavaBinaryOperatorExpression.class, null, false);
	}

	public void testSimpleAnd() {
		tryToParse("(1<2)&&(2<1)", "false", JavaBinaryOperatorExpression.class, false, false);
	}

	public void testNumericValue8() {
		tryToParse("1+(2*7-9)", "6", BinaryOperatorExpression.class, 6, false);
	}

	public void testNumericValue9() {
		tryToParse("1+((298*7.1e-3)-9)", "-5.8842", BinaryOperatorExpression.class, -5.8842, false);
	}

	public void testStringExpression() {
		tryToParse("\"foo1\"+\"foo2\"", "\"foo1foo2\"", BinaryOperatorExpression.class, "foo1foo2", false);
	}

	public void testExpression1() {
		tryToParse("machin+1", "machin + 1", BinaryOperatorExpression.class, null, false);
	}

	public void testExpression2() {
		tryToParse("machin+1*6-8/7+bidule", "machin + 6 - 1.1428571428571428 + bidule", BinaryOperatorExpression.class, null, false);
	}

	public void testExpression3() {
		tryToParse("7-x-(-x-6-8*2)", "7 - x - (-x - 6 - 16)", BinaryOperatorExpression.class, null, false);
	}

	public void testExpression4() {
		tryToParse("1+function(test,4<7-x)", "1 + function(test,4 < 7 - x)", BinaryOperatorExpression.class, null, false);
	}

	public void testEquality() {
		Expression e = tryToParse("a==b", "a == b", BinaryOperatorExpression.class, null, false);
		assertEquals(JavaBooleanBinaryOperator.EQUALS, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testEquality2() {
		tryToParse("binding1.a.b == binding2.a.b*7", "binding1.a.b == binding2.a.b * 7", BinaryOperatorExpression.class, null, false);
	}

	public void testBoolean1() {
		tryToParse("false", "false", BooleanConstant.FALSE.getClass(), false, false);
	}

	public void testBoolean2() {
		tryToParse("true", "true", BooleanConstant.TRUE.getClass(), true, false);
	}

	public void testBoolean3() {
		tryToParse("false && true", "false", BinaryOperatorExpression.class, false, false);
	}

	public void testBooleanExpression1() {
		tryToParse("!a&&b", "!a && b", BinaryOperatorExpression.class, null, false);
	}

	public void testImbricatedCall() {
		tryToParse("function1(function2(8+1,9,10-1))", "function1(function2(9,9,9))", BindingPath.class, null, false);
	}

	public void testEmptyCall() {
		tryToParse("function1()", "function1()", BindingPath.class, null, false);
	}

	public void testComplexBooleanExpression() {
		tryToParse("a && (c || d && (!f)) ||b", "a && (c || d && !f) || b", BinaryOperatorExpression.class, null, false);
	}

	public void testArithmeticNumberComparison1() {
		tryToParse("1 < 2", "true", BinaryOperatorExpression.class, true, false);
	}

	public void testArithmeticNumberComparison2() {
		tryToParse("0.1109 < 1.1108E-03", "false", BinaryOperatorExpression.class, false, false);
	}

	public void testStringConcatenation() {
		tryToParse("\"a + ( 2 + b )\"+2", "\"a + ( 2 + b )2\"", BinaryOperatorExpression.class, null, false);
	}

	public void testParsingError1() {
		tryToParse("a\"b", "", null, null, true);
	}

	public void testParsingError2() {
		tryToParse("a'b", "", null, null, true);
	}

	public void testParsingError3() {
		tryToParse("\"", "", null, null, true);
	}

	public void testParsingError4() {
		tryToParse("test23 ( fdfd + 1", "", null, null, true);
	}

	public void testParsingError5() {
		tryToParse("test24 [ fdfd + 1", "", null, null, true);
	}

	public void testParsingError6() {
		tryToParse("obj..f()", "", null, null, true);
	}

	public void testIgnoredChars() {
		tryToParse(" test  \n\n", "test", BindingPath.class, null, false);
	}

	// Test conditionals

	public void testConditional1() {
		tryToParse("a?b:c", "(a ? b : c)", ConditionalExpression.class, null, false);
	}

	public void testConditional2() {
		tryToParse("a > 9 ?true:false", "(a > 9 ? true : false)", ConditionalExpression.class, null, false);
	}

	public void testConditional3() {
		tryToParse("a+1 > 10-7 ?8+4:5", "(a + 1 > 3 ? 12 : 5)", ConditionalExpression.class, null, false);
	}

	public void testConditional4() {
		tryToParse("a+1 > (a?1:2) ?8+4:5", "(a + 1 > ((a ? 1 : 2)) ? 12 : 5)", ConditionalExpression.class, null, false);
	}

	public void testConditional5() {
		tryToParse("2 < 3 ? 4:2", "4", ConditionalExpression.class, 4, false);
	}

	public void testConditional6() {
		tryToParse("2 > 3 ? 4:2", "2", ConditionalExpression.class, 2, false);
	}

	public void testInvalidConditional() {
		tryToParse("2 > 3 ? 3", "", ConditionalExpression.class, null, true);
	}

}
