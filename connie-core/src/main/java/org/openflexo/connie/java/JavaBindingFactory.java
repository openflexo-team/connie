/**
 * 
 */
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

package org.openflexo.connie.java;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.JavaMethodPathElement;
import org.openflexo.connie.binding.JavaPropertyPathElement;
import org.openflexo.connie.binding.MethodDefinition;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.kvc.KeyValueLibrary;
import org.openflexo.kvc.KeyValueProperty;

public class JavaBindingFactory implements BindingFactory {
	static final Logger LOGGER = Logger.getLogger(JavaBindingFactory.class.getPackage().getName());

	@Override
	public Type getTypeForObject(Object object) {
		if (object != null) {
			return object.getClass();
		}
		return Object.class;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		if (parent.getType() != null) {
			if (TypeUtils.getBaseClass(parent.getType()) == null) {
				return null;
			}
			Type currentType = parent.getType();
			if (currentType instanceof Class && ((Class<?>) currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class<?>) currentType);
			}
			if (currentType instanceof WildcardType) {
				Type[] upperBounds = ((WildcardType) currentType).getUpperBounds();
				if (upperBounds.length == 1) {
					currentType = upperBounds[0];
				}
			}
			List<JavaPropertyPathElement> returned = new ArrayList<>();
			for (KeyValueProperty p : KeyValueLibrary.getAccessibleProperties(currentType)) {
				returned.add(new JavaPropertyPathElement(parent, p));
			}
			return returned;
		}
		return null;
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		if (parent.getType() != null) {
			if (TypeUtils.getBaseClass(parent.getType()) == null) {
				return null;
			}
			Type currentType = parent.getType();
			if (currentType instanceof Class && ((Class<?>) currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class<?>) currentType);
			}
			List<JavaMethodPathElement> returned = new ArrayList<>();
			for (MethodDefinition m : KeyValueLibrary.getAccessibleMethods(currentType)) {
				returned.add(new JavaMethodPathElement(parent, m, null));
			}
			return returned;
		}
		return null;
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement father, String propertyName) {
		Type fatherType = father.getType();
		if (fatherType instanceof Class && ((Class<?>) fatherType).isPrimitive()) {
			fatherType = TypeUtils.fromPrimitive((Class<?>) fatherType);
		}
		KeyValueProperty keyValueProperty = KeyValueLibrary.getKeyValueProperty(fatherType, propertyName);
		if (keyValueProperty != null) {
			return new JavaPropertyPathElement(father, keyValueProperty);
		}
		return null;
	}

	@Override
	public FunctionPathElement makeFunctionPathElement(BindingPathElement father, Function function, List<DataBinding<?>> args) {
		if (function instanceof MethodDefinition) {
			return new JavaMethodPathElement(father, (MethodDefinition) function, args);
		}
		return null;
	}

	@Override
	public Function retrieveFunction(Type parentType, String functionName, List<DataBinding<?>> args) {
		Vector<Method> possiblyMatchingMethods = new Vector<>();
		Class<?> typeClass = TypeUtils.getBaseClass(parentType);
		if (typeClass == null) {
			System.out.println("Cannot find typeClass for " + parentType);
			return null;
		}
		Method[] allMethods = typeClass.getMethods();
		// First attempt: we perform type checking on parameters
		for (Method method : allMethods) {
			if (method.getName().equals(functionName) && method.getGenericParameterTypes().length == args.size()) {
				boolean lookupFails = false;
				for (int i = 0; i < args.size(); i++) {
					DataBinding<?> suppliedArg = args.get(i);
					Type argType = method.getGenericParameterTypes()[i];
					if (!TypeUtils.isTypeAssignableFrom(argType, suppliedArg.getDeclaredType())) {
						lookupFails = true;
					}
				}
				if (!lookupFails) {
					possiblyMatchingMethods.add(method);
				}
			}
		}
		// Second attempt: we don't check the types of parameters
		if (possiblyMatchingMethods.size() == 0) {
			for (Method method : allMethods) {
				if (method.getName().equals(functionName) && method.getGenericParameterTypes().length == args.size()) {
					possiblyMatchingMethods.add(method);
				}
			}
		}
		if (possiblyMatchingMethods.size() > 1) {
			LOGGER.warning("Please implement disambiguity here");
			/*for (DataBinding<?> arg : args) {
				System.out.println("arg " + arg + " of " + arg.getDeclaredType() + " / " + arg.getAnalyzedType());
			}*/
			// Return the first one
			// TODO: try to find the best one
			return MethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
		}
		else if (possiblyMatchingMethods.size() == 1) {
			return MethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
		}
		else {
			// We dont log it inconditionnaly, because this may happen (while for example inspectors are merged)
			// logger.warning("Cannot find method named " + functionName + " with args=" + args + "(" + args.size() + ") for type "
			// + parentType);
			return null;
		}
	}
}
