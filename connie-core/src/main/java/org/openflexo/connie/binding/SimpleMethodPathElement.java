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

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;

/**
 * Model a method call which is a call to a method in a given context (the object on which this "method" is called) with some arguments
 * 
 * @author sylvain
 * 
 */
public abstract class SimpleMethodPathElement<F extends Function> extends FunctionPathElement<F> {

	static final Logger logger = Logger.getLogger(SimpleMethodPathElement.class.getPackage().getName());

	public SimpleMethodPathElement(IBindingPathElement parent, String methodName, List<DataBinding<?>> args) {
		super(parent, methodName, null, args);
	}

	public SimpleMethodPathElement(IBindingPathElement parent, F method, List<DataBinding<?>> args) {
		super(parent, method.getName(), method, args);
		setFunction(method);
	}

	/**
	 * Return a flag indicating if this BindingPathElement supports computation with 'null' value as entry (target)<br>
	 * 
	 * @return false in this case
	 */
	@Override
	public final boolean supportsNullValues() {
		return false;
	}

}
