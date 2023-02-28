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

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class WildcardTypeImpl<T extends Type> implements WildcardType, ConnieType {

	private T[] upperBoundsArray = null;
	private T[] lowerBoundsArray = null;
	private List<T> upperBounds = new ArrayList<>();
	private List<T> lowerBounds = new ArrayList<>();

	public static class DefaultWildcardType extends WildcardTypeImpl<Type> {

		public static DefaultWildcardType makeUpperBoundWilcard(Type upperBound) {
			DefaultWildcardType returned = new DefaultWildcardType();
			returned.addUpperBound(upperBound);
			return returned;
		}

		public static DefaultWildcardType makeLowerBoundWilcard(Type lowerBound) {
			DefaultWildcardType returned = new DefaultWildcardType();
			returned.addLowerBound(lowerBound);
			return returned;
		}

		public DefaultWildcardType() {
			super();
		}

		public DefaultWildcardType(List<Type> upperBounds, List<Type> lowerBounds) {
			super(upperBounds, lowerBounds);
		}

		public DefaultWildcardType(Type[] upperBounds, Type[] lowerBounds) {
			super(upperBounds, lowerBounds);
		}

		@Override
		public Class<Type> getTypeClass() {
			return Type.class;
		}

		@Override
		public DefaultWildcardType translateTo(TypingSpace typingSpace) {
			if (hasConnieTypeArguments()) {
				List<Type> newUpper = new ArrayList<>();
				for (Type t : getUpperBounds()) {
					newUpper.add(t instanceof ConnieType ? (Type) ((ConnieType) t).translateTo(typingSpace) : t);
				}
				List<Type> newLower = new ArrayList<>();
				for (Type t : getLowerBounds()) {
					newLower.add(t instanceof ConnieType ? (Type) ((ConnieType) t).translateTo(typingSpace) : t);
				}
				return new DefaultWildcardType(newUpper, newLower);
			}
			return this;
		}

	}

	protected WildcardTypeImpl() {
	}

	public WildcardTypeImpl(List<T> upperBounds, List<T> lowerBounds) {
		this.upperBounds.addAll(upperBounds);
		this.lowerBounds.addAll(lowerBounds);
	}

	public WildcardTypeImpl(T[] upperBounds, T[] lowerBounds) {
		this(Arrays.asList(upperBounds), Arrays.asList(lowerBounds));
	}

	public abstract Class<T> getTypeClass();

	@Override
	public T[] getLowerBounds() {
		if (lowerBoundsArray == null) {
			lowerBoundsArray = lowerBounds.toArray((T[]) Array.newInstance(getTypeClass(), lowerBounds.size()));
		}
		return lowerBoundsArray;
	}

	@Override
	public T[] getUpperBounds() {
		if (upperBoundsArray == null) {
			upperBoundsArray = upperBounds.toArray((T[]) Array.newInstance(getTypeClass(), upperBounds.size()));
		}
		return upperBoundsArray;
	}

	protected void addUpperBound(T t) {
		upperBounds.add(t);
		upperBoundsArray = null;
	}

	protected void addLowerBound(T t) {
		lowerBounds.add(t);
		lowerBoundsArray = null;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("?");

		if (getUpperBounds() != null && getUpperBounds().length > 0) {
			sb.append(" extends ");
			boolean isFirst = true;
			for (Type t : getUpperBounds()) {
				sb.append((isFirst ? "" : ",") + TypeUtils.simpleRepresentation(t));
				isFirst = false;
			}
		}

		if (getLowerBounds() != null && getLowerBounds().length > 0) {
			sb.append(" super ");
			boolean isFirst = true;
			for (Type t : getLowerBounds()) {
				sb.append((isFirst ? "" : ",") + TypeUtils.simpleRepresentation(t));
				isFirst = false;
			}
		}

		return sb.toString();
	}

	public String fullQualifiedRepresentation() {
		StringBuffer sb = new StringBuffer();
		sb.append("?");

		if (getUpperBounds() != null && getUpperBounds().length > 0) {
			sb.append(" extends ");
			boolean isFirst = true;
			for (Type t : getUpperBounds()) {
				sb.append((isFirst ? "" : ",") + TypeUtils.fullQualifiedRepresentation(t));
				isFirst = false;
			}
		}

		if (getLowerBounds() != null && getLowerBounds().length > 0) {
			sb.append(" super ");
			boolean isFirst = true;
			for (Type t : getLowerBounds()) {
				sb.append((isFirst ? "" : ",") + TypeUtils.fullQualifiedRepresentation(t));
				isFirst = false;
			}
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(lowerBounds, upperBounds);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WildcardTypeImpl other = (WildcardTypeImpl) obj;
		return Objects.equals(lowerBounds, other.lowerBounds) && Objects.equals(upperBounds, other.upperBounds);
	}

	protected boolean hasConnieTypeArguments() {
		for (Type argument : upperBounds) {
			if (argument instanceof ConnieType) {
				return true;
			}
		}
		for (Type argument : lowerBounds) {
			if (argument instanceof ConnieType) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasUnresolvedArguments() {
		for (Type argument : upperBounds) {
			if (argument instanceof UnresolvedType) {
				return true;
			}
		}
		for (Type argument : lowerBounds) {
			if (argument instanceof UnresolvedType) {
				return true;
			}
		}
		return false;
	}

	/*@Override
	public WildcardTypeImpl<T> translateTo(TypingSpace typingSpace) {
		if (hasConnieTypeArguments()) {
			List<T> newUpper = new ArrayList<>();
			for (T t : upperBounds) {
				newUpper.add(t instanceof ConnieType ? (T) ((ConnieType) t).translateTo(typingSpace) : t);
			}
			List<T> newLower = new ArrayList<>();
			for (T t : lowerBounds) {
				newLower.add(t instanceof ConnieType ? (T) ((ConnieType) t).translateTo(typingSpace) : t);
			}
			return new WildcardTypeImpl<T>(newUpper, newLower);
		}
		return this;
	}*/

	@Override
	public abstract WildcardTypeImpl<T> translateTo(TypingSpace typingSpace);

	@Override
	public boolean isResolved() {
		if (hasUnresolvedArguments()) {
			return false;
		}
		if (hasConnieTypeArguments()) {
			for (T t : upperBounds) {
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					return false;
				}
			}
			for (T t : lowerBounds) {
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
			for (T t : upperBounds) {
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					((ConnieType) t).resolve();
				}
			}
			for (T t : lowerBounds) {
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					((ConnieType) t).resolve();
				}
			}
		}
	}

}
