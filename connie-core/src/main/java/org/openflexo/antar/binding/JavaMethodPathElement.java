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

package org.openflexo.antar.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Function.FunctionArgument;
import org.openflexo.antar.expr.InvocationTargetTransformException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;

/**
 * Modelize a java call which is a call to a method and with some arguments
 * 
 * @author sylvain
 * 
 */
public class JavaMethodPathElement extends FunctionPathElement {

	static final Logger LOGGER = Logger.getLogger(JavaMethodPathElement.class.getPackage().getName());

	public JavaMethodPathElement(BindingPathElement parent, MethodDefinition method, List<DataBinding<?>> args) {
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

	@Override
	public String getLabel() {
		return getMethodDefinition().getLabel();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return getMethodDefinition().getTooltipText(resultingType);
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetTransformException {

		// System.out.println("evaluate " + getMethodDefinition().getSignature() + " for " + target);

		Object[] args = new Object[getFunction().getArguments().size()];
		int i = 0;

		for (Function.FunctionArgument a : getFunction().getArguments()) {
			try {
				args[i] = TypeUtils.castTo(getParameter(a).getBindingValue(context), getMethodDefinition().getMethod()
						.getGenericParameterTypes()[i]);
			} catch (InvocationTargetException e) {
				throw new InvocationTargetTransformException(e);
			}
			i++;
		}
		try {
			return getMethodDefinition().getMethod().invoke(target, args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer("While evaluating method " + getMethodDefinition().getMethod()
					+ " exception occured: " + e.getMessage());
			warningMessage.append(", object = " + target);
			for (i = 0; i < getFunction().getArguments().size(); i++) {
				warningMessage.append(", arg[" + i + "] = " + args[i]);
			}
			LOGGER.warning(warningMessage.toString());
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
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

}
