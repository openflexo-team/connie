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

package org.openflexo.connie.del.parser;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.ParseException;
import org.openflexo.connie.del.DELBindingFactory;
import org.openflexo.connie.del.expr.DELBinaryOperatorExpression;
import org.openflexo.connie.del.expr.DELBooleanBinaryOperator;
import org.openflexo.connie.del.expr.DELCastExpression;
import org.openflexo.connie.del.expr.DELConditionalExpression;
import org.openflexo.connie.del.expr.DELConstant.BooleanConstant;
import org.openflexo.connie.del.expr.DELConstant.FloatConstant;
import org.openflexo.connie.del.expr.DELConstant.FloatSymbolicConstant;
import org.openflexo.connie.del.expr.DELConstant.IntegerConstant;
import org.openflexo.connie.del.expr.DELConstant.StringConstant;
import org.openflexo.connie.del.expr.DELExpressionEvaluator;
import org.openflexo.connie.del.expr.DELPrettyPrinter;
import org.openflexo.connie.del.expr.DELUnaryOperatorExpression;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionEvaluator;

import junit.framework.TestCase;

public class TestExpressionParser extends TestCase {

	private final BindingFactory BINDING_FACTORY = new DELBindingFactory();
	private DELPrettyPrinter prettyPrinter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prettyPrinter = new DELPrettyPrinter();
	}

	private Expression tryToParse(String anExpression, String expectedEvaluatedExpression,
			Class<? extends Expression> expectedExpressionClass, Object expectedEvaluation, boolean shouldFail) {

		try {
			System.out.println("Parsing... " + anExpression);
			Bindable bindable = new DefaultBindable() {
				@Override
				public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
				}

				@Override
				public void notifiedBindingChanged(DataBinding<?> dataBinding) {
				}

				@Override
				public BindingModel getBindingModel() {
					return null;
				}

				@Override
				public BindingFactory getBindingFactory() {
					return BINDING_FACTORY;
				}

			};

			Expression parsed = ExpressionParser.parse(anExpression, bindable);
			Expression evaluated = parsed.evaluate(new BindingEvaluationContext() {
				@Override
				public Object getValue(BindingVariable variable) {
					return null;
				}

				@Override
				public ExpressionEvaluator getEvaluator() {
					return new DELExpressionEvaluator(this);
				}
			});
			System.out.println("parsed=" + parsed);
			System.out.println("evaluated=" + evaluated);
			System.out.println("Successfully parsed as : " + parsed.getClass().getSimpleName());
			System.out.println("Normalized: " + prettyPrinter.getStringRepresentation(parsed, null));
			System.out.println("Evaluated: " + prettyPrinter.getStringRepresentation(evaluated, null));
			if (shouldFail) {
				fail();
			}
			assertEquals(expectedExpressionClass, parsed.getClass());
			assertEquals(expectedEvaluatedExpression, prettyPrinter.getStringRepresentation(evaluated, null));
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
		} catch (ReflectiveOperationException e) {
			fail();
			return null;
		}

		/*try {
			System.out.println("\nParsing " + aString);
			Expression parsed = parser.parse(aString);
			System.out.println("Successfully parsed as : " + parsed.getClass().getSimpleName());
			System.out.println("Normalized: " + prettyPrinter.getStringRepresentation(parsed));
			System.out.println("Evaluated: " + prettyPrinter.getStringRepresentation(parsed.evaluate()));
			if (shouldFail) {
				fail();
			}
			assertEquals(expectedEvaluatedExpression, prettyPrinter.getStringRepresentation(parsed.evaluate()));
		} catch (ParseException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + aString + " has failed as expected: " + e.getMessage());
			}
		} catch (TypeMismatchException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + aString + " has failed as expected: " + e.getMessage());
			}
		}*/
	}

	public void testBindingValue() {
		tryToParse("foo", "foo", BindingValue.class, null, false);
	}

	public void testBindingValue2() {
		tryToParse("foo_foo2", "foo_foo2", BindingValue.class, null, false);
	}

	public void testBindingValue3() {
		tryToParse("foo.foo2.foo3", "foo.foo2.foo3", BindingValue.class, null, false);
	}

	public void testBindingValue4() {
		tryToParse("method(1)", "method(1)", BindingValue.class, null, false);
	}

	public void testBindingValue5() {
		tryToParse("a.b.c.method(1)", "a.b.c.method(1)", BindingValue.class, null, false);
	}

	public void testBindingValue6() {
		tryToParse("i.am.a(1,2+3,7.8,'foo').little.test(1)", "i.am.a(1,5,7.8,\"foo\").little.test(1)", BindingValue.class, null, false);
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
		tryToParse("1+1", "2", DELBinaryOperatorExpression.class, 2, false);
	}

	public void testNumericValue8() {
		tryToParse("1+(2*7-9)", "6", DELBinaryOperatorExpression.class, 6, false);
	}

	public void testNumericValue9() {
		tryToParse("1+((298*7.1e-3)-9)", "-5.8842", DELBinaryOperatorExpression.class, -5.8842, false);
	}

	public void testStringValue1() {
		tryToParse("\"foo1\"", "\"foo1\"", StringConstant.class, "foo1", false);
	}

	public void testStringValue2() {
		tryToParse("'foo1'", "\"foo1\"", StringConstant.class, "foo1", false);
	}

	public void testStringValue3() {
		tryToParse("\"foo1\"+\"foo2\"", "\"foo1foo2\"", DELBinaryOperatorExpression.class, "foo1foo2", false);
	}

	public void testStringValue4() {
		tryToParse("\"foo1\"+'and'+\"foo2\"", "\"foo1andfoo2\"", DELBinaryOperatorExpression.class, "foo1andfoo2", false);
	}

	public void testExpression1() {
		tryToParse("machin+1", "(machin + 1)", DELBinaryOperatorExpression.class, null, false);
	}

	public void testExpression2() {
		tryToParse("machin+1*6-8/7+bidule", "(((machin + 6) - 1.1428571428571428) + bidule)", DELBinaryOperatorExpression.class, null,
				false);
	}

	public void testExpression3() {
		tryToParse("7-x-(-x-6-8*2)", "((7 - x) - (((-(x)) - 6) - 16))", DELBinaryOperatorExpression.class, null, false);
	}

	public void testExpression4() {
		tryToParse("1+function(test,4<7-x)", "(1 + function(test,(4 < (7 - x))))", DELBinaryOperatorExpression.class, null, false);
	}

	public void testTrigonometricComputing1() {
		tryToParse("sin(-pi/2)", "-1.0", DELUnaryOperatorExpression.class, -1, false);
	}

	public void testTrigonometricComputing2() {
		tryToParse("-atan(2)", "-1.1071487177940904", DELUnaryOperatorExpression.class, -1.1071487177940904, false);
	}

	public void testTrigonometricComputing3() {
		tryToParse("-(atan(-pi/2)*(3-5*pi/7+8/9))", "1.651284257012876", DELUnaryOperatorExpression.class, 1.651284257012876, false);
	}

	public void testTrigonometricComputing4() {
		tryToParse("-cos(atan(-pi/2)*(3-5*pi/7+8/9))", "0.08040105411083133", DELUnaryOperatorExpression.class, 0.08040105411083133, false);
	}

	public void testEquality() {
		Expression e = tryToParse("a==b", "(a = b)", DELBinaryOperatorExpression.class, null, false);
		assertEquals(DELBooleanBinaryOperator.EQUALS, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testEquality2() {
		tryToParse("binding1.a.b == binding2.a.b*7", "(binding1.a.b = (binding2.a.b * 7))", DELBinaryOperatorExpression.class, null, false);
	}

	public void testOr1() {
		Expression e = tryToParse("a|b", "(a | b)", DELBinaryOperatorExpression.class, null, false);
		assertEquals(DELBooleanBinaryOperator.OR, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testOr2() {
		Expression e = tryToParse("a||b", "(a | b)", DELBinaryOperatorExpression.class, null, false);
		assertEquals(DELBooleanBinaryOperator.OR, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testAnd1() {
		Expression e = tryToParse("a&b", "(a & b)", DELBinaryOperatorExpression.class, null, false);
		assertEquals(DELBooleanBinaryOperator.AND, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testAnd2() {
		Expression e = tryToParse("a&&b", "(a & b)", DELBinaryOperatorExpression.class, null, false);
		assertEquals(DELBooleanBinaryOperator.AND, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testBoolean1() {
		tryToParse("false", "false", BooleanConstant.FALSE.getClass(), false, false);
	}

	public void testBoolean2() {
		tryToParse("true", "true", BooleanConstant.TRUE.getClass(), true, false);
	}

	public void testBoolean3() {
		tryToParse("false && true", "false", DELBinaryOperatorExpression.class, false, false);
	}

	public void testBooleanExpression1() {
		tryToParse("!a&&b", "((!(a)) & b)", DELBinaryOperatorExpression.class, null, false);
	}

	public void testPi() {
		tryToParse("pi", "3.141592653589793", FloatSymbolicConstant.class, null, false);
	}

	public void testPi2() {
		tryToParse("-pi/2", "-1.5707963267948966", DELBinaryOperatorExpression.class, null, false);
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
		tryToParse("a && (c || d && (!f)) ||b", "((a & (c | (d & (!(f))))) | b)", DELBinaryOperatorExpression.class, null, false);
	}

	public void testArithmeticNumberComparison1() {
		tryToParse("1 < 2", "true", DELBinaryOperatorExpression.class, true, false);
	}

	public void testArithmeticNumberComparison2() {
		tryToParse("0.1109 < 1.1108E-03", "false", DELBinaryOperatorExpression.class, false, false);
	}

	public void testStringConcatenation() {
		tryToParse("\"a + ( 2 + b )\"+2", "\"a + ( 2 + b )2\"", DELBinaryOperatorExpression.class, null, false);
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
		tryToParse("a?b:c", "(a ? b : c)", DELConditionalExpression.class, null, false);
	}

	public void testConditional2() {
		tryToParse("a > 9 ?true:false", "((a > 9) ? true : false)", DELConditionalExpression.class, null, false);
	}

	public void testConditional3() {
		tryToParse("a+1 > 10-7 ?8+4:5", "(((a + 1) > 3) ? 12 : 5)", DELConditionalExpression.class, null, false);
	}

	public void testConditional4() {
		tryToParse("a+1 > (a?1:2) ?8+4:5", "(((a + 1) > (a ? 1 : 2)) ? 12 : 5)", DELConditionalExpression.class, null, false);
	}

	public void testConditional5() {
		tryToParse("2 < 3 ? 4:2", "4", DELConditionalExpression.class, 4, false);
	}

	public void testConditional6() {
		tryToParse("2 > 3 ? 4:2", "2", DELConditionalExpression.class, 2, false);
	}

	public void testInvalidConditional() {
		tryToParse("2 > 3 ? 3", "", DELConditionalExpression.class, null, true);
	}

	/*public void test25() throws java.text.ParseException {
		Date date = new SimpleDateFormat("dd/MM/yy HH:mm").parse("17/12/07 15:55");
		SimpleDateFormat localeDateFormat = new SimpleDateFormat();
		tryToParse("(([dd/MM/yy HH:mm,17/12/07 12:54] + [3h] ) + [1min])",
				"[" + localeDateFormat.toPattern() + "," + localeDateFormat.format(date) + "]", false);
	}
	
	public void test26() throws java.text.ParseException {
		Date date = new SimpleDateFormat("dd/MM/yy HH:mm").parse("17/12/07 15:55");
		SimpleDateFormat localeDateFormat = new SimpleDateFormat();
		tryToParse("([dd/MM/yy HH:mm,17/12/07 12:54] + ( [3h] + [1min]))",
				"[" + localeDateFormat.toPattern() + "," + localeDateFormat.format(date) + "]", false);
	}
	*/

	public void testCast() {
		tryToParse("($java.lang.Integer)2", "($java.lang.Integer)2", DELCastExpression.class, null, false);
	}

	public void testCast2() {
		tryToParse("($java.lang.Integer)2+(($java.lang.Integer)2+($java.lang.Double)2)",
				"(($java.lang.Integer)2 + (($java.lang.Integer)2 + ($java.lang.Double)2))", DELBinaryOperatorExpression.class, null, false);
	}

	public void testInvalidCast() {
		tryToParse("(java.lang.Integer)2", "", DELCastExpression.class, null, true);
	}

	public void testParameteredCast() {
		tryToParse("($java.util.List<$java.lang.String>)data.list", "($java.util.List<$java.lang.String>)data.list",
				DELCastExpression.class, null, false);
	}

	public void testParameteredCast2() {
		tryToParse("($java.util.Hashtable<$java.lang.String,$java.util.List<$java.lang.String>>)data.map",
				"($java.util.Hashtable<$java.lang.String,$java.util.List<$java.lang.String>>)data.map", DELCastExpression.class, null,
				false);
	}

	public void testAccentCharacter() {
		tryToParse("flexoConcept.unité", "flexoConcept.unité", BindingValue.class, null, false);
	}

}
