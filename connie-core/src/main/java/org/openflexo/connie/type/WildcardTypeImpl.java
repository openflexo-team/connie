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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

public class WildcardTypeImpl implements WildcardType, ConnieType {

	private Type[] upperBounds = new Type[0];
	private Type[] lowerBounds = new Type[0];

	public static WildcardTypeImpl makeUpperBoundWilcard(Type upperBound) {
		WildcardTypeImpl returned = new WildcardTypeImpl();
		returned.upperBounds = new Type[1];
		returned.upperBounds[0] = upperBound;
		return returned;
	}

	public static WildcardTypeImpl makeLowerBoundWilcard(Type lowerBound) {
		WildcardTypeImpl returned = new WildcardTypeImpl();
		returned.lowerBounds = new Type[1];
		returned.lowerBounds[0] = lowerBound;
		return returned;
	}

	public WildcardTypeImpl() {
	}

	public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
		this.upperBounds = upperBounds;
		this.lowerBounds = lowerBounds;
	}

	/*public WilcardTypeImpl(Type upperBound) {
		upperBounds = new Type[1];
		upperBounds[0] = upperBound;
		lowerBounds = new Type[0];
	}*/

	@Override
	public Type[] getLowerBounds() {
		return lowerBounds;
	}

	@Override
	public Type[] getUpperBounds() {
		return upperBounds;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(lowerBounds);
		result = prime * result + Arrays.hashCode(upperBounds);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WildcardType) {
			WildcardType that = (WildcardType) obj;
			return Arrays.asList(lowerBounds).equals(Arrays.asList(that.getLowerBounds()))
					&& Arrays.asList(upperBounds).equals(Arrays.asList(that.getUpperBounds()));
		}
		return false;
	}

	private boolean hasConnieTypeArguments() {
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

	private boolean hasUnresolvedArguments() {
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

	@Override
	public WildcardTypeImpl translateTo(TypingSpace typingSpace) {
		if (hasConnieTypeArguments()) {
			Type[] newUpper = new Type[upperBounds.length];
			for (int i = 0; i < upperBounds.length; i++) {
				Type t = upperBounds[i];
				newUpper[i] = (t instanceof ConnieType ? ((ConnieType) t).translateTo(typingSpace) : t);
			}
			Type[] newLower = new Type[lowerBounds.length];
			for (int i = 0; i < lowerBounds.length; i++) {
				Type t = lowerBounds[i];
				newLower[i] = (t instanceof ConnieType ? ((ConnieType) t).translateTo(typingSpace) : t);
			}
			return new WildcardTypeImpl(newUpper, newLower);
		}
		return this;
	}

	@Override
	public boolean isResolved() {
		if (hasUnresolvedArguments()) {
			return false;
		}
		if (hasConnieTypeArguments()) {
			for (int i = 0; i < upperBounds.length; i++) {
				Type t = upperBounds[i];
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					return false;
				}
			}
			for (int i = 0; i < lowerBounds.length; i++) {
				Type t = lowerBounds[i];
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
			for (int i = 0; i < upperBounds.length; i++) {
				Type t = upperBounds[i];
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					((ConnieType) t).resolve();
				}
			}
			for (int i = 0; i < lowerBounds.length; i++) {
				Type t = lowerBounds[i];
				if (t instanceof ConnieType && !((ConnieType) t).isResolved()) {
					((ConnieType) t).resolve();
				}
			}
		}
	}

}
