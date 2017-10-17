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

import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.annotations.NotificationUnsafe;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionTransformer;
import org.openflexo.connie.type.TypeUtils;

/**
 * Modelize a java call which is a call to a method and with some arguments
 * 
 * @author sylvain
 * 
 */
public class JavaMethodPathElement extends FunctionPathElement {

	static final Logger LOGGER = Logger.getLogger(JavaMethodPathElement.class.getPackage().getName());

	public JavaMethodPathElement(IBindingPathElement parent, MethodDefinition method, List<DataBinding<?>> args) {
		super(parent, method, args);
		if (getMethodDefinition() != null) {
			for (FunctionArgument arg : getMethodDefinition().getArguments()) {
				DataBinding<?> argValue = getParameter(arg);
				if (argValue != null) {
					argValue.setDeclaredType(arg.getArgumentType());
				}
			}
		}
		setType(getMethodDefinition().getMethod().getGenericReturnType());
	}

	final public MethodDefinition getMethodDefinition() {
		return getFunction();
	}

	@Override
	public MethodDefinition getFunction() {
		return (MethodDefinition) super.getFunction();
	}

	public String getMethodName() {
		return getMethodDefinition().getMethod().getName();
	}

	@Override
	public Type getType() {
		if (customType != null) {
			if (TypeUtils.isGeneric(customType)) {
				return TypeUtils.makeInstantiatedType(customType, getParent().getType());
			}
			return customType;
		}
		if (getMethodDefinition() != null) {
			return TypeUtils.makeInstantiatedType(getMethodDefinition().getMethod().getGenericReturnType(), getParent().getType());
		}
		return super.getType();
	}

	private Type customType = null;

	@Override
	public void setType(Type type) {
		customType = type;
	}

	/**
	 * Return boolean indicating if this {@link BindingPathElement} is notification-safe (all modifications of data are notified using
	 * {@link PropertyChangeSupport} scheme)<br>
	 * 
	 * A {@link JavaPropertyPathElement} is notification-safe when related method is not tagged with {@link NotificationUnsafe} annotation
	 * 
	 * Otherwise return true
	 * 
	 * @return
	 */
	@Override
	public boolean isNotificationSafe() {

		Method m = getMethodDefinition().getMethod();
		if (m == null || m.getAnnotation(NotificationUnsafe.class) != null) {
			return false;
		}
		return true;
	}

	@Override
	public String getLabel() {
		return getMethodDefinition().getLabel();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return getMethodDefinition().getTooltipText(resultingType);
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException {

		// System.out.println("evaluate " + getMethodDefinition().getSignature() + " for " + target);

		Object[] args = new Object[getFunction().getArguments().size()];
		int i = 0;

		for (Function.FunctionArgument a : getFunction().getArguments()) {
			try {
				args[i] = TypeUtils.castTo(getParameter(a).getBindingValue(context),
						getMethodDefinition().getMethod().getGenericParameterTypes()[i]);
			} catch (InvocationTargetException e) {
				throw new InvocationTargetTransformException(e);
			}
			i++;
		}
		try {
			return getMethodDefinition().getMethod().invoke(target, args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer(
					"While evaluating method " + getMethodDefinition().getMethod() + " exception occured: " + e.getMessage());
			warningMessage.append(", object = " + target);
			for (i = 0; i < getFunction().getArguments().size(); i++) {
				warningMessage.append(", arg[" + i + "] = " + args[i]);
			}
			LOGGER.warning(warningMessage.toString());
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// e.getTargetException().printStackTrace();
			StringBuffer sb = new StringBuffer();
			sb.append("InvocationTargetException " + e.getTargetException().getClass().getSimpleName() + " : "
					+ e.getTargetException().getMessage() + " while evaluating method " + getMethodDefinition().getMethod()
					+ " with args: ");
			for (int j = 0; j < args.length; j++) {
				sb.append("arg " + j + " = " + args[j] + " ");
			}
			LOGGER.warning(sb.toString());
			/*e.printStackTrace();
			logger.info("Caused by:");
			e.getTargetException().printStackTrace();*/
			throw new InvocationTargetTransformException(e);
		}
		return null;

	}

	private final Map<ExpressionTransformer, JavaMethodPathElement> transformedPathElements = new HashMap<>();

	@Override
	public JavaMethodPathElement transform(ExpressionTransformer transformer) throws TransformException {
		JavaMethodPathElement returned = transformedPathElements.get(transformer);
		if (returned == null) {
			System.out.println("On recalcule un JavaMethodPathElement pour " + this + " transformer=" + transformer);
			returned = makeTransformedPathElement(transformer);
			transformedPathElements.put(transformer, returned);
			System.out.println("CREATE On transforme " + toString() + " en " + returned.toString());
		}
		else {
			System.out.println("Pas la peine de refaire un JavaMethodPathElement pour " + this + " transformer=" + transformer);
			System.out.println("On met a jour quand meme");
			updateTransformedPathElement(returned, transformer);
			System.out.println("UPDATE On transforme " + toString() + " en " + returned.toString());
		}
		return returned;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getMethodName() + "(");
		boolean isFirst = true;
		for (FunctionArgument arg : getArguments()) {
			sb.append((isFirst ? "" : ",") + getParameter(arg));
			isFirst = false;
		}
		sb.append(")");
		return sb.toString();
	}

	private JavaMethodPathElement makeTransformedPathElement(ExpressionTransformer transformer) throws TransformException {

		boolean hasBeenTransformed = false;
		List<DataBinding<?>> transformedArgs = new ArrayList<>();

		for (FunctionArgument arg : getArguments()) {
			DataBinding<?> argValue = getParameter(arg);
			if (argValue.isValid()) {
				Expression currentExpression = argValue.getExpression();
				Expression transformedExpression = currentExpression.transform(transformer);
				if (!transformedExpression.equals(currentExpression)) {
					hasBeenTransformed = true;
					DataBinding<?> newTransformedBinding = new DataBinding<Object>(argValue.getOwner(), argValue.getDeclaredType(),
							argValue.getBindingDefinitionType(), false);
					newTransformedBinding.setExpression(transformedExpression);
					// TODO: better to do i think
					newTransformedBinding.isValid();
					transformedArgs.add(newTransformedBinding);
					// System.out.println(
					// "On a transforme " + argValue + " en " + newTransformedBinding + " valid=" + newTransformedBinding.isValid());
					hasBeenTransformed = true;
				}
				else {
					transformedArgs.add(argValue);
				}
			}
			else {
				transformedArgs.add(argValue);
			}
		}

		if (!hasBeenTransformed) {
			return this;
		}

		return new JavaMethodPathElement(getParent(), getMethodDefinition(), transformedArgs);
	}

	private JavaMethodPathElement updateTransformedPathElement(JavaMethodPathElement transformedPathElement,
			ExpressionTransformer transformer) throws TransformException {

		for (FunctionArgument arg : getArguments()) {
			DataBinding<?> argValue = getParameter(arg);
			if (argValue.isValid()) {
				Expression currentExpression = argValue.getExpression();
				Expression transformedExpression = currentExpression.transform(transformer);
				if (!transformedExpression.equals(currentExpression)) {
					DataBinding<?> transformedBinding = transformedPathElement.getParameter(arg.getArgumentName());
					transformedBinding.setExpression(transformedExpression);
					// TODO: better to do i think
					transformedBinding.isValid();
				}
			}
		}
		return transformedPathElement;
	}

	// Stores temporary DataBinding created
	// private final List<DataBinding<?>> transformedDataBindings = new ArrayList<>();

	/*public void clearTransformedDataBinding() {
		for (DataBinding<?> db : transformedDataBindings) {
			db.delete();
		}
	}*/

}
