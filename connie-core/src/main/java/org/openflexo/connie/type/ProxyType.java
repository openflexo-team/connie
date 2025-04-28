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

package org.openflexo.connie.type;

import java.lang.reflect.Type;

/**
 * A type which proxies another one using a local name
 * 
 * @author sylvain
 * 
 */
public class ProxyType implements CustomType {

	private final String localName;

	private final Type referencedType;

	/**
	 * Utility method to retrieve effective type of any type
	 * 
	 * @param aType
	 * @return
	 */
	public static Type getEffectiveType(Type aType) {
		if (aType instanceof ProxyType) {
			return getEffectiveType(((ProxyType) aType).getReferencedType());
		}
		return aType;
	}

	public ProxyType(String localName, Type referencedType) {
		this.localName = localName;
		this.referencedType = referencedType;
	}

	public String getLocalName() {
		return localName;
	}

	public Type getReferencedType() {
		return referencedType;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		if (getReferencedType() instanceof CustomType) {
			return ((CustomType) getReferencedType()).isTypeAssignableFrom(aType, permissive);
		}
		return TypeUtils.isTypeAssignableFrom(getReferencedType(), aType, permissive);
	}

	@Override
	public boolean isOfType(Object object, boolean permissive) {
		if (getReferencedType() instanceof CustomType) {
			return ((CustomType) getReferencedType()).isOfType(object, permissive);
		}
		return TypeUtils.isOfType(object, getReferencedType());
	}

	@Override
	public String simpleRepresentation() {
		return getLocalName();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return getLocalName() + "->" + TypeUtils.fullQualifiedRepresentation(getReferencedType());
	}

	@Override
	public Class<?> getBaseClass() {
		return TypeUtils.getBaseClass(getReferencedType());
	}

	@Override
	public String getSerializationRepresentation() {
		return getLocalName();
	}

	@Override
	public boolean isResolved() {
		return TypeUtils.isResolved(getReferencedType());
	}

	@Override
	public void resolve() {
		if (getReferencedType() instanceof CustomType) {
			((CustomType) getReferencedType()).resolve();
		}
		else if (!isResolved()) {
			System.err.println("Cannot resolve : " + getReferencedType());
		}
	}

	@Override
	public String toString() {
		return fullQualifiedRepresentation();
	}
}
