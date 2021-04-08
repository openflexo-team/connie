package org.openflexo.connie.java.parser;

import java.lang.reflect.InvocationTargetException;

import org.openflexo.connie.ParseException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Constant.FloatConstant;
import org.openflexo.connie.expr.Constant.IntegerConstant;
import org.openflexo.connie.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.connie.expr.Expression;

import junit.framework.TestCase;

public class TestExpressionParser extends TestCase {

	private DefaultExpressionPrettyPrinter prettyPrinter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prettyPrinter = new DefaultExpressionPrettyPrinter();
	}

	private Expression tryToParse(String anExpression, String expectedEvaluatedExpression,
			Class<? extends Expression> expectedExpressionClass, Object expectedEvaluation, boolean shouldFail) {

		/*try {
			Expression parsed = ExpressionParser.parse(anExpression);
			System.out.println("parsed=" + parsed);
			return parsed;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (!shouldFail) {
				fail();
			}
			return null;
		}*/

		try {
			System.out.println("Parsing... " + anExpression);
			Expression parsed = ExpressionParser.parse(anExpression);
			System.out.println("parsed=" + parsed);
			Expression evaluated = parsed.evaluate(null);
			System.out.println("evaluated=" + evaluated);
			System.out.println("Successfully parsed as : " + parsed.getClass().getSimpleName());
			System.out.println("Normalized: " + prettyPrinter.getStringRepresentation(parsed));
			System.out.println("Evaluated: " + prettyPrinter.getStringRepresentation(evaluated));
			if (shouldFail) {
				fail();
			}
			assertEquals(expectedExpressionClass, parsed.getClass());
			if (expectedEvaluatedExpression != null) {
				assertEquals(expectedEvaluatedExpression, prettyPrinter.getStringRepresentation(evaluated));
			}
			if (expectedEvaluation != null) {
				if (!(evaluated instanceof Constant)) {
					fail("Evaluated value is not a constant (expected: " + expectedEvaluation + ") but " + expectedEvaluation);
				}
				if (expectedEvaluation instanceof Number) {
					Object value = ((Constant<?>) evaluated).getValue();
					if (value instanceof Number) {
						assertEquals(((Number) expectedEvaluation).doubleValue(), ((Number) value).doubleValue());
					}
					else {
						fail("Evaluated value is not a number (expected: " + expectedEvaluation + ") but " + expectedEvaluation);
					}
				}
				else {
					assertEquals(expectedEvaluation, ((Constant<?>) evaluated).getValue());
				}
			}
			return parsed;
		} catch (ParseException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			}
			else {
				System.out.println("Parsing " + anExpression + " has failed as expected: " + e.getMessage());
			}
			return null;
		} catch (TypeMismatchException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			}
			else {
				System.out.println("Parsing " + anExpression + " has failed as expected: " + e.getMessage());
			}
			return null;
		} catch (NullReferenceException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			}
			else {
				System.out.println("Parsing " + anExpression + " has failed as expected: " + e.getMessage());
			}
			return null;
		} catch (InvocationTargetException e) {
			fail();
			return null;
		}

	}

	// Test numbers

	public void testSimpleInteger() {
		tryToParse("42", "42", IntegerConstant.class, 42, false);
	}

	public void testLongIntegerWithFinalL() {
		tryToParse("42L", "42", IntegerConstant.class, 42L, false);
	}

	public void testLongIntegerWithFinall() {
		tryToParse("42l", "42", IntegerConstant.class, 42L, false);
	}

	public void testLongInteger() {
		tryToParse("4242424242242424242L", "4242424242242424242", IntegerConstant.class, 4242424242242424242L, false);
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
		tryToParse("3.1415", "3.1415", FloatConstant.class, 3.1415, false);
	}

	public void testSimpleFloatWithF() {
		tryToParse("3.1415F", null, FloatConstant.class, null, false);
	}

	public void testSimpleFloatWithf() {
		tryToParse("3.1415f", null, FloatConstant.class, null, false);
	}

	public void testSimpleDoubleWithD() {
		tryToParse("3.1415D", "3.1415", FloatConstant.class, 3.1415D, false);
	}

	public void testSimpleDoubleWithd() {
		tryToParse("3.1415d", "3.1415", FloatConstant.class, 3.1415d, false);
	}

	public void testExpNumber1() {
		tryToParse("1e42", "1.0E42", FloatConstant.class, 1e42, false);
	}

	public void testExpNumber2() {
		tryToParse("1.786e-42", "1.786E-42", FloatConstant.class, 1.786e-42, false);
	}

	// Test BindingValue

	public void testSimpleIdentifier() {
		tryToParse("foo", "foo", BindingValue.class, null, false);
	}

	public void testUnderscoredIdentifier() {
		tryToParse("foo_foo2", "foo_foo2", BindingValue.class, null, false);
	}

	public void testComposedIdentifier() {
		tryToParse("foo.foo2.foo3", "foo.foo2.foo3", BindingValue.class, null, false);
	}

	public void testSimpleMethodNoArgs() {
		tryToParse("method()", "method()", BindingValue.class, null, false);
	}

	public void testSimpleMethodWith1Arg() {
		tryToParse("method(1)", "method(1)", BindingValue.class, null, false);
	}

	public void testSimpleMethodWith3Args() {
		tryToParse("method(1,2,3)", "method(1,2,3)", BindingValue.class, null, false);
	}

	public void testFullQualifiedMethod() {
		tryToParse("a.b.c.method(1)", "a.b.c.method(1)", BindingValue.class, null, false);
	}

	public void testImbricatedMethods() {
		tryToParse("a.b.c.method(m1(1),d.e.f.m2(1))", "a.b.c.method(m1(1),d.e.f.m2(1))", BindingValue.class, null, false);
	}

	public void testBindingValue6() {
		tryToParse("a.b.c.method1(1).method2(2)+c.d.e", "a.b.c.method(1).method2(2)", BindingValue.class, null, false);
	}

	/*
	public void testBindingValue7() {
		tryToParse("i.am.a(1,2+3,7.8,\"foo\",'a').little.test(1).foo()", "i.am.a(1,5,7.8,\"foo\").little.test(1)", BindingValue.class, null,
				false);
	}
	
	public void testNumericValue1() {
		tryToParse("34", "34", IntegerConstant.class, 34, false);
	}
	
	public void testNumericValue2() {
		tryToParse("7.8", "7.8", FloatConstant.class, 7.8, false);
	}
	
	public void testNumericValue3() {
		tryToParse("1.876E12", "1.876E12", FloatConstant.class, 1.876E12, false);
	}
	
	public void testNumericValue4() {
		tryToParse("0.876e-9", "8.76E-10", FloatConstant.class, 8.76E-10, false);
	}
	
	public void testNumericValue5() {
		tryToParse("-89", "-89", IntegerConstant.class, -89, false);
	}
	
	public void testNumericValue6() {
		tryToParse("-89.7856", "-89.7856", FloatConstant.class, -89.7856, false);
	}
	
	public void testNumericValue7() {
		tryToParse("1+1", "2", BinaryOperatorExpression.class, 2, false);
	}
	
	public void testNumericValue8() {
		tryToParse("1+(2*7-9)", "6", BinaryOperatorExpression.class, 6, false);
	}
	
	public void testNumericValue9() {
		tryToParse("1+((298*7.1e-3)-9)", "-5.8842", BinaryOperatorExpression.class, -5.8842, false);
	}
	
	public void testCharValue() {
		tryToParse("'a'", "'a'", StringConstant.class, 'a', false);
	}
	
	public void testStringValue1() {
		tryToParse("\"foo1\"", "\"foo1\"", StringConstant.class, "foo1", false);
	}
	
	public void testStringValue2() {
		tryToParse("\"foo1\"+\"foo2\"", "\"foo1foo2\"", BinaryOperatorExpression.class, "foo1foo2", false);
	}
	
	public void testExpression1() {
		tryToParse("machin+1", "(machin + 1)", BinaryOperatorExpression.class, null, false);
	}
	
	public void testExpression2() {
		tryToParse("machin+1*6-8/7+bidule", "(((machin + 6) - 1.1428571428571428) + bidule)", BinaryOperatorExpression.class, null, false);
	}
	
	public void testExpression3() {
		tryToParse("7-x-(-x-6-8*2)", "((7 - x) - (((-(x)) - 6) - 16))", BinaryOperatorExpression.class, null, false);
	}
	
	public void testExpression4() {
		tryToParse("1+function(test,4<7-x)", "(1 + function(test,(4 < (7 - x))))", BinaryOperatorExpression.class, null, false);
	}
	
	public void testEquality() {
		Expression e = tryToParse("a==b", "(a = b)", BinaryOperatorExpression.class, null, false);
		// assertEquals(BooleanBinaryOperator.EQUALS, ((BinaryOperatorExpression) e).getOperator());
	}
	
	public void testEquality2() {
		tryToParse("binding1.a.b == binding2.a.b*7", "(binding1.a.b = (binding2.a.b * 7))", BinaryOperatorExpression.class, null, false);
	}
	
	public void testOr1() {
		Expression e = tryToParse("a|b", "(a | b)", BinaryOperatorExpression.class, null, false);
		// assertEquals(BooleanBinaryOperator.OR, ((BinaryOperatorExpression) e).getOperator());
	}
	
	public void testOr2() {
		Expression e = tryToParse("a||b", "(a | b)", BinaryOperatorExpression.class, null, false);
		// assertEquals(BooleanBinaryOperator.OR, ((BinaryOperatorExpression) e).getOperator());
	}
	
	public void testAnd1() {
		Expression e = tryToParse("a&b", "(a & b)", BinaryOperatorExpression.class, null, false);
		// assertEquals(BooleanBinaryOperator.AND, ((BinaryOperatorExpression) e).getOperator());
	}
	
	public void testAnd2() {
		Expression e = tryToParse("a&&b", "(a & b)", BinaryOperatorExpression.class, null, false);
		// assertEquals(BooleanBinaryOperator.AND, ((BinaryOperatorExpression) e).getOperator());
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
		tryToParse("!a&&b", "((!(a)) & b)", BinaryOperatorExpression.class, null, false);
	}
	
	public void testComplexCall() {
		tryToParse("testFunction(-pi/2,7.8,1-9*7/9,aVariable,foo1+foo2,e)",
				"testFunction(-1.5707963267948966,7.8,-6.0,aVariable,(foo1 + foo2),e)", BindingValue.class, null, false);
	}
	
	public void testImbricatedCall() {
		tryToParse("function1(function2(8+1,9,10-1))", "function1(function2(9,9,9))", BindingValue.class, null, false);
	}
	
	public void testEmptyCall() {
		tryToParse("function1()", "function1()", BindingValue.class, null, false);
	}
	
	public void testComplexBooleanExpression() {
		tryToParse("a && (c || d && (!f)) ||b", "((a & (c | (d & (!(f))))) | b)", BinaryOperatorExpression.class, null, false);
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
		tryToParse(" test  \n\n", "test", BindingValue.class, null, false);
	}
	
	public void testConditional1() {
		tryToParse("a?b:c", "(a ? b : c)", ConditionalExpression.class, null, false);
	}
	
	public void testConditional2() {
		tryToParse("a > 9 ?true:false", "((a > 9) ? true : false)", ConditionalExpression.class, null, false);
	}
	
	public void testConditional3() {
		tryToParse("a+1 > 10-7 ?8+4:5", "(((a + 1) > 3) ? 12 : 5)", ConditionalExpression.class, null, false);
	}
	
	public void testConditional4() {
		tryToParse("a+1 > (a?1:2) ?8+4:5", "(((a + 1) > (a ? 1 : 2)) ? 12 : 5)", ConditionalExpression.class, null, false);
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
	
	public void testCast() {
		tryToParse("(AType)2", "", CastExpression.class, null, true);
	}
	
	public void testCast2() {
		tryToParse("(java.lang.Integer)2", "(java.lang.Integer)2", CastExpression.class, null, false);
	}
	
	public void testCast3() {
		tryToParse("(int)2+((float)2+(double)2)", "(int)2+((float)2+(double)2)", BinaryOperatorExpression.class, null, false);
	}
	
	public void testCast4() {
		tryToParse("(List<Tutu>)toto", "toto--", BindingValue.class, null, false);
	}
	
	public void testParameteredCast() {
		tryToParse("(java.util.List<String>)data.list", "(java.util.List<java.lang.String>)data.list", CastExpression.class, null, false);
	}
	
	public void testParameteredCast2() {
		tryToParse("(java.util.Hashtable<java.lang.String,java.util.List<java.lang.String>>)data.map",
				"(java.util.Hashtable<java.lang.String,java.util.List<java.lang.String>>)data.map", CastExpression.class, null, false);
	}
	
	public void testAccentCharacter() {
		tryToParse("flexoConcept.unité", "flexoConcept.unité", BindingValue.class, null, false);
	}
	
	public void testPostInc() {
		tryToParse("toto++", "toto++", BindingValue.class, null, false);
	}
	
	public void testPostDec() {
		tryToParse("toto--", "toto--", BindingValue.class, null, false);
	}
	
	public void testInstanceOf() {
		tryToParse("a instanceof Toto", "toto--", BindingValue.class, null, false);
	}
	
	public void testNewInstance() {
		tryToParse("new List()", "toto--", BindingValue.class, null, false);
	}
	
	public void testNewInstance2() {
		tryToParse("(Map)(new java.util.HashTable(1,2))", "toto--", BindingValue.class, null, false);
	}
	
	public void testClassMethod1() {
		tryToParse("Class.forName(\"Toto\")", "toto--", BindingValue.class, null, false);
	}
	
	public void testClassMethod2() {
		tryToParse("java.Class.forName(\"Toto\")", "toto--", BindingValue.class, null, false);
	}*/

}
