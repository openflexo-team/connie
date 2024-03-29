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

package org.openflexo.connie.binding.javareflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.openflexo.connie.binding.AccessorMethod;
import org.openflexo.connie.binding.Property;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * {@link KeyValueProperty} represent a property in Java language.
 * 
 * This is the low-level representation of a property associated to a Java type.<br>
 * A property is defined by
 * <ul>
 * <li>either a public field (in this case, the name of the property is the name of the field itself)</li>
 * <li>or a couple of get/set methods (set method is not mandatory for read-only property). In this case, the name is computed while
 * extracting basic template getXXX()/setXXX(value) while XXX is the name of the property</li>
 * </ul>
 * 
 * When the set method is not defined, settable property is false (read-only property)
 * 
 * This implementation should rely on a efficient hashCode()/equals() implementation, which is performed here using {@link #declaringType}
 * and {@link #name} pair.
 * 
 * @author sylvain
 * @see KeyValueLibrary
 *
 */
public class KeyValueProperty extends Observable implements Property {
	static final Logger LOGGER = Logger.getLogger(KeyValueProperty.class.getPackage().getName());

	/** Stores property's name */
	protected String name;

	/** Stores related object'class */
	protected Class<?> declaringClass;

	/** Stores related object'type */
	protected Type declaringType;

	/**
	 * Stores related field (if this one is public) or null if field is protected or non-existant
	 */
	protected Field field;

	/** Stores related type (the class of related property) */
	protected Type type;

	/**
	 * Stores related "get" method (if this one is public) or null if method is protected or non-existant
	 */
	protected Method getMethod;

	/**
	 * Stores related "set" method (if this one is public) or null if method is protected or non-existant
	 */
	protected Method setMethod;

	private boolean settable = false;

	KeyValueProperty(Type aDeclaringType, String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException {
		declaringClass = TypeUtils.getBaseClass(aDeclaringType);
		declaringType = aDeclaringType;
		init(propertyName, setMethodIsMandatory);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((declaringType == null) ? 0 : declaringType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValueProperty other = (KeyValueProperty) obj;
		if (declaringType == null) {
			if (other.declaringType != null)
				return false;
		}
		else if (!declaringType.equals(other.declaringType))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Initialize this property, given a propertyName.<br>
	 * This method is called during constructor invokation. NB: to be valid, a property should be identified by at least the field or the
	 * get/set methods pair. If the field is accessible, and only the get or the set method is accessible, a warning will be thrown.
	 */
	protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException {
		name = propertyName;

		// System.out.println("Declaring type = "+declaringType+" search property "+propertyName);

		String propertyNameWithFirstCharToUpperCase = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());

		field = null;

		try {
			field = declaringClass.getField(name);
		} catch (NoSuchFieldException e) {
			// Debugging.debug ("NoSuchFieldException, trying to find get/set
			// methods pair");
		} catch (SecurityException e) {
			// Debugging.debug ("SecurityException, trying to find get/set
			// methods pair");
		}

		getMethod = searchMatchingGetMethod(declaringClass, name);

		if (field == null) {
			if (getMethod == null) {
				throw new InvalidKeyValuePropertyException("No public field " + name + " found, nor method matching " + name + "() nor "
						+ "_" + name + "() nor " + "get" + propertyNameWithFirstCharToUpperCase + "() nor " + "_get"
						+ propertyNameWithFirstCharToUpperCase + "() found in class:" + declaringClass.getName());
			}
			type = getMethod.getGenericReturnType();
		}
		else { // field != null
			type = field.getGenericType();
			if (getMethod != null) {
				if (getMethod.getGenericReturnType() != type) {
					LOGGER.warning("Public field " + name + " found, with type " + type + " found " + " and method " + getMethod.getName()
							+ " found " + " declaring return type " + getMethod.getReturnType() + " Ignoring method...");
					getMethod = null;
				}
			}
		}

		setMethod = searchMatchingSetMethod(declaringClass, name, type);

		if (setMethodIsMandatory) {
			if (setMethod == null) {
				if (field == null) {
					throw new InvalidKeyValuePropertyException("No public field " + name + " found, nor method matching " + "set"
							+ propertyNameWithFirstCharToUpperCase + "(" + type + ") or " + "_set" + propertyNameWithFirstCharToUpperCase
							+ "(" + type + ") found " + "in class " + declaringClass);
				}
				if (getMethod != null) {
					// Debugging.debug ("Public field "+propertyName+"
					// found, with type "
					// + type.getName()+ " found "
					// + " and method "+getMethod.getName()+" found "
					// + " but no method matching "
					// +"set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+")
					// or "
					// +"_set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+")
					// found."
					// +" Will use directly the field to set values.");
				}
			}
		}

		settable = field != null || setMethod != null;

		if (getMethod != null && setMethod != null) {
			// If related field exist (is public) and accessors methods exists
			// also,
			// the operations are processed using accessors (field won't be used
			// directly,
			// and should be set to null).
			field = null;
		}

		// System.out.println("Made KeyValueProperty "+name+" for class "+declaringClass.getSimpleName()+" type="+type);

		if (TypeUtils.isGeneric(type)) {
			type = TypeUtils.makeInstantiatedType(type, declaringType);
		}

		/*
		 * if (type instanceof TypeVariable) { TypeVariable<GenericDeclaration>
		 * tv = (TypeVariable<GenericDeclaration>)type;
		 * //System.out.println("Found type variable "
		 * +tv+" name="+tv.getName()+" GD="+tv.getGenericDeclaration()); if
		 * (declaringType instanceof ParameterizedType) { GenericDeclaration gd
		 * = tv.getGenericDeclaration(); for (int i=0;
		 * i<gd.getTypeParameters().length; i++) { if (gd.getTypeParameters()[i]
		 * == tv) { type =
		 * ((ParameterizedType)declaringType).getActualTypeArguments()[i]; //
		 * Found matching parameterized type } } } }
		 */

	}

	/**
	 * Try to find a matching "get" method, such as (in order):
	 * <ul>
	 * <li>propertyName()</li>
	 * <li>_propertyName()</li>
	 * <li>getPropertyName()</li>
	 * <li>_getPropertyName()</li>
	 * </ul>
	 * Returns corresponding method, null if no such method exist
	 */
	protected Method searchMatchingGetMethod(Class<?> aDeclaringClass, String propertyName) {

		Method returnedMethod = null;
		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		String[] tries = new String[5];

		tries[0] = "get" + propertyNameWithFirstCharToUpperCase;
		tries[1] = propertyName;
		tries[2] = "_" + propertyName;
		tries[3] = "_get" + propertyNameWithFirstCharToUpperCase;
		tries[4] = "is" + propertyNameWithFirstCharToUpperCase;

		for (String trie : tries) {
			try {
				returnedMethod = aDeclaringClass.getMethod(trie);
				if (returnedMethod != null) {
					return returnedMethod;
				}
			} catch (SecurityException err) {
				// we continue
			} catch (NoSuchMethodException err) {
				// we continue
			} catch (NoClassDefFoundError err) {
				// we continue
			}
		}

		// If declaring class is interface, also lookup in Object class
		if (returnedMethod == null && aDeclaringClass.isInterface()) {
			return searchMatchingGetMethod(Object.class, propertyName);
		}

		// TODO: this code is totally wrong ??? i commented it
		// If everything fails, try static class methods
		/*
		 * for (String trie : tries) { try { return ((Class)
		 * aDeclaringClass.getClass()).getMethod(trie, (Class<?>[]) null); }
		 * catch (SecurityException err) { // we continue } catch
		 * (NoSuchMethodException err) { // we continue } }
		 */

		// Debugging.debug ("No method matching "
		// +propertyName+"() or "
		// +"_"+propertyName+"() or "
		// +"get"+propertyNameWithFirstCharToUpperCase+"() or "
		// +"_get"+propertyNameWithFirstCharToUpperCase+"() found.");

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
	protected Method searchMatchingSetMethod(Class<?> aDeclaringClass, String propertyName, Type aType) {
		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());
		List<String> tries = new ArrayList<>();
		tries.add("set" + propertyNameWithFirstCharToUpperCase);
		tries.add("_set" + propertyNameWithFirstCharToUpperCase);
		if (TypeUtils.isBoolean(aType)) {
			if (propertyName.startsWith("is")) {
				String propertyNameWithFirstCharToUpperCase2 = propertyName.substring(2);
				tries.add("set" + propertyNameWithFirstCharToUpperCase2);
				tries.add("_set" + propertyNameWithFirstCharToUpperCase2);
			}
			else if (propertyName.startsWith("_is")) {
				String propertyNameWithFirstCharToUpperCase2 = propertyName.substring(3);
				tries.add("set" + propertyNameWithFirstCharToUpperCase2);
				tries.add("_set" + propertyNameWithFirstCharToUpperCase2);
			}
		}

		if (aType instanceof Class) {
			for (Method m : aDeclaringClass.getMethods()) {
				for (String t : tries) {
					if (m.getName().equals(t) && m.getParameterTypes().length == 1
							&& TypeUtils.isTypeAssignableFrom(aType, m.getParameterTypes()[0])) {
						return m;
					}
				}
			}
		}
		else {
			for (Method m : aDeclaringClass.getMethods()) {
				for (String t : tries) {
					if (m.getName().equals(t) && m.getGenericParameterTypes().length == 1
							&& TypeUtils.isTypeAssignableFrom(aType, m.getGenericParameterTypes()[0])) {
						return m;
					}
				}
			}
		}

		// Find with super types (typed with generics)
		Type superType = TypeUtils.getSuperType(aType);
		if (superType != null) {
			// Try with a super class
			Method returned = searchMatchingSetMethod(aDeclaringClass, propertyName, superType);
			if (returned != null) {
				return returned;
			}
		}

		// Finally try without generics
		if (TypeUtils.getBaseClass(aType) != null && TypeUtils.getBaseClass(aType).getSuperclass() != null
				&& TypeUtils.getBaseClass(aType).getSuperclass() != superType) {
			return searchMatchingSetMethod(aDeclaringClass, propertyName, TypeUtils.getBaseClass(aType).getSuperclass());
		}
		/*
		 * Class typeClass = TypeUtils.getBaseClass(aType);
		 * 
		 * if (typeClass != null && typeClass.getSuperclass() != null) { // Try
		 * with a super class return searchMatchingSetMethod(declaringClass,
		 * propertyName, typeClass.getSuperclass()); }
		 */

		return null;

	}

	/**
	 * Stores related "get" method (if this one is public) or null if method is protected/private or non-existant
	 */
	public Method getGetMethod() {

		return getMethod;
	}

	/**
	 * Stores related "set" method (if this one is public) or null if method is protected/private or non-existant
	 */
	public Method getSetMethod() {

		return setMethod;
	}

	/**
	 * Returns name of this property
	 */
	@Override
	public String getName() {

		return name;
	}

	/**
	 * Returns related object class (never null)
	 */
	public Class<?> getDeclaringClass() {

		return declaringClass;
	}

	public Type getDeclaringType() {
		return declaringType;
	}

	/**
	 * Returns related field (if this one is public) or null if field is protected or non-existant
	 */
	public Field getField() {

		return field;
	}

	/**
	 * Returns related type
	 */
	@Override
	public Type getType() {
		return type;
	}

	/**
	 * Search and returns all methods (as {@link AccessorMethod} objects) of related class whose names is in the specified string list, with
	 * exactly the specified number of parameters, ascendant ordered regarding parameters specialization.
	 * 
	 * @see AccessorMethod
	 */
	protected TreeSet<AccessorMethod> searchMethodsWithNameAndParamsNumber(String[] searchedNames, int paramNumber) {

		TreeSet<AccessorMethod> returnedTreeSet = new TreeSet<>();
		Method[] allMethods = declaringClass.getMethods();

		for (int i = 0; i < allMethods.length; i++) {
			Method tempMethod = allMethods[i];
			for (int j = 0; j < searchedNames.length; j++) {
				if (tempMethod.getName().equalsIgnoreCase(searchedNames[j]) && tempMethod.getParameterTypes().length == paramNumber) {
					// This is a good candidate
					returnedTreeSet.add(new AccessorMethod(this, tempMethod));
				}
			}
		}
		// Debugging.debug ("Class "+objectClass.getName()+": found "
		// +returnedTreeSet.size()+" accessors:");
		// for (Iterator i = returnedTreeSet.iterator(); i.hasNext();) {
		// Debugging.debug ("> "+((AccessorMethod)i.next()).getMethod());
		// }
		return returnedTreeSet;
	}

	public String getSerializationRepresentation() {
		return name;
	}

	public boolean isSettable() {
		return settable;
	}

	@Override
	public String toString() {
		return "KeyValueProperty: " + (declaringClass != null ? declaringClass.getSimpleName() : declaringType) + "." + name;
	}

	public String getLabel() {
		return getName();
	}

	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (resultingType != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(resultingType);
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		}
		else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getName() + "</b></p>";
		// returned +=
		// "<p><i>"+(property.getDescription()!=null?property.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	/**
	 * Returns Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an {@code Object} value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public Object getObjectValue(Object object) {
		if (object == null) {
			throw new InvalidKeyValuePropertyException("No object is specified");
		}
		Object currentObject = object;
		if (field != null) {
			try {
				return field.get(currentObject);
			} catch (Exception e) {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + declaringClass.getName()
						+ ": field " + field.getName() + " Exception raised: " + e.toString());
			}
		}
		else if (getMethod != null) {
			if (!getMethod.getDeclaringClass().isAssignableFrom(currentObject.getClass())) {
				throw new InvalidKeyValuePropertyException("Object is not an instance of declaring class: expected "
						+ getMethod.getDeclaringClass().getName() + ", but was " + currentObject.getClass());
			}
			try {
				return getMethod.invoke(currentObject, (Object[]) null);
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
				throw new AccessorInvocationException("AccessorInvocationException: class " + declaringClass.getName() + ": method "
						+ getMethod.getName() + " Exception raised: " + e.getTargetException().toString(), e);
			} catch (Exception e) {
				System.out.println("current object = " + currentObject);
				System.out.println("declaring class = " + declaringClass);
				System.out.println("Assignable=" + declaringClass.isAssignableFrom(currentObject.getClass()));
				System.out.println("getMethod=" + getMethod);
				System.out.println("declaring class = " + getMethod.getDeclaringClass());
				System.out.println("Assignable=" + getMethod.getDeclaringClass().isAssignableFrom(currentObject.getClass()));
				e.printStackTrace();
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + declaringClass.getName()
						+ ": method " + getMethod.getName() + " Exception raised: " + e.toString());
			}
		}
		else {
			throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
		}
	}

	/**
	 * Sets Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            an {@code Object} value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public void setObjectValue(Object aValue, Object object) {
		if (object == null) {
			throw new InvalidKeyValuePropertyException("No object is specified");
		}
		Object currentObject = object;
		if (field != null) {
			try {
				field.set(currentObject, aValue);
			} catch (Exception e) {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + declaringClass.getName()
						+ ": field " + field.getName() + " Exception raised: " + e.toString());
			}
		}
		else if (setMethod != null) {
			Object params[] = new Object[1];
			params[0] = aValue;
			if (setMethod.getDeclaringClass().isAssignableFrom(currentObject.getClass())) {
				try {
					setMethod.invoke(currentObject, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("AccessorInvocationException: class " + declaringClass.getName() + ": method "
							+ setMethod.getName() + " Exception raised: " + e.getTargetException().toString(), e);
				} catch (IllegalArgumentException e) {
					// e.printStackTrace();
					throw new InvalidKeyValuePropertyException(
							"InvalidKeyValuePropertyException: class " + declaringClass.getName() + ": method " + setMethod.getName()
									+ "Argument mismatch: tried to pass a '" + (aValue != null ? aValue.getClass().getName() : "null")
									+ " instead of a " + setMethod.getParameterTypes()[0] + " Exception raised: " + e.toString());
				} catch (Exception e) {
					// e.printStackTrace();
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + declaringClass.getName()
							+ ": field " + setMethod.getName() + " Exception raised: " + e.toString());
				}
			}
			else {
				throw new InvalidKeyValuePropertyException(
						"InvalidKeyValuePropertyException: " + ": method " + setMethod.getName() + " called for object " + currentObject
								+ currentObject.getClass().getName() + " instead of " + declaringClass.getName());
			}
		}
		else {
			throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
		}
	}

}
