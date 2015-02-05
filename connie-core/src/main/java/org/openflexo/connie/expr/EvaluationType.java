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

package org.openflexo.connie.expr;

import java.lang.reflect.Type;
import java.util.Date;

import org.openflexo.toolbox.Duration;

public enum EvaluationType {
	LITERAL, BOOLEAN, ARITHMETIC_INTEGER, ARITHMETIC_FLOAT, STRING, DATE, DURATION, ENUM;

	public String getName() {
		return toString();
	}

	public String getLocalizedName() {
		return getName(); // FlexoLocalization.localizedForKey(getName());
	}

	public boolean isLiteral() {
		return this == LITERAL;
	}

	public boolean isArithmetic() {
		return this == ARITHMETIC_FLOAT || this == ARITHMETIC_INTEGER;
	}

	public boolean isArithmeticInteger() {
		return this == ARITHMETIC_INTEGER;
	}

	public boolean isArithmeticFloat() {
		return this == ARITHMETIC_FLOAT;
	}

	public boolean isArithmeticOrLiteral() {
		return this == ARITHMETIC_FLOAT || this == ARITHMETIC_INTEGER || this == LITERAL;
	}

	public boolean isBoolean() {
		return this == BOOLEAN;
	}

	public boolean isBooleanOrLiteral() {
		return this == BOOLEAN || this == LITERAL;
	}

	public boolean isEnum() {
		return this == ENUM;
	}

	public boolean isEnumOrLiteral() {
		return this == ENUM || this == LITERAL;
	}

	public boolean isString() {
		return this == STRING;
	}

	public boolean isStringOrLiteral() {
		return this == STRING || this == LITERAL;
	}

	public boolean isDate() {
		return this == DATE;
	}

	public boolean isDateOrLiteral() {
		return this == DATE || this == LITERAL;
	}

	public boolean isDuration() {
		return this == DURATION;
	}

	public boolean isDurationOrLiteral() {
		return this == DURATION || this == LITERAL;
	}

	public Type getType() {
		if (this == LITERAL) {
			return Object.class;
		} else if (this == BOOLEAN) {
			return Boolean.class;
		} else if (this == ARITHMETIC_FLOAT) {
			return Double.class;
		} else if (this == ARITHMETIC_INTEGER) {
			return Long.class;
		} else if (this == STRING) {
			return String.class;
		} else if (this == DATE) {
			return Date.class;
		} else if (this == DURATION) {
			return Duration.class;
		} else if (this == ENUM) {
			return Enum.class;
		} else {
			return Object.class;
		}
	}
}
