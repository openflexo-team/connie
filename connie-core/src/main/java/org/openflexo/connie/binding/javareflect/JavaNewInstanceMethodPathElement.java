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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.NewInstancePathElementImpl;
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
public class JavaNewInstanceMethodPathElement extends NewInstancePathElementImpl<JavaConstructorDefinition> {

	static final Logger logger = Logger.getLogger(JavaNewInstanceMethodPathElement.class.getPackage().getName());

	private JavaBasedBindingFactory bindingFactory;

	public JavaNewInstanceMethodPathElement(Type type, IBindingPathElement parent, String constructorName, List<DataBinding<?>> args,
			Bindable bindable) {
		super(type, parent, constructorName, args, bindable);
		this.bindingFactory = (JavaBasedBindingFactory) bindable.getBindingFactory();
	}

	public JavaNewInstanceMethodPathElement(Type type, IBindingPathElement parent, JavaConstructorDefinition constructor,
			List<DataBinding<?>> args, Bindable bindable) {
		super(type, parent, constructor, args, bindable);
		this.bindingFactory = (JavaBasedBindingFactory) bindable.getBindingFactory();
	}

	@Override
	public void setFunction(JavaConstructorDefinition function) {
		super.setFunction(function);
		if (hasInnerAccess()) {
			// If we have inner access, we add a new null element at the beginning of the arguments list
			// (this is the hidden argument used by java reflection)
			getArguments().add(0, null);
			if (function != null) {
				// We have to force the declared type again, because a new hidden argument representing inner access was added
				for (FunctionArgument arg : function.getArguments()) {
					DataBinding<?> argValue = getArgumentValue(arg);
					if (argValue != null) {
						argValue.setDeclaredType(arg.getArgumentType());
					}
				}
				setType(function.getReturnType());
			}
		}
	}

	final public JavaConstructorDefinition getConstructorDefinition() {
		return getFunction();
	}

	@Override
	public String getLabel() {
		return "new " + TypeUtils.simpleRepresentation(getType());
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return getConstructorDefinition().getTooltipText(resultingType);
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException {

		// System.out.println("evaluate " + getConstructorDefinition().getSignature() + " for " + target);

		Object[] args = new Object[getFunction().getArguments().size()];
		int i = 0;

		for (Function.FunctionArgument a : getFunction().getArguments()) {
			try {
				if (getArgumentValue(a) != null) {
					DataBinding<?> valueBinding = getArgumentValue(a);
					args[i] = TypeUtils.castTo(valueBinding.getBindingValue(context),
							getConstructorDefinition().getConstructor().getGenericParameterTypes()[i]);
					/*System.out.println("Argument " + a.getArgumentName() + " / " + a.getArgumentType() + " values: " + valueBinding);
					System.out.println("BindingValue: " + valueBinding + " = " + valueBinding.getBindingValue(context));
					System.out.println("Valid: " + valueBinding);
					System.out.println("Reason: " + valueBinding.invalidBindingReason());*/
				}
			} catch (ReflectiveOperationException e) {
				throw new InvocationTargetTransformException(e);
			}
			i++;
		}

		if (hasInnerAccess()) {
			args[0] = target;
		}

		try {
			return getConstructorDefinition().getConstructor().newInstance(args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer(
					"While evaluating method " + getConstructorDefinition().getConstructor() + " exception occured: " + e.getMessage());
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
					+ e.getTargetException().getMessage() + " while evaluating constructor " + getConstructorDefinition().getConstructor()
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
		} catch (InstantiationException e) {
			// e.getTargetException().printStackTrace();
			StringBuffer sb = new StringBuffer();
			sb.append("InstantiationException " + " : " + e.getMessage() + " while evaluating constructor "
					+ getConstructorDefinition().getConstructor() + " with args: ");
			for (int j = 0; j < args.length; j++) {
				sb.append("arg " + j + " = " + args[j] + " ");
			}
			logger.warning(sb.toString());
			/*e.printStackTrace();
			logger.info("Caused by:");
			e.getTargetException().printStackTrace();*/
			throw new InvocationTargetTransformException(e);
		}
		return null;

	}

	private final Map<ExpressionTransformer, JavaNewInstanceMethodPathElement> transformedPathElements = new HashMap<>();

	@Override
	public JavaNewInstanceMethodPathElement transform(ExpressionTransformer transformer) throws TransformException {
		JavaNewInstanceMethodPathElement returned = transformedPathElements.get(transformer);
		if (returned == null) {
			// System.out.println("On recalcule un JavaMethodPathElement pour " + this + " transformer=" + transformer);
			returned = makeTransformedPathElement(transformer);
			transformedPathElements.put(transformer, returned);
			// System.out.println("CREATE On transforme " + toString() + " en " + returned.toString());
		}
		else {
			// System.out.println("Pas la peine de refaire un JavaMethodPathElement pour " + this + " transformer=" + transformer);
			// System.out.println("On met a jour quand meme");
			updateTransformedPathElement(returned, transformer);
			// System.out.println("UPDATE On transforme " + toString() + " en " + returned.toString());
		}
		return returned;
	}

	/*@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("new " + TypeUtils.simpleRepresentation(getType()) + "(");
		boolean isFirst = true;
		if (getFunctionArguments() != null) {
			for (FunctionArgument arg : getFunctionArguments()) {
				sb.append((isFirst ? "" : ",") + getArgumentValue(arg));
				isFirst = false;
			}
		}
		sb.append(")");
		return sb.toString();
	}*/

	@Override
	public String getSerializationRepresentation() {
		// if (serializationRepresentation == null) {
		StringBuffer returned = new StringBuffer();
		returned.append("new " + TypeUtils.simpleRepresentation(getType()) + "(");
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

	private JavaNewInstanceMethodPathElement makeTransformedPathElement(ExpressionTransformer transformer) throws TransformException {

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

		if (getConstructorDefinition() != null) {
			return new JavaNewInstanceMethodPathElement(getType(), getParent(), getConstructorDefinition(), transformedArgs, getBindable());
		}
		else {
			return new JavaNewInstanceMethodPathElement(getType(), getParent(), getParsed(), transformedArgs, getBindable());
		}

	}

	private JavaNewInstanceMethodPathElement updateTransformedPathElement(JavaNewInstanceMethodPathElement transformedPathElement,
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
	public BindingPathCheck checkBindingPathIsValid(IBindingPathElement parentElement, Type parentType) {

		BindingPathCheck check = super.checkBindingPathIsValid(parentElement, parentType);

		// TODO: some other checks ???
		check.returnedType = getType();
		if (!TypeUtils.isResolved(getType())) {
			check.valid = false;
			check.invalidBindingReason = "Unresolved type : " + getType();
		}
		else {
			check.valid = true;
		}

		return check;
	}

	@Override
	public boolean requiresContext() {
		return false;
	}

	@Override
	public boolean isResolved() {
		return getConstructorDefinition() != null;
	}

	@Override
	public void resolve() {
		if (bindingFactory != null) {
			JavaConstructorDefinition function = (JavaConstructorDefinition) bindingFactory.retrieveConstructor(getType(),
					getParent() != null ? getParent().getType() : null, getParsed(), getArguments());
			setFunction(function);
			if (function == null) {
				logger.warning("cannot find constructor " + getParsed() + " for type " + getType() + " with arguments " + getArguments());
			}
		}
	}

}
