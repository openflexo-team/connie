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

package org.openflexo.connie.del;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.del.expr.DELExpressionEvaluator;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.ExpressionEvaluator;
import org.openflexo.connie.type.TypeUtils;

import com.google.common.reflect.TypeToken;

import junit.framework.TestCase;

public class TestBinding extends TestCase {

	private static final BindingFactory BINDING_FACTORY = new DELBindingFactory();
	private static final TestBindingContext BINDING_CONTEXT = new TestBindingContext();
	private static final TestBindingModel BINDING_MODEL = new TestBindingModel();

	public static class TestBindingContext extends DefaultBindable implements BindingEvaluationContext {

		public static String aString = "this is a test";
		public static boolean aBoolean = false;
		public static int anInt = 7;
		public static List<String> aList = new ArrayList<>();
		public static String aUTF8string = "Á à é ð";
		public static Float unMontant = Float.valueOf((float) 18.0);

		static {
			aList.add("this");
			aList.add("is");
			aList.add("a");
			aList.add("test");
		}

		@Override
		public BindingFactory getBindingFactory() {
			return BINDING_FACTORY;
		}

		@Override
		public BindingModel getBindingModel() {
			return BINDING_MODEL;
		}

		@Override
		public ExpressionEvaluator getEvaluator() {
			return new DELExpressionEvaluator(this);
		}

		@Override
		public Object getValue(BindingVariable variable) {

			if (variable.getVariableName().equals("aString")) {
				return aString;
			}
			else if (variable.getVariableName().equals("aBoolean")) {
				return aBoolean;
			}
			else if (variable.getVariableName().equals("anInt")) {
				return anInt;
			}
			else if (variable.getVariableName().equals("aList")) {
				return aList;
			}
			else if (variable.getVariableName().equals("unëChaÎneUnîcÔde")) {
				return aUTF8string;
			}
			else if (variable.getVariableName().equals("des€")) {
				return unMontant;
			}
			return null;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}
	}

	// String aString;
	// Boolean aBoolean;
	// List<String> aList;
	public static class TestBindingModel extends BindingModel {
		public TestBindingModel() {
			super();
			addToBindingVariables(new BindingVariable("aString", String.class));
			addToBindingVariables(new BindingVariable("unëChaÎneUnîcÔde", String.class));
			addToBindingVariables(new BindingVariable("des€", Float.TYPE));
			addToBindingVariables(new BindingVariable("aBoolean", Boolean.TYPE));
			addToBindingVariables(new BindingVariable("anInt", Integer.TYPE));
			addToBindingVariables(new BindingVariable("aList", new TypeToken<List<String>>() {
			}.getType()));
		}
	}

	/*public static class TestObject implements Bindable, BindingEvaluationContext {
	
		private Object object;
		private BindingDefinition bindingDefinition;
		private BindingModel bindingModel;
	
		private BindingEvaluator(Object object) {
			this.object = object;
			bindingDefinition = new BindingDefinition("object", object.getClass(), BindingDefinitionType.GET, true);
			bindingModel = new BindingModel();
			bindingModel.addToBindingVariables(new BindingVariableImpl(this, "object", object.getClass()));
			BINDING_FACTORY.setBindable(this);
		}
	
		private static String normalizeBindingPath(String bindingPath) {
			DefaultExpressionParser parser = new DefaultExpressionParser();
			Expression expression = null;
			try {
				expression = ExpressionParser.parse(bindingPath);
				expression = expression.transform(new ExpressionTransformer() {
					@Override
					public Expression performTransformation(Expression e) throws TransformException {
						if (e instanceof BindingValueAsExpression) {
							BindingValueAsExpression bv = (BindingValueAsExpression) e;
							if (bv.getBindingPath().size() > 0) {
								AbstractBindingPathElement firstPathElement = bv.getBindingPath().get(0);
								if (!(firstPathElement instanceof NormalBindingPathElement)
										|| !((NormalBindingPathElement) firstPathElement).property.equals("object")) {
									bv.getBindingPath().add(0, new NormalBindingPathElement("object"));
								}
							}
							return bv;
						}
						return e;
					}
				});
	
				return expression.toString();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (TransformException e) {
				e.printStackTrace();
			}
			return expression.toString();
		}
	
		@Override
		public BindingModel getBindingModel() {
			return bindingModel;
		}
	
		@Override
		public BindingFactory getBindingFactory() {
			return BINDING_FACTORY;
		}
	
		@Override
		public Object getValue(BindingVariable variable) {
			return object;
		}
	
	}
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString", thisIsATest, thisIsATest);
	}
	
	public void test2() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()", thisIsATest, thisIsATest);
	}
	
	public void test3() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,8)", thisIsATest, "llo wo");
	}
	
	public void test4() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,5*2-2)", thisIsATest, "llo wo");
	}
	
	public void test5() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+toString()", thisIsATest, "Hello world, this is a testHello world, this is a test");
	}
	
	public void test6() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+' hash='+object.hashCode()", thisIsATest, thisIsATest + " hash=" + thisIsATest.hashCode());
	}
	
	public void test7() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(0,5)+' '+substring(23,27).toUpperCase()", thisIsATest, "Hello TEST");
	}
	
	public void test8() {
		genericTest("object*2-7", 10, 13);
	}
	
	public void test9() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(3,length()-2)+' hash='+hashCode()", thisIsATest, "lo world, this is a te hash=" + thisIsATest.hashCode());
	}*/

	public static void genericTest(String bindingPath, Type expectedType, Object expectedResult) {

		System.out.println("Evaluate " + bindingPath);

		DataBinding<?> dataBinding = new DataBinding<>(bindingPath, BINDING_CONTEXT, expectedType, DataBinding.BindingDefinitionType.GET);

		/*	BINDING_FACTORY.setBindable(BINDING_CONTEXT);
			AbstractBinding binding = BINDING_FACTORY.convertFromString(bindingPath);
			binding.setBindingDefinition(new BindingDefinition("test", expectedType, BindingDefinitionType.GET, true));*/

		if (dataBinding.getExpression() != null) {
			System.out.println(
					"Parsed " + dataBinding + " as " + dataBinding.getExpression() + " of " + dataBinding.getExpression().getClass());

			if (!dataBinding.isValid()) {
				fail(dataBinding.invalidBindingReason());
			}

			Object evaluation = null;
			try {
				evaluation = dataBinding.getBindingValue(BINDING_CONTEXT);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
				fail();
			} catch (NullReferenceException e) {
				e.printStackTrace();
				fail();
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				fail();
			}
			System.out.println("Evaluated as " + evaluation);

			System.out.println("expectedResult = " + expectedResult + " of " + expectedResult.getClass());
			System.out.println("evaluation = " + evaluation + " of " + evaluation.getClass());

			assertEquals(expectedResult, TypeUtils.castTo(evaluation, expectedType));
		}
		else {
			System.out.println("Could not Parse " + dataBinding + " defined as " + dataBinding.toString());
			fail("Unparseable binding");

		}

		/*Object evaluatedResult = null;
		try {
			evaluatedResult = BindingEvaluator.evaluateBinding(bindingPath, object);
		} catch (InvalidKeyValuePropertyException e) {
			fail();
		} catch (TypeMismatchException e) {
			fail();
		} catch (NullReferenceException e) {
			fail();
		}
		System.out.println("Evaluated as " + evaluatedResult);
		
		if (expectedResult instanceof Number) {
			if (evaluatedResult instanceof Number) {
				assertEquals(((Number) expectedResult).doubleValue(), ((Number) evaluatedResult).doubleValue());
			} else {
				fail("Evaluated value is not a number (expected: " + expectedResult + ") but " + evaluatedResult);
			}
		} else {
			assertEquals(expectedResult, evaluatedResult);
		}*/
	}

	public void test1() {
		System.out.println("*********** test1");
		TestBindingContext.aString = "this is a test";
		genericTest("aString", String.class, "this is a test");
	}

	public void test2() {
		System.out.println("*********** test2");
		TestBindingContext.aString = "this is a test";
		genericTest("aString.substring(5,7)", String.class, "is");
	}

	public void test3() {
		System.out.println("*********** test3");
		TestBindingContext.aString = "this is a test";
		genericTest("aString.substring(anInt+3,anInt+7)", String.class, "test");
	}

	public void test4() {
		System.out.println("*********** test4");
		TestBindingContext.aString = "this is a test";
		genericTest("aString.length", Integer.class, 14);
	}

	public void test5() {
		System.out.println("*********** test5");
		TestBindingContext.aString = "this is a test";
		genericTest("aString.length+aList.size()", Integer.class, 18);
	}

	public void test6() {
		System.out.println("*********** test6");
		TestBindingContext.aString = "this is a test";
		genericTest("aString.length > aList.size()", Boolean.class, true);
	}

	public void test7() {
		System.out.println("*********** test7");
		TestBindingContext.aString = "this is a test";
		genericTest("aString.length > aList.size()", Boolean.TYPE, true);
	}

	public void test8() {
		System.out.println("*********** test8");
		TestBindingContext.aString = "this is a test";
		genericTest("aString == null", Boolean.TYPE, false);
	}

	public void test9() {
		System.out.println("*********** test9");
		TestBindingContext.aString = "this is a test";
		genericTest("aString == ''", Boolean.TYPE, false);
	}

	public void test10() {
		System.out.println("*********** test1O");
		TestBindingContext.aString = "";
		genericTest("aString == ''", Boolean.TYPE, true);
	}

	public void test11() {
		System.out.println("*********** test11");
		TestBindingContext.aString = "foo";
		genericTest("aString+((aString != 'foo' ? ('=' + aString) : ''))", String.class, "foo");
		TestBindingContext.aString = "foo2";
		genericTest("aString+((aString != 'foo' ? ('=' + aString) : ''))", String.class, "foo2=foo2");
	}

	public void test12() {
		System.out.println("*********** test12");
		genericTest("anInt > 2 ? 'anInt > 2' : 'anInt<=2' ", String.class, "anInt > 2");
	}

	public void test13() {
		System.out.println("*********** test13");
		genericTest("aString != null", Boolean.TYPE, true);
	}

	public void test14() {
		System.out.println("*********** test14");
		genericTest("1", Integer.TYPE, 1);
	}

	public void test15() {
		System.out.println("*********** test15");
		TestBindingContext.aString = "foo";
		genericTest("aString.length", Integer.TYPE, 3);
	}

	public void testUTF8_1() {
		System.out.println("***********  test UTF-8 1");
		TestBindingContext.aUTF8string = "à la même place";
		genericTest("unëChaÎneUnîcÔde == 'à la même place'", Boolean.TYPE, true);
	}

	public void testUTF8_2() {
		System.out.println("*********** test UTF-8 2");
		TestBindingContext.unMontant = Float.valueOf((float) 120.0);
		genericTest("des€ > 12.0", Boolean.TYPE, true);
	}

}
