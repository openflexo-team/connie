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

/**
 * All primitives managed by CONNIE-CORE
 * 
 * @author sylvain
 *
 */
public enum PrimitiveType {
	Boolean {
		@Override
		public Type getType() {
			return java.lang.Boolean.TYPE;
		}
	},
	String {
		@Override
		public Type getType() {
			return String.class;
		}
	},
	Date {
		@Override
		public Type getType() {
			return java.util.Date.class;
		}
	},
	Integer {
		@Override
		public Type getType() {
			return java.lang.Integer.TYPE;
		}
	},
	Long {
		@Override
		public Type getType() {
			return java.lang.Long.TYPE;
		}
	},
	Float {
		@Override
		public Type getType() {
			return java.lang.Float.TYPE;
		}
	},
	Double {
		@Override
		public Type getType() {
			return java.lang.Double.TYPE;
		}
	};

	public abstract Type getType();

	public static PrimitiveType toPrimitiveType(Type type) {
		if (TypeUtils.isBoolean(type)) {
			return Boolean;
		}
		if (TypeUtils.isDouble(type)) {
			return Double;
		}
		if (TypeUtils.isFloat(type)) {
			return Float;
		}
		if (TypeUtils.isLong(type)) {
			return Long;
		}
		if (TypeUtils.isInteger(type)) {
			return Integer;
		}
		if (TypeUtils.isString(type)) {
			return String;
		}
		if (TypeUtils.isDate(type)) {
			return Date;
		}
		return null;
	}

	public static boolean isPrimitiveType(Type type) {
		return toPrimitiveType(type) != null;
	}
}
