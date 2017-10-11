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

package org.openflexo.connie.binding;

import java.util.List;

import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.Variable;
import org.openflexo.connie.expr.parser.ExpressionParser;
import org.openflexo.connie.expr.parser.ParseException;

import junit.framework.TestCase;

public class TestExpression extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testVariable1() {
		try {
			List<BindingValue> vars = ExpressionParser.parse("this+is+a+test").getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("this")));
			assertTrue(vars.contains(new Variable("is")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testVariable2() {
		try {
			List<BindingValue> vars = ExpressionParser.parse("i+(am-a/test)+2").getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("i")));
			assertTrue(vars.contains(new Variable("am")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testVariable3() {
		try {
			List<BindingValue> vars = ExpressionParser.parse("this.is.a.little.test+and+this+is.not()").getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new BindingValue("this.is.a.little.test")));
			assertTrue(vars.contains(new Variable("and")));
			assertTrue(vars.contains(new Variable("this")));
			assertTrue(vars.contains(new BindingValue("is.not()")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive1() {
		try {
			List<BindingValue> vars = ExpressionParser.parse("i+am+a+test").getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("i")));
			assertTrue(vars.contains(new Variable("am")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive2() {
		try {
			List<BindingValue> vars = ExpressionParser.parse("i+(am-a/test)+2").getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("i")));
			assertTrue(vars.contains(new Variable("am")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive3() {
		try {
			List<BindingValue> vars = ExpressionParser.parse("i.am.a.little.test+and+following+is.not()").getAllBindingValues();
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new BindingValue("i.am.a.little.test")));
			assertTrue(vars.contains(new Variable("and")));
			assertTrue(vars.contains(new Variable("following")));
			assertTrue(vars.contains(new BindingValue("is.not()")));
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
			// TODO Auto-generated catch block
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
