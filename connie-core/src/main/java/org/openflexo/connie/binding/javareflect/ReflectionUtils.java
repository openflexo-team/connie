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

package org.openflexo.connie.binding.javareflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final public class ReflectionUtils {

	private ReflectionUtils() {
	}

	/**
	 * Returns all methods that are overridden by the specified method. If no method is overridden, it returns an empty list.
	 * 
	 * @param method
	 *            the method to check for.
	 * @return all methods that are overridden by the specified method
	 */
	public static List<Method> getOverridenMethods(Method method) {
		return appendOverriddenMethods(new ArrayList<Method>(), method.getDeclaringClass(), method);
	}

	private static List<Method> appendOverriddenMethods(List<Method> methods, Class<?> klass, Method method) {
		if (klass == null) {
			return methods;
		}
		if (klass != method.getDeclaringClass()) {
			try {
				Method m = klass.getMethod(method.getName(), method.getParameterTypes());
				methods.add(m);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
		}
		appendOverriddenMethods(methods, klass.getSuperclass(), method);
		for (Class<?> superInterface : klass.getInterfaces()) {
			appendOverriddenMethods(methods, superInterface, method);
		}
		return methods;
	}
}
