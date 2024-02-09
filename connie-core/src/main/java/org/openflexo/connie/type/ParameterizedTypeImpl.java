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
import java.util.Arrays;
import java.util.Objects;

/**
 * Connie-specific implementation of {@link ParameterizedType}
 * 
 * Semantics of hashCode() and equals() follows base API (equals might return true for alternative {@link ParameterizedType}
 * implementations)
 * 
 * @author sylvain
 *
 */
public class ParameterizedTypeImpl implements ParameterizedType, ConnieType {

	private Type rawType;
	private Type ownerType;
	private Type[] actualTypeArguments;

	public ParameterizedTypeImpl(Type rawType, Type... actualTypeArguments) {
		this(rawType, rawType instanceof Class ? ((Class<?>) rawType).getDeclaringClass() : null, actualTypeArguments);
	}

	public ParameterizedTypeImpl(Type rawType, Type actualTypeArgument) {
		this(rawType, rawType instanceof Class ? ((Class<?>) rawType).getDeclaringClass() : null, makeTypeArray(actualTypeArgument));
	}

	private static Type[] makeTypeArray(Type t) {
		Type[] returned = new Type[1];
		returned[0] = t;
		return returned;
	}

	public ParameterizedTypeImpl(Type rawType, Type ownerType, Type[] actualTypeArguments) {
		super();
		this.rawType = rawType;
		this.ownerType = ownerType;
		this.actualTypeArguments = actualTypeArguments;
	}

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
		sb.append(TypeUtils.simpleRepresentation(rawType) + "<");
		// sb.append(rawType.getSimpleName() + "<");
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
		// sb.append(rawType.getName() + "<");
		sb.append(TypeUtils.fullQualifiedRepresentation(rawType) + "<");
		boolean isFirst = true;
		for (Type t : getActualTypeArguments()) {
			sb.append((isFirst ? "" : ",") + TypeUtils.fullQualifiedRepresentation(t));
			isFirst = false;
		}
		sb.append(">");
		return sb.toString();
	}

	/*@Override
	public int hashCode() {
		return fullQualifiedRepresentation().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Type) {
			return TypeUtils.fullQualifiedRepresentation(this).equals(TypeUtils.fullQualifiedRepresentation((Type) obj));
		}
		return super.equals(obj);
	}*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(actualTypeArguments);
		result = prime * result + Objects.hash(ownerType, rawType);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (TypeUtils.isTypeAssignableFrom(ParameterizedType.class, obj.getClass())) {
			ParameterizedType other = (ParameterizedType) obj;
			return Arrays.equals(actualTypeArguments, other.getActualTypeArguments()) && Objects.equals(ownerType, other.getOwnerType())
					&& Objects.equals(rawType, other.getRawType());
		}
		return false;
	}

	protected boolean hasUnresolvedArguments() {
		for (Type argument : actualTypeArguments) {
			if (argument instanceof UnresolvedType) {
				return true;
			}
		}
		return false;
	}

	private boolean hasConnieTypeArguments() {
		if (rawType instanceof ConnieType) {
			return true;
		}
		if (ownerType instanceof ConnieType) {
			return true;
		}
		for (Type argument : actualTypeArguments) {
			if (argument instanceof ConnieType) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ParameterizedTypeImpl translateTo(TypingSpace typingSpace) {
		if (hasConnieTypeArguments()) {
			Type newRawType = (rawType instanceof ConnieType ? ((ConnieType) rawType).translateTo(typingSpace) : rawType);
			Type newOwnerType = (ownerType instanceof ConnieType ? ((ConnieType) ownerType).translateTo(typingSpace) : ownerType);
			Type[] newArgs = new Type[actualTypeArguments.length];
			for (int i = 0; i < actualTypeArguments.length; i++) {
				Type t = actualTypeArguments[i];
				newArgs[i] = (t instanceof ConnieType ? ((ConnieType) t).translateTo(typingSpace) : t);
			}
			return new ParameterizedTypeImpl(newRawType, newOwnerType, newArgs);
		}
		return this;
	}

	@Override
	public boolean isResolved() {
		// System.out.println("rawType=" + rawType + " of " + rawType.getClass());
		if (rawType instanceof UnresolvedType) {
			return false;
		}
		if (hasUnresolvedArguments()) {
			return false;
		}
		if (hasConnieTypeArguments()) {
			if (rawType instanceof ConnieType && !((ConnieType) rawType).isResolved()) {
				return false;
			}
			if (ownerType instanceof ConnieType && !((ConnieType) ownerType).isResolved()) {
				return false;
			}
			for (int i = 0; i < actualTypeArguments.length; i++) {
				Type t = actualTypeArguments[i];
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					return false;
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public void resolve() {
		if (hasConnieTypeArguments()) {
			if (rawType instanceof ConnieType && !((ConnieType) rawType).isResolved()) {
				((ConnieType) rawType).resolve();
			}
			if (ownerType instanceof ConnieType && !((ConnieType) ownerType).isResolved()) {
				((ConnieType) ownerType).resolve();
			}
			for (int i = 0; i < actualTypeArguments.length; i++) {
				Type t = actualTypeArguments[i];
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					((ConnieType) t).resolve();
				}
			}
		}
	}

}
