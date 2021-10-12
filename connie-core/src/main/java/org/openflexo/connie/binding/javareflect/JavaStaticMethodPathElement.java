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

package org.openflexo.connie.binding.javareflect;

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
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionTransformer;
import org.openflexo.connie.type.TypeUtils;

/**
 * Model a java call which is a call to a method and with some arguments
 * 
 * @author sylvain
 * 
 */
public class JavaStaticMethodPathElement extends FunctionPathElement<JavaStaticMethodDefinition> {

	static final Logger logger = Logger.getLogger(JavaStaticMethodPathElement.class.getPackage().getName());

	private JavaBasedBindingFactory bindingFactory;
	private Type type;

	public JavaStaticMethodPathElement(Type type, String methodName, List<DataBinding<?>> args, JavaBasedBindingFactory bindingFactory) {
		super(null, methodName, null, args);
		this.bindingFactory = bindingFactory;
		this.type = type;
	}

	public JavaStaticMethodPathElement(Type type, JavaStaticMethodDefinition method, List<DataBinding<?>> args,
			JavaBasedBindingFactory bindingFactory) {
		super(null, method.getName(), method, args);
		this.bindingFactory = bindingFactory;
		this.type = type;
		setFunction(method);
	}

	final public JavaStaticMethodDefinition getMethodDefinition() {
		return getFunction();
	}

	public String getMethodName() {
		if (getMethodDefinition() != null) {
			return getMethodDefinition().getMethod().getName();
		}
		return getParsed();
	}

	@Override
	public void setFunction(JavaStaticMethodDefinition function) {
		super.setFunction(function);
		if (function != null) {
			setType(function.getMethod().getGenericReturnType());
		}
	}

	@Override
	public Type getType() {
		if (getMethodDefinition() != null) {
			return getMethodDefinition().getReturnType();
		}
		return type;
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
	public boolean supportsNullValues() {
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
				if (getArgumentValue(a) != null)
					args[i] = TypeUtils.castTo(getArgumentValue(a).getBindingValue(context),
							getMethodDefinition().getMethod().getGenericParameterTypes()[i]);
			} catch (ReflectiveOperationException e) {
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
			logger.warning(warningMessage.toString());
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
			logger.warning(sb.toString());
			/*e.printStackTrace();
			logger.info("Caused by:");
			e.getTargetException().printStackTrace();*/
			e.getTargetException().printStackTrace();
			throw new InvocationTargetTransformException(e);
		}
		return null;

	}

	private final Map<ExpressionTransformer, JavaStaticMethodPathElement> transformedPathElements = new HashMap<>();

	@Override
	public JavaStaticMethodPathElement transform(ExpressionTransformer transformer) throws TransformException {
		JavaStaticMethodPathElement returned = transformedPathElements.get(transformer);
		if (returned == null) {
			returned = makeTransformedPathElement(transformer);
			transformedPathElements.put(transformer, returned);
		}
		else {
			updateTransformedPathElement(returned, transformer);
		}
		return returned;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(TypeUtils.simpleRepresentation(getType()) + "." + getMethodName() + "(");
		boolean isFirst = true;
		if (getFunctionArguments() != null) {
			for (FunctionArgument arg : getFunctionArguments()) {
				sb.append((isFirst ? "" : ",") + getArgumentValue(arg));
				isFirst = false;
			}
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getSerializationRepresentation() {
		// if (serializationRepresentation == null) {
		StringBuffer returned = new StringBuffer();
		returned.append(TypeUtils.simpleRepresentation(getType()) + ".");
		if (getFunction() != null) {
			returned.append(getFunction().getName());
		}
		else {
			returned.append(getParsed());
		}
		returned.append("(");
		boolean isFirst = true;
		for (DataBinding<?> arg : getArguments()) {
			returned.append((isFirst ? "" : ",") + arg);
			isFirst = false;
		}
		returned.append(")");

		// serializationRepresentation = returned.toString();
		// }
		// return serializationRepresentation;
		return returned.toString();
	}

	private JavaStaticMethodPathElement makeTransformedPathElement(ExpressionTransformer transformer) throws TransformException {

		boolean hasBeenTransformed = false;
		List<DataBinding<?>> transformedArgs = new ArrayList<>();

		for (DataBinding<?> argValue : getArguments()) {
			if (argValue != null /*&& argValue.isValid()*/) {
				Expression currentExpression = argValue.getExpression();
				if (currentExpression != null) {
					Expression transformedExpression = currentExpression.transform(transformer);
					if (!transformedExpression.equals(currentExpression)) {
						hasBeenTransformed = true;
						DataBinding<?> newTransformedBinding = new DataBinding<>(argValue.getOwner(), argValue.getDeclaredType(),
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
			}
		}

		if (!hasBeenTransformed) {
			return this;
		}

		// TODO : getParsed() might be transformed, too
		// return new JavaStaticMethodPathElement(getParent(), getParsed(), getMethodDefinition(), transformedArgs);

		if (getMethodDefinition() != null) {
			return new JavaStaticMethodPathElement(getType(), getMethodDefinition(), transformedArgs, bindingFactory);
		}
		else {
			return new JavaStaticMethodPathElement(getType(), getParsed(), transformedArgs, bindingFactory);
		}
	}

	private JavaStaticMethodPathElement updateTransformedPathElement(JavaStaticMethodPathElement transformedPathElement,
			ExpressionTransformer transformer) throws TransformException {

		for (FunctionArgument arg : getFunctionArguments()) {
			DataBinding<?> argValue = getArgumentValue(arg);
			if (argValue != null && argValue.isValid()) {
				Expression currentExpression = argValue.getExpression();
				if (currentExpression != null) {
					Expression transformedExpression = currentExpression.transform(transformer);
					if (!transformedExpression.equals(currentExpression)) {
						DataBinding<?> transformedBinding = transformedPathElement.getArgumentValue(arg.getArgumentName());
						transformedBinding.setExpression(transformedExpression);
						// TODO: better to do i think
						transformedBinding.isValid();
					}
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

	@Override
	public BindingPathCheck checkBindingPathIsValid(IBindingPathElement currentElement, Type currentType) {

		BindingPathCheck check = new BindingPathCheck();

		check.invalidBindingReason = "J'aime pas les static methods";
		check.valid = false;
		return check;
	}

	@Override
	public boolean requiresContext() {
		return false;
	}

	@Override
	public boolean isResolved() {
		return getMethodDefinition() != null;
	}

	@Override
	public void resolve() {
		JavaStaticMethodDefinition function = (JavaStaticMethodDefinition) bindingFactory.retrieveFunction(getType(), getParsed(),
				getArguments());
		setFunction(function);
		if (function == null) {
			logger.warning("cannot find method " + getParsed() + " for " + getParent() + " with arguments " + getArguments());
		}
	}

}
