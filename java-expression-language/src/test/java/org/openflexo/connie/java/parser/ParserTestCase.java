package org.openflexo.connie.java.parser;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.ContextualizedBindable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultContextualizedBindable;
import org.openflexo.connie.ParseException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionEvaluator;
import org.openflexo.connie.java.JavaTypingSpace;
import org.openflexo.connie.java.expr.JavaExpressionEvaluator;
import org.openflexo.connie.java.expr.JavaPrettyPrinter;

import junit.framework.TestCase;

public abstract class ParserTestCase extends TestCase {

	private JavaPrettyPrinter prettyPrinter;
	private JavaTypingSpace typingSpace;
	private ContextualizedBindable bindable;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prettyPrinter = new JavaPrettyPrinter();
	}

	public JavaTypingSpace getTypingSpace() {
		return typingSpace;
	}

	protected Expression tryToParse(String anExpression, String expectedEvaluatedExpression,
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

			typingSpace = new JavaTypingSpace();
			bindable = new DefaultContextualizedBindable(typingSpace) {
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
					return null;
				}

			};
			System.out.println("Parsing... " + anExpression);
			Expression parsed = ExpressionParser.parse(anExpression, bindable);
			System.out.println("parsed=" + parsed);
			Expression evaluated = parsed.evaluate(new BindingEvaluationContext() {
				@Override
				public Object getValue(BindingVariable variable) {
					return null;
				}

				@Override
				public ExpressionEvaluator getEvaluator() {
					return new JavaExpressionEvaluator(this);
				}
			});

			System.out.println("evaluated=" + evaluated);
			System.out.println("Successfully parsed as : " + parsed.getClass().getSimpleName());
			System.out.println("Normalized: " + prettyPrinter.getStringRepresentation(parsed, bindable));
			System.out.println("Evaluated: " + prettyPrinter.getStringRepresentation(evaluated, bindable));
			if (shouldFail) {
				fail();
			}
			assertTrue(expectedExpressionClass.isAssignableFrom(parsed.getClass()));
			if (expectedEvaluatedExpression != null) {
				assertEquals(expectedEvaluatedExpression, prettyPrinter.getStringRepresentation(evaluated, bindable));
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
		} catch (ReflectiveOperationException e) {
			fail();
			return null;
		}

	}

}
