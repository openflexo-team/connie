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

package org.openflexo.kvc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.binding.MethodDefinition;
import org.openflexo.connie.type.TypeUtils;

public class KeyValueLibrary {

	private static final Logger LOGGER = Logger.getLogger(KeyValueLibrary.class.getPackage().getName());

	private static final Map<Type, Hashtable<String, KeyValueProperty>> PROPERTIES = new Hashtable<Type, Hashtable<String, KeyValueProperty>>();

	private static final Map<Type, Vector<KeyValueProperty>> DECLARED_KEY_VALUE_PROPERTIES = new Hashtable<Type, Vector<KeyValueProperty>>();

	private static final Map<Type, Vector<MethodDefinition>> DECLARED_METHODS = new Hashtable<Type, Vector<MethodDefinition>>();

	private static final Map<Type, Vector<KeyValueProperty>> ACCESSIBLE_KEY_VALUE_PROPERTIES = new Hashtable<Type, Vector<KeyValueProperty>>();

	private static final Map<Type, Vector<MethodDefinition>> ACCESSIBLE_METHODS = new Hashtable<Type, Vector<MethodDefinition>>();

	public static void clearCache() {
		PROPERTIES.clear();
		DECLARED_KEY_VALUE_PROPERTIES.clear();
		DECLARED_METHODS.clear();
		ACCESSIBLE_KEY_VALUE_PROPERTIES.clear();
		ACCESSIBLE_METHODS.clear();
	}

	public static KeyValueProperty getKeyValueProperty(Type declaringType, String propertyName) {
		if (declaringType == null) {
			return null;
		}
		Hashtable<String, KeyValueProperty> cacheForType = PROPERTIES.get(declaringType);
		if (cacheForType == null) {
			cacheForType = new Hashtable<String, KeyValueProperty>();
			PROPERTIES.put(declaringType, cacheForType);
		}
		KeyValueProperty returned = cacheForType.get(propertyName);
		if (returned == null) {
			try {
				returned = new KeyValueProperty(declaringType, propertyName, false);
				cacheForType.put(propertyName, returned);
			} catch (InvalidKeyValuePropertyException e) {
				// logger.warning("While computing getKeyValueProperty(" + propertyName + ") for " + declaringType + " message:" +
				// e.getMessage());
				// e.printStackTrace();
				return null;
			}
		}
		return returned;
	}

	public static Vector<KeyValueProperty> getDeclaredProperties(Type declaringType) {
		Vector<KeyValueProperty> returned = DECLARED_KEY_VALUE_PROPERTIES.get(declaringType);
		if (returned == null) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("build declaredProperties() for " + declaringType);
			}
			Vector<String> excludedSignatures = new Vector<String>();
			returned = searchForProperties(declaringType, true, excludedSignatures);
			DECLARED_KEY_VALUE_PROPERTIES.put(declaringType, returned);
			Vector<MethodDefinition> methods = searchForMethods(declaringType, excludedSignatures);
			DECLARED_METHODS.put(declaringType, methods);
		}
		return returned;
	}

	public static Vector<MethodDefinition> getDeclaredMethods(Type declaringType) {
		Vector<MethodDefinition> returned = DECLARED_METHODS.get(declaringType);
		if (returned == null) {
			LOGGER.fine("build declaredMethods() for " + declaringType);
			Vector<String> excludedSignatures = new Vector<String>();
			Vector<KeyValueProperty> properties = searchForProperties(declaringType, true, excludedSignatures);
			DECLARED_KEY_VALUE_PROPERTIES.put(declaringType, properties);
			returned = searchForMethods(declaringType, excludedSignatures);
			DECLARED_METHODS.put(declaringType, returned);
		}
		return returned;
	}

	public static Vector<KeyValueProperty> getAccessibleProperties(Type declaringType) {
		Vector<KeyValueProperty> returned = ACCESSIBLE_KEY_VALUE_PROPERTIES.get(declaringType);
		if (returned == null) {
			returned = new Vector<KeyValueProperty>();
			appendAccessibleProperties(declaringType, returned);
			Collections.sort(returned, new Comparator<KeyValueProperty>() {

				@Override
				public int compare(KeyValueProperty o1, KeyValueProperty o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			ACCESSIBLE_KEY_VALUE_PROPERTIES.put(declaringType, returned);
		}
		return returned;
	}

	public static void appendAccessibleProperties(Type declaringType, Vector<KeyValueProperty> returned) {
		Type current = declaringType;
		while (current != null) {
			Vector<KeyValueProperty> declaredProperties = getDeclaredProperties(current);
			for (KeyValueProperty p : declaredProperties) {
				boolean isAlreadyContained = false;
				for (KeyValueProperty p2 : returned) {
					if (p.getName().equals(p2.getName())) {
						isAlreadyContained = true;
						break;
					}
				}
				if (!isAlreadyContained) {
					returned.add(p);
				}
			}
			for (Type t : TypeUtils.getSuperInterfaceTypes(current)) {
				appendAccessibleProperties(t, returned);
			}

			Type superType = TypeUtils.getSuperType(current);

			// If this is a simple interface, at least inherits properties from Object class
			if (superType == null && TypeUtils.getBaseClass(current) != null && TypeUtils.getBaseClass(current).isInterface()) {
				current = Object.class;
			} else {
				current = superType;
			}

		}
	}

	public static Vector<MethodDefinition> getAccessibleMethods(Type declaringType) {
		Vector<MethodDefinition> returned = ACCESSIBLE_METHODS.get(declaringType);
		if (returned == null) {
			returned = new Vector<MethodDefinition>();
			Type current = declaringType;
			while (current != null) {
				returned.addAll(getDeclaredMethods(current));
				current = TypeUtils.getSuperType(current);
				// current = current.getSuperclass();
			}
			Collections.sort(returned, new Comparator<MethodDefinition>() {

				@Override
				public int compare(MethodDefinition o1, MethodDefinition o2) {
					return o1.getSignature().compareTo(o2.getSignature());
				}
			});
			ACCESSIBLE_METHODS.put(declaringType, returned);
		}
		return returned;
	}

	private static Vector<MethodDefinition> searchForMethods(Type declaringType, Vector<String> excludedSignatures) {
		Vector<MethodDefinition> returned = new Vector<MethodDefinition>();

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("searchForMethods()");
		}
		for (String excludedSignature : excludedSignatures) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Excluded: " + excludedSignature);
			}
		}

		Class theClass = TypeUtils.getBaseClass(declaringType);
		if (theClass == null) {
			LOGGER.warning("Cannot search properties for type: " + declaringType);
			return null;
		}

		try {
			Method[] declaredMethods = theClass.getDeclaredMethods();
			for (int i = 0; i < declaredMethods.length; i++) {
				Method method = declaredMethods[i];
				MethodDefinition methodDefinition = MethodDefinition.getMethodDefinition(declaringType, method);
				if (!excludedSignatures.contains(methodDefinition.getSignature())) {
					returned.add(methodDefinition);
				}
			}
		} catch (NoClassDefFoundError e) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Could not find class: " + e.getMessage());
			}
		} catch (Throwable e) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Unexpected exception raised " + e);
			}
			e.printStackTrace();
		}
		Collections.sort(returned, new Comparator<MethodDefinition>() {

			@Override
			public int compare(MethodDefinition o1, MethodDefinition o2) {
				return o1.getSignature().compareTo(o2.getSignature());
			}
		});
		return returned;
	}

	private static Vector<KeyValueProperty> searchForProperties(Type declaringTypeType, boolean includesGetOnlyProperties,
			Vector<String> excludedSignatures) {
		Vector<KeyValueProperty> returned = new Vector<KeyValueProperty>();

		Class<?> theClass = TypeUtils.getBaseClass(declaringTypeType);
		if (theClass == null) {
			LOGGER.warning("Cannot search properties for type: " + declaringTypeType);
			return null;
		}

		try {
			Method[] declaredMethods = theClass.getDeclaredMethods();
			for (int i = 0; i < declaredMethods.length; i++) {
				Method method = declaredMethods[i];
				KeyValueProperty newProperty = makeProperty(declaringTypeType, method, includesGetOnlyProperties, excludedSignatures);
				if (newProperty != null && !containsAPropertyNamed(returned, newProperty.getName())) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("Make property from method: " + method);
					}
					returned.add(newProperty);
				}
			}

			Field[] declaredFields = theClass.getDeclaredFields();

			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];

				KeyValueProperty newProperty = makeProperty(declaringTypeType, field);
				if (newProperty != null && !containsAPropertyNamed(returned, newProperty.getName())) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("Make property from field: " + field);
					}
					returned.add(newProperty);
				}
			}
		} catch (NoClassDefFoundError e) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Could not find class: " + e.getMessage());
			}
		} catch (Throwable e) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Unexpected exception raised " + e);
			}
			e.printStackTrace();
		}
		Collections.sort(returned, new Comparator<KeyValueProperty>() {

			@Override
			public int compare(KeyValueProperty o1, KeyValueProperty o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return returned;
	}

	private static boolean containsAPropertyNamed(Vector<KeyValueProperty> properties, String aName) {
		for (KeyValueProperty p : properties) {
			if (p.getName().equals(aName)) {
				return true;
			}
		}
		return false;
	}

	private static KeyValueProperty makeProperty(Type declaringType, Method method, boolean includesGetOnlyProperties,
			Vector<String> excludedSignatures) {
		Type returnType = method.getGenericReturnType();
		Type[] parameters = method.getGenericParameterTypes();

		if (returnType != Void.TYPE && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
				&& parameters.length == 0) {
			// This signature matches a GET property, lets continue !

			// Look for name
			String propertyName = method.getName();

			// Exclude it from methods
			if (excludedSignatures != null) {
				excludedSignatures.add(MethodDefinition.getMethodDefinition(declaringType, method).getSignature());
			}

			// Beautify property name

			if (propertyName.length() > 3 && propertyName.substring(0, 3).equalsIgnoreCase("get")) {
				propertyName = propertyName.substring(3);
			}
			if (propertyName.length() > 1 && propertyName.substring(0, 1).equals("_")) {
				propertyName = propertyName.substring(1);
			}

			// First char always to lower case
			propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1, propertyName.length());

			// Is there a SET method ?
			Method setMethod = searchMatchingSetMethod(declaringType, propertyName, returnType);
			boolean isSettable = setMethod != null;
			if (setMethod != null && excludedSignatures != null) {
				excludedSignatures.add(MethodDefinition.getMethodDefinition(declaringType, setMethod).getSignature());
			}

			// Creates and register the property

			if (includesGetOnlyProperties || isSettable) {

				return getKeyValueProperty(declaringType, propertyName);

			}

		}
		return null;
	}

	/**
	 * Build a new DMProperty
	 */
	private static KeyValueProperty makeProperty(Type declaringType, Field field) {
		Type fieldType = field.getGenericType();
		if (fieldType != Void.TYPE && Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
			// This signature matches a GET property, lets continue !

			// Look for name
			String propertyName = field.getName();

			return getKeyValueProperty(declaringType, propertyName);

		}

		return null;
	}

	/**
	 * Try to find a matching "set" method, such as (in order):
	 * <ul>
	 * <li>setPropertyName(Type)</li>
	 * <li>_setPropertyName(Type)</li>
	 * </ul>
	 * Returns corresponding method, null if no such method exist
	 */
	private static Method searchMatchingSetMethod(Type declaringType, String propertyName, Type aType) {

		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		Vector<String> tries = new Vector<String>();

		Type params[] = new Type[1];
		params[0] = aType;
		/*if (aType instanceof Class)
			params[0] = (Class)aType;
		else if (aType instanceof ParameterizedType)
			params[0] = (Class)((ParameterizedType)aType).getRawType();
		else if (aType instanceof TypeVariable){
			logger.warning ("Pas tres bien gere pour le moment "+aType.getClass());
			params[0] = Object.class;
		}*/

		tries.add("set" + propertyNameWithFirstCharToUpperCase);
		tries.add("_set" + propertyNameWithFirstCharToUpperCase);

		for (Enumeration<String> e = tries.elements(); e.hasMoreElements();) {
			try {
				String methodName = e.nextElement();
				// Method returned = type.getMethod(methodName, params);
				Method returned = getMethod(declaringType, methodName, params);
				if (returned != null) {
					return returned;
				}
			} catch (SecurityException err) {
				// we continue
			} // catch (NoSuchMethodException err) {
				// we continue
			// }
		}

		return null;

	}

	// Not anymore throw SuchMethodException (return null instead)
	// > performance issue
	private static Method getMethod(Type type, String methodName, Type... params) /*throws NoSuchMethodException*/{
		Class theClass = TypeUtils.getBaseClass(type);
		if (theClass == null) {
			LOGGER.warning("Cannot search properties for type: " + type);
			return null;
		}

		if (params == null) {
			params = new Type[0];
		}
		StringBuffer sb = null;
		if (LOGGER.isLoggable(Level.FINE)) {
			sb = new StringBuffer();
			for (Type t : params) {
				sb.append(" " + t.toString());
			}
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Looking for " + methodName + " with" + sb.toString());
			}
		}
		for (Method m : theClass.getMethods()) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Examining " + m);
			}
			if (m.getName().equals(methodName) && m.getGenericParameterTypes().length == params.length) {
				boolean paramMatches = true;
				for (int i = 0; i < params.length; i++) {
					if (!params[i].equals(m.getGenericParameterTypes()[i])) {
						paramMatches = false;
					}
				}
				if (paramMatches) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("Looking for " + methodName + " with" + sb.toString() + ": found");
					}
					return m;
				}
			}
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Looking for " + methodName + " with" + sb.toString() + ": NOT found");
		}
		// throw new NoSuchMethodException();
		return null;
	}

}
