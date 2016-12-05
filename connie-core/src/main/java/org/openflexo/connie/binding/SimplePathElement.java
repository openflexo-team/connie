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

import org.openflexo.connie.BindingVariable;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Model a simple path element in a binding path, represented by a simple get/set access through a property
 * 
 * @author sylvain
 * 
 */
public abstract class SimplePathElement implements BindingPathElement, SettableBindingPathElement, HasPropertyChangeSupport {

	private BindingPathElement parent;
	private String propertyName;
	private Type type;
	private PropertyChangeSupport pcSupport;

	public static final String NAME_PROPERTY = "propertyName";
	public static final String TYPE_PROPERTY = "type";
	public static final String DELETED_PROPERTY = "deleted";

	public SimplePathElement(BindingPathElement parent, String propertyName, Type type) {
		this.parent = parent;
		this.propertyName = propertyName;
		this.type = type;
		pcSupport = new PropertyChangeSupport(this);
	}

	public void delete() {
		getPropertyChangeSupport().firePropertyChange(DELETED_PROPERTY, this, null);
		pcSupport = null;
		parent = null;
		propertyName = null;
		type = null;
	}

	@Override
	public BindingPathElement getParent() {
		return parent;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		String oldValue = getPropertyName();
		if (propertyName != null && !propertyName.equals(oldValue)) {
			this.propertyName = propertyName;
			getPropertyChangeSupport().firePropertyChange(NAME_PROPERTY, oldValue, propertyName);
		}
	}

	@Override
	public Type getType() {
		return type;
	}

	public final void setType(Type type) {
		Type oldType = getType();
		if (type != null && !type.equals(oldType)) {
			this.type = type;
			getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, oldType, type);
		}
	}

	@Override
	public boolean isSettable() {
		return true;
	}

	@Override
	public String getSerializationRepresentation() {
		return getPropertyName();
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePathElement other = (SimplePathElement) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		}
		else if (!parent.equals(other.parent))
			return false;
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		}
		else if (!propertyName.equals(other.propertyName))
			return false;
		return true;
	}

	/*@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimplePathElement) {
			return getParent().equals(((SimplePathElement) obj).getParent())
					&& getPropertyName().equals(((SimplePathElement) obj).getPropertyName());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return getPropertyName().hashCode();
	}*/

	public boolean isNotifyingBindingPathChanged() {
		return false;
	}

	public String getBindingPath() {
		if (getParent() instanceof SimplePathElement) {
			return ((SimplePathElement) getParent()).getBindingPath() + "." + getLabel();
		}
		if (getParent() instanceof BindingVariable) {
			return ((BindingVariable) getParent()).getVariableName() + "." + getLabel();
		}
		return getLabel();
	}
}
