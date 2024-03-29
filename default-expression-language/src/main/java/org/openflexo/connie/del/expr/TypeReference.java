/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.connie.del.expr;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.type.ParameterizedTypeImpl;

/**
 * Represents a type reference, as formed by a static access and some optional parameters
 * 
 * @author sylvain
 * 
 */
public class TypeReference implements Type {
	private String baseType;
	private List<TypeReference> parameters;
	private Type type;

	public TypeReference(String baseType) {
		super();
		this.baseType = baseType;
		parameters = new ArrayList<>();
	}

	public TypeReference(String baseType, List<TypeReference> someParameters) {
		super();
		this.baseType = baseType;
		parameters = new ArrayList<>(someParameters);
	}

	public String getBaseType() {
		return baseType;
	}

	public List<TypeReference> getParameters() {
		return parameters;
	}

	public Type getType() {
		if (type == null) {
			try {
				type = makeType();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return type;
	}

	private Type makeType() throws ClassNotFoundException {
		Class<?> baseClass = Class.forName(baseType);
		if (parameters.size() > 0) {
			Type[] params = new Type[parameters.size()];
			int i = 0;
			for (TypeReference r : parameters) {
				params[i++] = r.getType();
			}
			return new ParameterizedTypeImpl(baseClass, params);
		}
		return baseClass;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("$" + getBaseType());
		if (getParameters().size() > 0) {
			sb.append("<");
			boolean isFirst = true;
			for (TypeReference param : getParameters()) {
				sb.append((isFirst ? "" : ",") + param.toString());
				isFirst = false;
			}
			sb.append(">");
		}
		return sb.toString();
	}
}
