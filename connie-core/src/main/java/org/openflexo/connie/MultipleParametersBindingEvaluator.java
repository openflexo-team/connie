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

import java.util.List;
import java.util.Map;

import org.openflexo.connie.binding.javareflect.InvalidKeyValuePropertyException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.BindingValue.AbstractBindingPathElement;
import org.openflexo.connie.expr.BindingValue.NormalBindingPathElement;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionTransformer;

/**
 * Utility class allowing to compute binding value over an expression and a given set of objects.<br>
 * Expression must be expressed with or without supplied object (when mentioned, use "this." prefix).<br>
 * 
 * Syntax is this:
 * 
 * <pre>
 * {$variable1}+' '+{$variable2}+' !'"
 * </pre>
 * 
 * for an expression with the two variables variable1 and variable2
 * 
 * @author sylvain
 * 
 */
public abstract class MultipleParametersBindingEvaluator extends DefaultBindable implements BindingEvaluationContext {

	private final BindingFactory bindingFactory;

	private Map<String, Object> objects;
	private BindingModel bindingModel;

	protected MultipleParametersBindingEvaluator(Map<String, Object> objects) {
		this(objects, null);
	}

	protected MultipleParametersBindingEvaluator(Map<String, Object> objects, BindingFactory bindingFactory) {
		this.objects = objects;

		bindingModel = new BindingModel();

		this.bindingFactory = bindingFactory;

		for (String variableName : objects.keySet()) {
			Object value = objects.get(variableName);
			bindingModel.addToBindingVariables(new BindingVariable(variableName, getBindingFactory().getTypeForObject(value)));
		}

	}

	public void delete() {
		bindingModel.delete();
		bindingModel = null;
		objects.clear();
		objects = null;
	}

	protected static String extractParameters(String bindingPath, List<String> parameters, Object... args) {
		int index = 0;
		String returned = bindingPath;
		while (returned.contains("{$")) {
			int startIndex = returned.indexOf("{$");
			int endIndex = returned.indexOf("}", startIndex);
			String parameterName = returned.substring(startIndex + 2, endIndex);
			// System.out.println("Found at index " + index + " " + parameterName + "=" + args[index]);
			if (index < args.length && args[index] == null) {
				parameterName = "null";
			}
			parameters.add(parameterName);
			returned = returned.substring(0, startIndex) + parameterName + returned.substring(endIndex + 1);
			index++;
		}
		return returned;
	}

	protected String normalizeBindingPath(String bindingPath, List<String> parameters, BindingFactory bindingFactory) {
		Expression expression = null;
		try {
			expression = bindingFactory.parseExpression(bindingPath, this);
			// expression = ExpressionParser.parse(bindingPath);
			if (expression != null) {
				expression = expression.transform(new ExpressionTransformer() {
					@Override
					public Expression performTransformation(Expression e) throws org.openflexo.connie.exception.TransformException {
						if (e instanceof BindingValue) {
							BindingValue bv = (BindingValue) e;
							if (bv.getParsedBindingPath().size() > 0) {
								AbstractBindingPathElement firstPathElement = bv.getParsedBindingPath().get(0);
								if (!(firstPathElement instanceof NormalBindingPathElement)
										|| (!((NormalBindingPathElement) firstPathElement).property.equals("this"))
												&& !parameters.contains(((NormalBindingPathElement) firstPathElement).property)) {
									bv.getParsedBindingPath().add(0, new NormalBindingPathElement("this"));
									bv.markedAsToBeReanalized();
								}
							}
							return bv;
						}
						return e;
					}
				});
				return expression.toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (org.openflexo.connie.exception.TransformException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		return bindingModel;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return bindingFactory;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		return objects.get(variable.getVariableName());
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

	protected Object evaluate(String aBindingPath)
			throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, ReflectiveOperationException {
		String normalizedBindingPath = aBindingPath;
		DataBinding<?> binding = new DataBinding<>(normalizedBindingPath, this, Object.class, DataBinding.BindingDefinitionType.GET);
		// FD redondant : binding.setDeclaredType(Object.class);
		// FD redondant : binding.setBindingDefinitionType(BindingDefinitionType.GET);

		if (!binding.isValid()) {
			System.out.println("Invalid binding: " + binding);
			System.out.println("not valid: " + binding.invalidBindingReason());
			System.out.println("bm=" + getBindingModel());
			throw new InvalidKeyValuePropertyException("Cannot interpret " + normalizedBindingPath);
		}
		return binding.getBindingValue(this);
	}

}
