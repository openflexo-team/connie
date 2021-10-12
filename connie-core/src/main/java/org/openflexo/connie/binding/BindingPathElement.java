/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;

import org.openflexo.connie.DataBinding;

/**
 * General API for an non-root element of a formal binding path, which has at least one parent
 * 
 * @author sylvain
 * 
 */
public interface BindingPathElement extends IBindingPathElement {

	/**
	 * Return parent of this BindingPathElement
	 * 
	 * @return
	 */
	IBindingPathElement getParent();

	/**
	 * Activate this {@link BindingPathElement} by starting observing relevant objects when required
	 */
	public void activate();

	/**
	 * Desactivate this {@link BindingPathElement} by stopping observing relevant objects when required
	 */
	public void desactivate();

	/**
	 * Return boolean indicating if this {@link BindingPathElement} is activated
	 * 
	 * @return
	 */
	public boolean isActivated();

	/**
	 * Return boolean indicating if this {@link BindingPathElement} is notification-safe (all modifications of data are notified using
	 * {@link PropertyChangeSupport} scheme)<br>
	 * 
	 * When tagged as unsafe, disable caching while evaluating related {@link DataBinding}.
	 * 
	 * Otherwise return true
	 * 
	 * @return
	 */
	public boolean isNotificationSafe();

	/**
	 * Return a flag indicating if this BindingPathElement supports computation with 'null' value as entry (target)<br>
	 * 
	 * @return
	 */
	public boolean supportsNullValues();

	/**
	 * Carry the result of acceptability of to type checking of a {@link BindingPathElement} in the context of a parent
	 *
	 * @author sylvain
	 */
	public static class BindingPathCheck {
		public Boolean valid = null;
		public String invalidBindingReason;
		public Type returnedType;
	}

	/**
	 * Evaluate the acceptability relatively to type checking of this {@link BindingPathElement} in the context of a parent
	 * {@link IBindingPathElement}
	 * 
	 * @param parentElement
	 *            parent element of current {@link IBindingPathElement}
	 * @param parentType
	 *            resulting type for the parent {@link IBindingPathElement} in its context
	 * @return
	 */
	public abstract BindingPathCheck checkBindingPathIsValid(IBindingPathElement parentElement, Type parentType);

}
