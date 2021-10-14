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
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimpleMethodPathElement;
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
public class JavaInstanceMethodPathElement extends SimpleMethodPathElement<JavaInstanceMethodDefinition> {

	static final Logger logger = Logger.getLogger(JavaInstanceMethodPathElement.class.getPackage().getName());

	private JavaBasedBindingFactory bindingFactory;

	public JavaInstanceMethodPathElement(IBindingPathElement parent, String methodName, List<DataBinding<?>> args,
			JavaBasedBindingFactory bindingFactory) {
		super(parent, methodName, args);
		this.bindingFactory = bindingFactory;
	}

	public JavaInstanceMethodPathElement(IBindingPathElement parent, JavaInstanceMethodDefinition method, List<DataBinding<?>> args,
			JavaBasedBindingFactory bindingFactory) {
		super(parent, method, args);
		this.bindingFactory = bindingFactory;
	}

	final public JavaInstanceMethodDefinition getMethodDefinition() {
		return getFunction();
	}

	@Override
	public String getMethodName() {
		if (getMethodDefinition() != null) {
			return getMethodDefinition().getMethod().getName();
		}
		return getParsed();
	}

	@Override
	public void setFunction(JavaInstanceMethodDefinition function) {
		super.setFunction(function);
		if (function != null) {
			setType(function.getMethod().getGenericReturnType());
		}
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

	private final Map<ExpressionTransformer, JavaInstanceMethodPathElement> transformedPathElements = new HashMap<>();

	@Override
	public JavaInstanceMethodPathElement transform(ExpressionTransformer transformer) throws TransformException {
		JavaInstanceMethodPathElement returned = transformedPathElements.get(transformer);
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
		sb.append(getMethodName() + "(");
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

	private JavaInstanceMethodPathElement makeTransformedPathElement(ExpressionTransformer transformer) throws TransformException {

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

		if (getMethodDefinition() != null) {
			return new JavaInstanceMethodPathElement(getParent(), getMethodDefinition(), transformedArgs, bindingFactory);
		}
		else {
			return new JavaInstanceMethodPathElement(getParent(), getParsed(), transformedArgs, bindingFactory);
		}
	}

	private JavaInstanceMethodPathElement updateTransformedPathElement(JavaInstanceMethodPathElement transformedPathElement,
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

	@Override
	public BindingPathCheck checkBindingPathIsValid(IBindingPathElement parentElement, Type parentType) {

		BindingPathCheck check = super.checkBindingPathIsValid(parentElement, parentType);

		check.returnedType = TypeUtils.makeInstantiatedType(getType(), parentType);
		check.valid = true;
		return check;
	}

	@Override
	public boolean requiresContext() {
		return true;
	}

	@Override
	public boolean isResolved() {
		return getMethodDefinition() != null;
	}

	@Override
	public void resolve() {
		if (getParent() != null) {
			JavaInstanceMethodDefinition function = (JavaInstanceMethodDefinition) bindingFactory.retrieveFunction(getParent().getType(),
					getParsed(), getArguments());
			setFunction(function);
			if (function == null) {
				logger.warning("cannot find method " + getParsed() + " for " + getParent() + " with arguments " + getArguments());
			}
		}
		else {
			logger.warning("cannot find parent for " + this);
			// Thread.dumpStack();
			// System.exit(-1);
		}
	}

	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		if (isResolved()) {
			result = prime * result + Objects.hash(getMethodDefinition());
		}
		else {
			result = prime * result + Objects.hash(getParsed());
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaInstanceMethodPathElement other = (JavaInstanceMethodPathElement) obj;
		if (isResolved() != other.isResolved()) {
			return false;
		}
		if (isResolved()) {
			return Objects.equals(getMethodDefinition(), other.getMethodDefinition());
		}
		else {
			return Objects.equals(getParsed(), other.getParsed());
		}
	}*:
	
	/*@Override
	public BindingPathCheck checkBindingPathIsValid(IBindingPathElement currentElement, Type currentType) {
	
		BindingPathCheck check = new BindingPathCheck();
	
		if (getFunction() == null) {
			check.invalidBindingReason = "invalid null function";
			check.valid = false;
			return check;
		}
	
		for (FunctionArgument arg : getFunction().getArguments()) {
			DataBinding<?> argValue = getParameter(arg);
			// System.out.println("Checking " + argValue + " valid="
			// + argValue.isValid());
			if (argValue == null) {
				check.invalidBindingReason = "Parameter value for function: " + getFunction() + " : " + "invalid null argument "
						+ arg.getArgumentName();
				check.valid = false;
				return check;
			}
			if (!argValue.isValid()) {
				check.invalidBindingReason = "Parameter value for function: " + getFunction() + " : " + "invalid argument "
						+ arg.getArgumentName() + " reason=" + argValue.invalidBindingReason();
				check.valid = false;
				return check;
			}
		}
	
		if (getParent() == null) {
			check.invalidBindingReason = "No parent for: " + this;
			check.valid = false;
			return check;
		}
	
		if (getParent() != currentElement) {
			check.invalidBindingReason = "Inconsistent parent for: " + this;
			check.valid = false;
			return check;
		}
	
		if (!TypeUtils.isTypeAssignableFrom(currentElement.getType(), getParent().getType(), true)) {
			check.invalidBindingReason = "Mismatched: " + currentElement.getType() + " and " + getParent().getType();
			check.valid = false;
			return check;
		}
	
		check.returnedType = TypeUtils.makeInstantiatedType(getType(), currentType);
		check.valid = true;
		return check;
	}*/

}
