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

package org.openflexo.connie;

import java.lang.reflect.InvocationTargetException;

import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.BindingValue.AbstractBindingPathElement;
import org.openflexo.connie.expr.BindingValue.NormalBindingPathElement;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionTransformer;
import org.openflexo.connie.expr.parser.ExpressionParser;
import org.openflexo.connie.expr.parser.ParseException;
import org.openflexo.connie.java.JavaBindingFactory;
import org.openflexo.kvc.InvalidKeyValuePropertyException;

/**
 * Utility class allowing to compute binding value over an expression and a given object.<br>
 * Expression must be expressed with or without supplied object (when mentioned, use "object." prefix).<br>
 * Considering we are passing a String, valid binding path are for example:
 * <ul>
 * <li>toString</li>
 * <li>toString()</li>
 * <li>toString()+' hash='+object.hashCode()</li>
 * <li>substring(6,11)</li>
 * <li>substring(3,length()-2)+' hash='+hashCode()</li>
 * </ul>
 * 
 * @author sylvain
 * 
 */
final public class BindingEvaluator extends DefaultBindable implements BindingEvaluationContext {

	private static final BindingFactory BINDING_FACTORY = new JavaBindingFactory();

	private Object object;
	private BindingDefinition bindingDefinition;
	private BindingModel bindingModel;

	private BindingEvaluator(Object object) {
		this.object = object;

		bindingDefinition = new BindingDefinition("object", object.getClass(), DataBinding.BindingDefinitionType.GET, true);
		bindingModel = new BindingModel();
		bindingModel.addToBindingVariables(new BindingVariable("object", object.getClass()));
	}

	public void delete() {
		object = null;
		bindingDefinition = null;
		bindingModel.delete();
		bindingModel = null;
	}

	private static String normalizeBindingPath(String bindingPath) {
		Expression expression = null;
		try {
			expression = ExpressionParser.parse(bindingPath);

			expression = expression.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValue) {
						BindingValue bv = (BindingValue) e;
						if (bv.getParsedBindingPath().size() > 0) {
							AbstractBindingPathElement firstPathElement = bv.getParsedBindingPath().get(0);
							if (!(firstPathElement instanceof NormalBindingPathElement)
									|| !((NormalBindingPathElement) firstPathElement).property.equals("object")) {
								bv.getParsedBindingPath().add(0, new NormalBindingPathElement("object"));
							}
						}
						return bv;
					}
					return e;
				}
			});

			return expression.toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
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

	@Override
	public String getDeletedProperty() {
		return null;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}

	private Object evaluate(String bindingPath)
			throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, InvocationTargetException {
		// System.out.println("Evaluating " + bindingPath);
		String normalizedBindingPath = normalizeBindingPath(bindingPath);
		// System.out.println("Normalize " + bindingPath + " to " + normalizedBindingPath);
		DataBinding<?> binding = new DataBinding<>(normalizedBindingPath, this, Object.class, DataBinding.BindingDefinitionType.GET);
		binding.setDeclaredType(Object.class);
		binding.setBindingDefinitionType(BindingDefinitionType.GET);

		// System.out.println("Binding = " + binding + " valid=" + binding.isValid() + " as " + binding.getClass());
		if (!binding.isValid()) {
			// System.out.println("not valid: " + binding.invalidBindingReason());
			throw new InvalidKeyValuePropertyException(
					"Cannot interpret " + normalizedBindingPath + " for object of type " + object.getClass());
		}
		return binding.getBindingValue(this);
	}

	public static Object evaluateBinding(String bindingPath, Object object)
			throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, InvocationTargetException {

		BindingEvaluator evaluator = new BindingEvaluator(object);
		Object returned = evaluator.evaluate(bindingPath);
		evaluator.delete();
		return returned;
	}

	public static void main(String[] args) {
		String thisIsATest = "Hello world, this is a test";
		try {
			System.out.println(evaluateBinding("toString", thisIsATest));
			System.out.println(evaluateBinding("toString()", thisIsATest));
			System.out.println(evaluateBinding("toString()+' hash='+object.hashCode()", thisIsATest));
			System.out.println(evaluateBinding("substring(6,11)", thisIsATest));
			System.out.println(evaluateBinding("substring(3,length()-2)+' hash='+hashCode()", thisIsATest));
		} catch (InvalidKeyValuePropertyException e) {
			e.printStackTrace();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
