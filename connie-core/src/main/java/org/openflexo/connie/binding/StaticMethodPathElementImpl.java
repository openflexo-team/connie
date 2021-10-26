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
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.DataBinding;

/**
 * Default implementation for a {@link StaticMethodPathElement}
 * 
 * @author sylvain
 * 
 */
public abstract class StaticMethodPathElementImpl<F extends Function> extends FunctionPathElementImpl<F>
		implements StaticMethodPathElement<F> {

	static final Logger logger = Logger.getLogger(StaticMethodPathElementImpl.class.getPackage().getName());

	private Type type;

	public StaticMethodPathElementImpl(Type type, String methodName, List<DataBinding<?>> args, Bindable bindable) {
		super(null, methodName, null, args, bindable);
		this.type = type;
	}

	public StaticMethodPathElementImpl(Type type, F method, List<DataBinding<?>> args, Bindable bindable) {
		super(null, method.getName(), method, args, bindable);
		this.type = type;
		setFunction(method);
	}

	@Override
	public Type getType() {
		if (getFunction() != null) {
			return getFunction().getReturnType();
		}
		return type;
	}

	@Override
	public boolean supportsNullValues() {
		return true;
	}

}
