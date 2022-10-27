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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.javareflect.InvalidKeyValuePropertyException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionTransformer;
import org.openflexo.connie.expr.UnresolvedBindingVariable;
import org.openflexo.connie.type.TypingSpace;

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
public abstract class BindingEvaluator extends AbstractBindingEvaluator {

	private static final String OBJECT = "object";

	private Object object;

	protected BindingEvaluator(Object object, Type objectType, BindingFactory bindingFactory, TypingSpace typingSpace) {
		super(bindingFactory, typingSpace);
		this.object = object;
		getBindingModel().addToBindingVariables(new BindingVariable(OBJECT, objectType));
	}

	@Override
	public void delete() {
		object = null;
		super.delete();
	}

	String normalizeBindingPath(String bindingPath) {

		if (bindingPath.contains("this")) {
			bindingPath = bindingPath.replaceAll("this", "object");
		}

		Expression expression = null;
		try {
			expression = getBindingFactory().parseExpression(bindingPath, this);
			if (expression != null) {
				expression = expression.transform(new ExpressionTransformer() {
					@Override
					public Expression performTransformation(Expression e) throws TransformException {
						if (e instanceof BindingValue) {
							BindingValue bindingPath = (BindingValue) e;
							if (bindingPath.getBindingVariable() == null) {
								UnresolvedBindingVariable objectBV = new UnresolvedBindingVariable(OBJECT);
								bindingPath.setBindingVariable(objectBV);
								return bindingPath;
							}
							else if (!bindingPath.getBindingVariable().getVariableName().equals(OBJECT)) {
								UnresolvedBindingVariable objectBV = new UnresolvedBindingVariable(OBJECT);
								List<BindingPathElement> bp2 = new ArrayList<>(bindingPath.getBindingPath());
								bp2.add(0, getBindingFactory().makeSimplePathElement(objectBV,
										bindingPath.getBindingVariable().getVariableName(), BindingEvaluator.this));
								bindingPath.setBindingVariable(objectBV);
								bindingPath.setBindingPath(bp2);
							}
							return bindingPath;
						}
						return e;
					}
				});
				return expression.toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		return object;
	}

	@Override
	protected Object evaluate(String bindingPath)
			throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, ReflectiveOperationException {
		String normalizedBindingPath = normalizeBindingPath(bindingPath);
		DataBinding<?> binding = new DataBinding<>(normalizedBindingPath, this, Object.class, DataBinding.BindingDefinitionType.GET);
		if (!binding.isValid()) {
			System.out.println("not valid: " + binding.invalidBindingReason());
			throw new InvalidKeyValuePropertyException(
					"Cannot interpret " + normalizedBindingPath + " for object of type " + object.getClass());
		}
		return binding.getBindingValue(this);
	}

}
