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

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
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
public class JavaNewInstanceMethodPathElement extends FunctionPathElement<ConstructorDefinition> {

	static final Logger LOGGER = Logger.getLogger(JavaNewInstanceMethodPathElement.class.getPackage().getName());

	private DataBinding<?> innerAccess;

	public JavaNewInstanceMethodPathElement(IBindingPathElement parent, ConstructorDefinition constructor, DataBinding<?> innerAccess,
			List<DataBinding<?>> args) {
		super(parent, constructor, args);
		this.innerAccess = innerAccess;
		if (getConstructorDefinition() != null) {
			int i = 0;
			if (innerAccess != null) {
				for (FunctionArgument arg : getConstructorDefinition().getArguments()) {
					if (i == 0) {
						setParameter(arg, innerAccess);
					}
					else {
						setParameter(arg, args.get(i - 1));
					}
					i++;
				}
			}

			for (FunctionArgument arg : getConstructorDefinition().getArguments()) {
				DataBinding<?> argValue = getParameter(arg);
				if (argValue != null) {
					argValue.setDeclaredType(arg.getArgumentType());
				}
				// System.out.println(">>>" + arg.getArgumentName() + "/" + arg.getArgumentType() + " = " + argValue);
			}
		}
		setType(getConstructorDefinition().getReturnType());
	}

	final public ConstructorDefinition getConstructorDefinition() {
		return getFunction();
	}

	@Override
	public Type getType() {
		if (getConstructorDefinition() != null) {
			return getConstructorDefinition().getReturnType();
		}
		return null;
	}

	@Override
	public void setType(Type type) {
	}

	/**
	 * Always return false
	 * 
	 * @return
	 */
	@Override
	public boolean isNotificationSafe() {
		return false;
	}

	@Override
	public boolean supportsNullValues() {
		return true;
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

		System.out.println("evaluate " + getConstructorDefinition().getSignature() + " for " + target);

		Object[] args = new Object[getFunction().getArguments().size() /*+ (innerAccess != null ? 1 : 0)*/];
		int i = 0;

		for (Function.FunctionArgument a : getFunction().getArguments()) {
			try {
				if (getParameter(a) != null) {
					args[i] = TypeUtils.castTo(getParameter(a).getBindingValue(context),
							getConstructorDefinition().getConstructor().getGenericParameterTypes()[i]);
				}
				// System.out.println("Argument " + a.getArgumentName() + " / " + a.getArgumentType()+" values: " + args[i]);
			} catch (ReflectiveOperationException e) {
				throw new InvocationTargetTransformException(e);
			}
			i++;
		}
		try {
			// return getConstructorDefinition().getConstructor().invoke(target, args);
			return getConstructorDefinition().getConstructor().newInstance(args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer(
					"While evaluating method " + getConstructorDefinition().getConstructor() + " exception occured: " + e.getMessage());
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
					+ e.getTargetException().getMessage() + " while evaluating constructor " + getConstructorDefinition().getConstructor()
					+ " with args: ");
			for (int j = 0; j < args.length; j++) {
				sb.append("arg " + j + " = " + args[j] + " ");
			}
			LOGGER.warning(sb.toString());
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
			LOGGER.warning(sb.toString());
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
		sb.append("new " + TypeUtils.simpleRepresentation(getType()) + "(");
		boolean isFirst = true;
		for (FunctionArgument arg : getArguments()) {
			sb.append((isFirst ? "" : ",") + getParameter(arg));
			isFirst = false;
		}
		sb.append(")");
		return sb.toString();
	}

	private JavaNewInstanceMethodPathElement makeTransformedPathElement(ExpressionTransformer transformer) throws TransformException {

		boolean hasBeenTransformed = false;
		List<DataBinding<?>> transformedArgs = new ArrayList<>();

		for (FunctionArgument arg : getArguments()) {
			DataBinding<?> argValue = getParameter(arg);
			if (argValue != null && argValue.isValid()) {
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
			else {
				transformedArgs.add(argValue);
			}
		}

		DataBinding<?> transformedInnerAccess = null;

		if (innerAccess != null && innerAccess.isValid()) {
			Expression innerAccessExpression = innerAccess.getExpression();
			if (innerAccessExpression != null) {
				Expression transformedInnerAccessExpression = innerAccessExpression.transform(transformer);
				if (!transformedInnerAccessExpression.equals(innerAccessExpression)) {
					hasBeenTransformed = true;
					transformedInnerAccess = new DataBinding<>(innerAccess.getOwner(), innerAccess.getDeclaredType(),
							innerAccess.getBindingDefinitionType(), false);
					transformedInnerAccess.setExpression(transformedInnerAccessExpression);
					// TODO: better to do i think
					transformedInnerAccess.isValid();
					hasBeenTransformed = true;
				}
			}
		}

		if (!hasBeenTransformed) {
			return this;
		}

		return new JavaNewInstanceMethodPathElement(getParent(), getConstructorDefinition(), transformedInnerAccess, transformedArgs);
	}

	private JavaNewInstanceMethodPathElement updateTransformedPathElement(JavaNewInstanceMethodPathElement transformedPathElement,
			ExpressionTransformer transformer) throws TransformException {

		for (FunctionArgument arg : getArguments()) {
			DataBinding<?> argValue = getParameter(arg);
			if (argValue != null && argValue.isValid()) {
				Expression currentExpression = argValue.getExpression();
				if (currentExpression != null) {
					Expression transformedExpression = currentExpression.transform(transformer);
					if (!transformedExpression.equals(currentExpression)) {
						DataBinding<?> transformedBinding = transformedPathElement.getParameter(arg.getArgumentName());
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

		// TODO: some checkings ???
		System.out.println("Tiens faudrait verifier le new instance");

		check.returnedType = getType();
		check.valid = true;
		return check;
	}
}
