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
import java.util.List;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.expr.ExpressionTransformer;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Modelize a parameterized path element in a binding path, which is the symbolic representation of a call to a function and with some
 * arguments
 * 
 * @author sylvain
 * 
 */
public interface FunctionPathElement<F extends Function> extends BindingPathElement, HasPropertyChangeSupport {

	public F getFunction();

	public void setFunction(F function);

	public String getMethodName();

	public void setMethodName(String methodName);

	@Override
	public Type getType();

	public void setType(Type type);

	public List<? extends FunctionArgument> getFunctionArguments();

	public List<DataBinding<?>> getArguments();

	public void setArguments(List<DataBinding<?>> arguments);

	public DataBinding<?> getArgumentValue(String argumentName);

	public DataBinding<?> getArgumentValue(Function.FunctionArgument argument);

	public void setArgumentValue(Function.FunctionArgument argument, DataBinding<?> value);

	public void instanciateParameters(Bindable bindable);

	public abstract FunctionPathElement<F> transform(ExpressionTransformer transformer) throws TransformException;

}
