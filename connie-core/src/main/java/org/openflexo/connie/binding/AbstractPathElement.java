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

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Abstract base implementation for a path element
 * 
 * @author sylvain
 * 
 */
public abstract class AbstractPathElement implements BindingPathElement, HasPropertyChangeSupport {

	private IBindingPathElement parent;
	private PropertyChangeSupport pcSupport;
	private boolean activated = false;

	public static final String NAME_PROPERTY = "propertyName";
	public static final String TYPE_PROPERTY = "type";
	public static final String DELETED_PROPERTY = "deleted";

	public AbstractPathElement(IBindingPathElement parent) {
		this.parent = parent;
		pcSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Activate this {@link BindingPathElement} by starting observing relevant objects when required
	 */
	@Override
	public void activate() {
		this.activated = true;
	}

	/**
	 * Desactivate this {@link BindingPathElement} by stopping observing relevant objects when required
	 */
	@Override
	public void desactivate() {
		this.activated = false;
	}

	/**
	 * Return boolean indicating if this {@link BindingPathElement} is activated
	 * 
	 * @return
	 */
	@Override
	public boolean isActivated() {
		return activated;
	}

	@Override
	public IBindingPathElement getParent() {
		return parent;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	@Override
	public BindingPathCheck checkBindingPathIsValid(IBindingPathElement parentElement, Type parentType) {

		BindingPathCheck check = new BindingPathCheck();

		if (getParent() == null) {
			check.invalidBindingReason = "No parent for: " + this;
			check.valid = false;
			return check;
		}

		if (getParent() != parentElement) {
			check.invalidBindingReason = "Inconsistent parent for: " + this;
			check.valid = false;
			return check;
		}

		if (!TypeUtils.isTypeAssignableFrom(parentElement.getType(), getParent().getType(), true)) {
			check.invalidBindingReason = "Mismatched: " + parentElement.getType() + " and " + getParent().getType();
			check.valid = false;
			return check;
		}

		check.returnedType = TypeUtils.makeInstantiatedType(getType(), parentType);
		check.valid = true;
		return check;
	}

}
