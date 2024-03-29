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
 * Default implementation for {@link NewInstancePathElement}
 * 
 * @author sylvain
 * 
 */
public abstract class NewInstancePathElementImpl<C extends AbstractConstructor> extends FunctionPathElementImpl<C>
		implements NewInstancePathElement<C> {

	static final Logger logger = Logger.getLogger(NewInstancePathElementImpl.class.getPackage().getName());

	private Type type;

	public NewInstancePathElementImpl(Type type, IBindingPathElement parent, String constructorName, List<DataBinding<?>> args,
			Bindable bindable) {
		super(parent, constructorName, null, args, bindable);
		this.type = type;
	}

	public NewInstancePathElementImpl(Type type, IBindingPathElement parent, C constructor, List<DataBinding<?>> args, Bindable bindable) {
		super(parent, constructor.getName(), constructor, args, bindable);
		this.type = type;
		setFunction(constructor);
	}

	@Override
	public void setFunction(C function) {
		super.setFunction(function);
		if (function != null) {
			setType(function.getNewInstanceType());
		}
		/*if (hasInnerAccess()) {
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
		}*/
	}

	@Override
	public Type getType() {
		if (getFunction() != null) {
			return getFunction().getReturnType();
		}
		return type;
	}

	@Override
	public final boolean hasInnerAccess() {
		return getParent() != null;
	}

	/**
	 * Always return false
	 * 
	 * @return
	 */
	@Override
	public final boolean isNotificationSafe() {
		return false;
	}

	@Override
	public final boolean supportsNullValues() {
		return true;
	}

}
