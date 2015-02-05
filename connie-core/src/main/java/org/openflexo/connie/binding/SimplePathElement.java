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

import java.lang.reflect.Type;

/**
 * Model a simple path element in a binding path, represented by a simple get/set access through a property
 * 
 * @author sylvain
 * 
 */
public abstract class SimplePathElement implements BindingPathElement, SettableBindingPathElement {

	private final BindingPathElement parent;
	private String propertyName;
	private Type type;

	public SimplePathElement(BindingPathElement parent, String propertyName, Type type) {
		this.parent = parent;
		this.propertyName = propertyName;
		this.type = type;
	}

	public void delete() {
		// TODO
	}

	@Override
	public BindingPathElement getParent() {
		return parent;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public Type getType() {
		return type;
	}

	public final void setType(Type type) {
		this.type = type;
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
	}
}
