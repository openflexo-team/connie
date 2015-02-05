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

package org.openflexo.connie.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {

	public ParameterizedTypeImpl(Class rawType, Type[] actualTypeArguments) {
		this(rawType, null, actualTypeArguments);
	}

	public ParameterizedTypeImpl(Class rawType, Type actualTypeArgument) {
		this(rawType, null, makeTypeArray(actualTypeArgument));
	}

	private static Type[] makeTypeArray(Type t) {
		Type[] returned = new Type[1];
		returned[0] = t;
		return returned;
	}

	public ParameterizedTypeImpl(Class rawType, Type ownerType, Type[] actualTypeArguments) {
		super();
		this.rawType = rawType;
		this.ownerType = ownerType;
		this.actualTypeArguments = actualTypeArguments;
	}

	private Class rawType;
	private Type ownerType;
	private Type[] actualTypeArguments;

	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public Type getOwnerType() {
		return ownerType;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(rawType.getSimpleName() + "<");
		boolean isFirst = true;
		for (Type t : getActualTypeArguments()) {
			sb.append((isFirst ? "" : ",") + TypeUtils.simpleRepresentation(t));
			isFirst = false;
		}
		sb.append(">");
		return sb.toString();
	}

	public String fullQualifiedRepresentation() {
		StringBuffer sb = new StringBuffer();
		sb.append(rawType.getName() + "<");
		boolean isFirst = true;
		for (Type t : getActualTypeArguments()) {
			sb.append((isFirst ? "" : ",") + TypeUtils.fullQualifiedRepresentation(t));
			isFirst = false;
		}
		sb.append(">");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return fullQualifiedRepresentation().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Type) {
			return TypeUtils.fullQualifiedRepresentation(this).equals(TypeUtils.fullQualifiedRepresentation((Type) obj));
		} else {
			return super.equals(obj);
		}
		/*if (obj instanceof ParameterizedType) {
			if (getRawType() == null) return false;
			if (getActualTypeArguments() == null) return false;
			if (! (((getOwnerType() == null && ((ParameterizedType)obj).getOwnerType() == null)
					|| (getOwnerType() != null && getOwnerType().equals(((ParameterizedType)obj).getOwnerType())))
					&& getRawType().equals(((ParameterizedType)obj).getRawType())))
				return false;
			// Now check all args
			for (int i=0; i<getActualTypeArguments().length; i++) {
				if (getActualTypeArguments()[i] == null) {
					if (((ParameterizedType)obj).getActualTypeArguments()[i] != null) return false;
				}
				else if (!getActualTypeArguments()[i].equals(((ParameterizedType)obj).getActualTypeArguments()[i])) return false;
			}
			return true;
		}
		else return super.equals(obj);*/
	}
}
