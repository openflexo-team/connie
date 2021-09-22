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

package org.openflexo.connie.binding.javareflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.connie.binding.AbstractConstructor;
import org.openflexo.connie.type.TypeUtils;

public class JavaConstructorDefinition extends AbstractJavaExecutableDefinition<Constructor<?>> implements AbstractConstructor {

	private String _signatureNFQ;
	private String _signatureFQ;

	private static Map<Constructor<?>, JavaConstructorDefinition> cache = new HashMap<>();

	public static JavaConstructorDefinition getConstructorDefinition(Type aDeclaringType, Constructor<?> constructor) {
		JavaConstructorDefinition returned = cache.get(constructor);
		if (returned == null) {
			returned = new JavaConstructorDefinition(aDeclaringType, constructor);
			cache.put(constructor, returned);
		}
		return returned;
	}

	private JavaConstructorDefinition(Type aDeclaringType, Constructor<?> constructor) {
		super(aDeclaringType, constructor);
	}

	public Constructor<?> getConstructor() {
		return getExecutable();
	}

	public String getConstructorName() {
		return getExecutableName();
	}

	@Override
	public String getSimplifiedSignature() {
		if (_signatureNFQ == null) {
			StringBuilder signature = new StringBuilder();
			signature.append(TypeUtils.simpleRepresentation(getDeclaringType()));
			signature.append("(");
			signature.append(getParameterListAsString(false));
			signature.append(")");
			_signatureNFQ = signature.toString();
		}
		return _signatureNFQ;
	}

	public String getSignature() {
		if (_signatureFQ == null) {
			// try {
			StringBuffer signature = new StringBuffer();
			signature.append(TypeUtils.fullQualifiedRepresentation(getDeclaringType()));
			signature.append("(");
			signature.append(getParameterListAsString(true));
			signature.append(")");
			_signatureFQ = signature.toString();
			/*}
			catch (InvalidKeyValuePropertyException e) {
				logger.warning("While computing getSignature() for "+method+" and "+declaringType+" message:"+e.getMessage());
				e.printStackTrace();
				return null;
			}*/

		}
		return _signatureFQ;
	}

	@Override
	public Type getReturnType() {
		return getDeclaringType();
	}

}
