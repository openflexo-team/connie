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

import java.util.List;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.ParseException;
import org.openflexo.connie.del.parser.ExpressionParser;
import org.openflexo.connie.expr.BindingPath;

import junit.framework.TestCase;

public class TestExpression extends TestCase {

	private static final BindingFactory BINDING_FACTORY = new DELBindingFactory();

	private static final Bindable BINDABLE = new DefaultBindable() {

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testVariable1() {
		try {
			List<BindingPath> vars = ExpressionParser.parse("this+is+a+test", BINDABLE).getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(BindingPath.parse("this", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("is", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("a", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("test", BINDABLE)));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testVariable2() {
		try {
			List<BindingPath> vars = ExpressionParser.parse("i+(am-a/test)+2", BINDABLE).getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(BindingPath.parse("i", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("am", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("a", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("test", BINDABLE)));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testVariable3() {
		try {
			List<BindingPath> vars = ExpressionParser.parse("this.is.a.little.test+and+this+is.not()", BINDABLE).getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(BindingPath.parse("this.is.a.little.test", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("and", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("this", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("is.not()", BINDABLE)));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive1() {
		try {
			List<BindingPath> vars = ExpressionParser.parse("i+am+a+test", BINDABLE).getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(BindingPath.parse("i", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("am", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("a", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("test", BINDABLE)));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive2() {
		try {
			List<BindingPath> vars = ExpressionParser.parse("i+(am-a/test)+2", BINDABLE).getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(BindingPath.parse("i", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("am", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("a", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("test", BINDABLE)));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive3() {
		try {
			List<BindingPath> vars = ExpressionParser.parse("i.am.a.little.test+and+following+is.not()", BINDABLE).getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(BindingPath.parse("i.am.a.little.test", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("and", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("following", BINDABLE)));
			assertTrue(vars.contains(BindingPath.parse("is.not()", BINDABLE)));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	/*public static Expression evaluate(Expression expr, final Hashtable<String, ?> variables) throws TypeMismatchException {
	
		try {
			System.out.println("On evalue " + expr);
			System.out.println("variables=" + variables);
			return expr.evaluate(new BindingEvaluationContext() {
				@Override
				public Object getValue(BindingVariable variable) {
					System.out.println("hop avec " + variable);
					System.out.println("On me demande " + variable.getVariableName() + " = " + variables.get(variable.toString()));
					return variables.get(variable.toString());
				}
			});
		} catch (NullReferenceException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void testEvaluate1() {
		try {
			Expression e = ExpressionParser.parse("a+(b-c)/2");
			Hashtable<String, Object> variables = new Hashtable<>();
			variables.put("a", 1);
			variables.put("b", 10);
			variables.put("c", 3);
			Expression evaluated = evaluate(e, variables);
			System.out.println("evaluated=" + evaluated);
			assertEquals(ExpressionParser.parse("4.5"), evaluated);
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testEvaluate2() {
		try {
			Expression e = ExpressionParser.parse("a+(b-2-c)/2");
			Hashtable<String, Object> variables = new Hashtable<>();
			variables.put("a", 1);
			variables.put("b", 10);
			Expression evaluated = evaluate(e, variables);
			System.out.println("evaluated=" + evaluated);
			assertEquals(ExpressionParser.parse("1+(8-c)/2"), evaluated);
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}*/
}
