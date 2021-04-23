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

package org.openflexo.connie.binding.javareflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.type.TypeUtils;

/**
 * This is base implementation for {@link BindingFactory} supporting java key-value conding for variables<br>
 * 
 * Accessible simple and function path elements are computed using Java fields/methods reflection A.P.I
 * 
 * 
 * @author sylvain
 *
 */
public abstract class JavaBasedBindingFactory implements BindingFactory {
	static final Logger LOGGER = Logger.getLogger(JavaBasedBindingFactory.class.getPackage().getName());

	private Map<Type, List<? extends SimplePathElement>> accessibleSimplePathElements = new HashMap<>();
	private Map<Type, List<? extends FunctionPathElement<?>>> accessibleFunctionPathElements = new HashMap<>();
	private Map<Type, Map<String, AbstractMethodDefinition>> storedFunctions = new HashMap<>();
	private Map<Type, Map<String, ConstructorDefinition>> storedConstructors = new HashMap<>();

	@Override
	public Type getTypeForObject(Object object) {
		if (object != null) {
			return object.getClass();
		}
		return Object.class;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(IBindingPathElement parent) {

		if (parent.getType() != null) {

			List<? extends SimplePathElement> returned = accessibleSimplePathElements.get(parent.getType());
			if (returned != null) {
				return returned;
			}

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
			List<JavaPropertyPathElement> newComputedList = new ArrayList<>();
			for (KeyValueProperty p : KeyValueLibrary.getAccessibleProperties(currentType)) {
				// System.out.println("on construit JavaPropertyPathElement pour " + p + " type=" + parent.getType());
				newComputedList.add(new JavaPropertyPathElement(parent, p));
			}
			accessibleSimplePathElements.put(parent.getType(), newComputedList);

			return newComputedList;
		}
		return null;
	}

	@Override
	public List<? extends FunctionPathElement<?>> getAccessibleFunctionPathElements(IBindingPathElement parent) {
		if (parent.getType() != null) {

			List<? extends FunctionPathElement<?>> returned = accessibleFunctionPathElements.get(parent.getType());
			if (returned != null) {
				return returned;
			}

			if (TypeUtils.getBaseClass(parent.getType()) == null) {
				return null;
			}
			Type currentType = parent.getType();
			if (currentType instanceof Class && ((Class<?>) currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class<?>) currentType);
			}
			List<JavaInstanceMethodPathElement> newComputedList = new ArrayList<>();
			for (InstanceMethodDefinition m : KeyValueLibrary.getAccessibleMethods(currentType)) {
				// System.out.println("on construit JavaMethodPathElement pour " + m);
				newComputedList.add(new JavaInstanceMethodPathElement(parent, m, null));
			}
			accessibleFunctionPathElements.put(parent.getType(), newComputedList);

			return newComputedList;
		}
		return null;
	}

	@Override
	public SimplePathElement makeSimplePathElement(IBindingPathElement father, String propertyName) {
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
	public FunctionPathElement<?> makeFunctionPathElement(IBindingPathElement father, Function function, List<DataBinding<?>> args) {
		if (function instanceof InstanceMethodDefinition) {
			return new JavaInstanceMethodPathElement(father, (InstanceMethodDefinition) function, args);
		}
		if (function instanceof StaticMethodDefinition) {
			return new JavaStaticMethodPathElement(father, (StaticMethodDefinition) function, args);
		}
		if (function instanceof ConstructorDefinition) {
			return new JavaNewInstanceMethodPathElement(father, (ConstructorDefinition) function, args);
		}
		return null;
	}

	private static String getSignature(String functionName, List<DataBinding<?>> args) {
		StringBuffer sb = new StringBuffer();
		sb.append(functionName);
		sb.append("(");
		boolean isFirst = true;
		for (DataBinding<?> arg : args) {
			sb.append((isFirst ? "" : ",") + TypeUtils.simpleRepresentation(arg.getDeclaredType()));
			isFirst = false;
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public AbstractMethodDefinition retrieveFunction(Type parentType, String functionName, List<DataBinding<?>> args) {

		Map<String, AbstractMethodDefinition> mapForType = storedFunctions.get(parentType);
		if (mapForType == null) {
			mapForType = new HashMap<>();
			storedFunctions.put(parentType, mapForType);
		}

		String signature = getSignature(functionName, args);
		AbstractMethodDefinition returned = mapForType.get(signature);
		if (returned != null) {
			return returned;
		}

		List<Method> possiblyMatchingMethods = new ArrayList<>();
		Class<?> typeClass = TypeUtils.getBaseClass(parentType);
		if (typeClass == null) {
			System.out.println("Cannot find typeClass for " + parentType);
			return null;
		}
		// System.out.println("On cherche la methode " + functionName + " pour " + args);
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
			returned = InstanceMethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
			mapForType.put(signature, returned);
			return returned;
		}
		else if (possiblyMatchingMethods.size() == 1) {
			returned = InstanceMethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
			mapForType.put(signature, returned);
			return returned;
		}
		else {
			// We dont log it inconditionnaly, because this may happen (while for example inspectors are merged)
			// LOGGER.warning(
			// "Cannot find method named " + functionName + " with args=" + args + "(" + args.size() + ") for type " + parentType);
			return null;
		}
	}

	// Note: in java, we don't care about functionName (which is the name of the declaring type)
	@Override
	public ConstructorDefinition retrieveConstructor(Type declaringType, String functionName, List<DataBinding<?>> args) {
		Map<String, ConstructorDefinition> mapForType = storedConstructors.get(declaringType);
		if (mapForType == null) {
			mapForType = new HashMap<>();
			storedConstructors.put(declaringType, mapForType);
		}

		String signature = getSignature(TypeUtils.fullQualifiedRepresentation(declaringType), args);
		ConstructorDefinition returned = mapForType.get(signature);
		if (returned != null) {
			return returned;
		}

		List<Constructor<?>> possiblyMatchingConstructors = new ArrayList<>();
		Class<?> typeClass = TypeUtils.getBaseClass(declaringType);
		if (typeClass == null) {
			System.out.println("Cannot find typeClass for " + declaringType);
			return null;
		}
		// System.out.println("On cherche la methode " + functionName + " pour " + args);
		Constructor<?>[] allConstructors = typeClass.getConstructors();
		// First attempt: we perform type checking on parameters
		for (Constructor<?> constructor : allConstructors) {
			if (constructor.getGenericParameterTypes().length == args.size()) {
				boolean lookupFails = false;
				for (int i = 0; i < args.size(); i++) {
					DataBinding<?> suppliedArg = args.get(i);
					Type argType = constructor.getGenericParameterTypes()[i];
					if (!TypeUtils.isTypeAssignableFrom(argType, suppliedArg.getDeclaredType())) {
						lookupFails = true;
					}
				}
				if (!lookupFails) {
					possiblyMatchingConstructors.add(constructor);
				}
			}
		}
		// Second attempt: we don't check the types of parameters
		if (possiblyMatchingConstructors.size() == 0) {
			for (Constructor<?> constructor : allConstructors) {
				if (constructor.getGenericParameterTypes().length == args.size()) {
					possiblyMatchingConstructors.add(constructor);
				}
			}
		}
		if (possiblyMatchingConstructors.size() > 1) {
			LOGGER.warning("Please implement disambiguity here");
			/*for (DataBinding<?> arg : args) {
				System.out.println("arg " + arg + " of " + arg.getDeclaredType() + " / " + arg.getAnalyzedType());
			}*/
			// Return the first one
			// TODO: try to find the best one
			returned = ConstructorDefinition.getConstructorDefinition(declaringType, possiblyMatchingConstructors.get(0));
			mapForType.put(signature, returned);
			return returned;
		}
		else if (possiblyMatchingConstructors.size() == 1) {
			returned = ConstructorDefinition.getConstructorDefinition(declaringType, possiblyMatchingConstructors.get(0));
			mapForType.put(signature, returned);
			return returned;
		}
		else {
			// We dont log it inconditionnaly, because this may happen (while for example inspectors are merged)
			// LOGGER.warning(
			// "Cannot find method named " + functionName + " with args=" + args + "(" + args.size() + ") for type " + parentType);
			return null;
		}
	}

	public void clear() {
		accessibleSimplePathElements.clear();
		accessibleFunctionPathElements.clear();
		storedFunctions.clear();
	}
}
