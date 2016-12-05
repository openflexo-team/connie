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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.Function.FunctionArgument;

/**
 * Modelize a compound path element in a binding path, which is the symbolic representation of a call to a function and with a given amount
 * of arguments
 * 
 * @author sylvain
 * 
 */
public abstract class FunctionPathElement extends Observable implements BindingPathElement {

	private static final Logger LOGGER = Logger.getLogger(FunctionPathElement.class.getPackage().getName());

	private final BindingPathElement parent;
	private final Function function;
	private Type type;
	private final HashMap<Function.FunctionArgument, DataBinding<?>> parameters;

	public FunctionPathElement(BindingPathElement parent, Function function, List<DataBinding<?>> paramValues) {
		this.parent = parent;
		this.function = function;
		parameters = new HashMap<>();
		if (function == null) {
			LOGGER.warning("FunctionPathElement called with null function");
		}
		else {
			this.type = function.getReturnType();
			if (paramValues != null) {
				int i = 0;
				for (Function.FunctionArgument arg : function.getArguments()) {
					if (i < paramValues.size()) {
						DataBinding<?> paramValue = paramValues.get(i);
						setParameter(arg, paramValue);
					}
					i++;
				}
			}
		}
	}

	public void instanciateParameters(Bindable bindable) {
		for (Function.FunctionArgument arg : function.getArguments()) {
			DataBinding<?> parameter = getParameter(arg);
			if (parameter == null) {
				parameter = new DataBinding<>(bindable, arg.getArgumentType(), DataBinding.BindingDefinitionType.GET);
				parameter.setBindingName(arg.getArgumentName());
				parameter.setUnparsedBinding("");
				setParameter(arg, parameter);
			}
			else {
				parameter.setOwner(bindable);
				parameter.setDeclaredType(arg.getArgumentType());
				parameter.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
		}
	}

	@Override
	public BindingPathElement getParent() {
		return parent;
	}

	public Function getFunction() {
		return function;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	private String serializationRepresentation = null;

	@Override
	public String getSerializationRepresentation() {
		if (serializationRepresentation == null) {
			StringBuffer returned = new StringBuffer();
			if (getFunction() != null) {
				returned.append(getFunction().getName());
				returned.append("(");
				boolean isFirst = true;
				for (Function.FunctionArgument a : getFunction().getArguments()) {
					returned.append((isFirst ? "" : ",") + getParameter(a));
					isFirst = false;
				}
				returned.append(")");
			}
			else {
				returned.append("unknown_function()");
			}
			serializationRepresentation = returned.toString();
		}
		return serializationRepresentation;
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	public List<? extends FunctionArgument> getArguments() {
		return function.getArguments();
	}

	public DataBinding<?> getParameter(Function.FunctionArgument argument) {
		return parameters.get(argument);
	}

	public void setParameter(Function.FunctionArgument argument, DataBinding<?> value) {
		serializationRepresentation = null;
		// System.out.println("setParameter " + argument + " for " + this + " with " + value);
		parameters.put(argument, value);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "/" + getSerializationRepresentation();
	}

	@Override
	public boolean isNotifyingBindingPathChanged() {
		return false;
	}
}
