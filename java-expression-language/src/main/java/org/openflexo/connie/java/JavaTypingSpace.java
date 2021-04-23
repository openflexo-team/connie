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

package org.openflexo.connie.java;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.ContextualizedBindable;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.TypingSpace;
import org.openflexo.connie.type.UnresolvedType;

/**
 * Default implementation for a Java typing space, which may be used as a delegate to {@link ContextualizedBindable}
 * 
 * Natively import both packages java.lang.* and java.util.*
 * 
 * @author sylvain
 *
 */
public class JavaTypingSpace implements TypingSpace {

	private List<Package> importedPackages = new ArrayList<>();
	private List<Class> importedClasses = new ArrayList<>();

	public JavaTypingSpace() {
		importedPackages = new ArrayList<>();
		importedPackages.add(Package.getPackage("java.lang"));
		importedPackages.add(Package.getPackage("java.util"));
	}

	/**
	 * Return boolean indicating if supplied {@link Type} is actually in current typing space
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public boolean isTypeImported(Type type) {
		Class c = TypeUtils.getRawType(type);
		if (c != null) {
			if (importedPackages.contains(c.getPackage())) {
				return true;
			}
			if (importedClasses.contains(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Import supplied type in this typing space
	 * 
	 * @param type
	 */
	@Override
	public void importType(Type type) {
		if (isTypeImported(type)) {
			return;
		}
		Class c = TypeUtils.getRawType(type);
		importedClasses.add(c);
	}

	/**
	 * Resolve {@link Type} according to current typing space using supplied type {@link String} representation
	 * 
	 * @param typeAsString
	 * @return
	 */
	@Override
	public Type resolveType(String typeAsString) {

		try {
			return Class.forName(typeAsString);
		} catch (ClassNotFoundException e1) {
			for (Package importedPackage : importedPackages) {
				try {
					return Class.forName(importedPackage.getName() + "." + typeAsString);
				} catch (ClassNotFoundException e2) {
					// Silently continue
				}
			}
			for (Class<?> aClass : importedClasses) {
				if (aClass.getSimpleName().equals(typeAsString)) {
					return aClass;
				}
			}
			return new UnresolvedType(typeAsString);
		}

	}

	@Override
	public String toString() {
		return "JavaTypingSpace importedPackages=" + importedPackages + " importedClasses=" + importedClasses;
	}

}
