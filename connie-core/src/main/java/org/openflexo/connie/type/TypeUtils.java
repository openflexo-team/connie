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

import java.io.File;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.expr.EvaluationType;
import org.openflexo.connie.type.WildcardTypeImpl.DefaultWildcardType;

import com.google.common.primitives.Primitives;

/**
 * Utility methods focusing on types introspection
 */
public class TypeUtils {

	static final Logger LOGGER = Logger.getLogger(TypeUtils.class.getPackage().getName());

	/**
	 * <p>
	 * Transforms the passed in type to a {@code Class} object. Type-checking method of convenience.
	 * </p>
	 * 
	 * @param aType
	 *            the type to be converted
	 * @return the corresponding {@code Class} object
	 * @throws IllegalStateException
	 *             if the conversion fails
	 */
	public static Class<?> getRawType(Type aType) {
		return getBaseClass(aType);
	}

	public static Class<?> getBaseClass(Type aType) {
		if (aType == null) {
			return null;
		}
		if (aType == ExplicitNullType.INSTANCE) {
			return Object.class;
		}
		if (aType == UndefinedType.INSTANCE) {
			return Object.class;
		}
		if (aType instanceof UnresolvedType) {
			return Object.class;
		}
		if (aType instanceof CustomType) {
			return ((CustomType) aType).getBaseClass();
		}
		if (isResolved(aType)) {
			if (aType instanceof Class) {
				return (Class<?>) aType;
			}
			else if (aType instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) aType).getRawType();
				if (rawType instanceof Class) {
					return (Class<?>) rawType;
				}
				if (rawType instanceof UnresolvedType) {
					return Object.class;
				}
				LOGGER.warning("Not handled: " + aType + " of " + aType.getClass().getName());
				return null;
			}
			else if (aType instanceof GenericArrayType) {
				Type componentType = ((GenericArrayType) aType).getGenericComponentType();
				return getBaseClass(componentType);
			}
			else {
				LOGGER.warning("Not handled: " + aType + " of " + aType.getClass().getName());
				return null;
			}
		}
		if (aType instanceof ParameterizedType) {
			return getBaseClass(((ParameterizedType) aType).getRawType());
		}
		if (aType instanceof WildcardType) {
			// System.out.println("WildcardType: " + aType);
			Type[] upperBounds = ((WildcardType) aType).getUpperBounds();
			// Type[] lowerBounds = ((WildcardType) aType).getLowerBounds();
			if (upperBounds == null || upperBounds.length == 0) {
				return Object.class;
			}
			// System.out.println("upper=" + upperBounds + " size=" + upperBounds.length);
			// System.out.println("lower=" + upperBounds + " size=" + lowerBounds.length);
			if (upperBounds.length > 0) {
				return getBaseClass(upperBounds[0]);
			}
		}
		if (aType instanceof TypeVariable) {
			TypeVariable<?> tv = (TypeVariable<?>) aType;
			StringBuffer upperBounds = new StringBuffer();
			boolean isFirst = true;
			for (Type upperBound : tv.getBounds()) {
				upperBounds.append((isFirst ? "" : ",") + upperBound.toString());
				isFirst = false;
			}
			// logger.warning("Unresolved TypeVariable: " + tv.getName() + " " + tv.getGenericDeclaration() + " bounds=" + upperBounds);
			if (tv.getBounds().length > 0) {
				return getBaseClass(tv.getBounds()[0]);
			}
			return Object.class;
		}
		if (aType instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) aType).getGenericComponentType();
			return getBaseClass(componentType);
		}

		LOGGER.warning("Not handled: " + aType.getClass().getName());
		return null;
	}

	public static boolean isClassAncestorOf(Class<?> parentClass, Class<?> childClass) {
		if (parentClass == null) {
			return false;
		}
		if (childClass == null) {
			return false;
		}

		if (isVoid(parentClass)) {
			return isVoid(childClass);
		}
		if (isVoid(childClass)) {
			return isVoid(parentClass);
		}

		// Special cases
		if (parentClass == Object.class && childClass.isPrimitive()) {
			return true;
		}
		if (parentClass.isPrimitive()) {
			return isClassAncestorOf(fromPrimitive(parentClass), childClass);
		}
		if (childClass.isPrimitive()) {
			return isClassAncestorOf(parentClass, fromPrimitive(childClass));
		}

		// Normal case
		return parentClass.isAssignableFrom(childClass);
	}

	public static Class<?> toPrimitive(Class<?> aClass) {
		if (isDouble(aClass)) {
			return Double.TYPE;
		}
		if (isFloat(aClass)) {
			return Float.TYPE;
		}
		if (isLong(aClass)) {
			return Long.TYPE;
		}
		if (isInteger(aClass)) {
			return Integer.TYPE;
		}
		if (isShort(aClass)) {
			return Short.TYPE;
		}
		if (isByte(aClass)) {
			return Byte.TYPE;
		}
		if (isBoolean(aClass)) {
			return Boolean.TYPE;
		}
		if (isChar(aClass)) {
			return Character.TYPE;
		}
		if (isVoid(aClass)) {
			return Void.TYPE;
		}
		return aClass;
	}

	public static Class<?> fromPrimitive(Class<?> aClass) {
		if (isDouble(aClass)) {
			return Double.class;
		}
		if (isFloat(aClass)) {
			return Float.class;
		}
		if (isLong(aClass)) {
			return Long.class;
		}
		if (isInteger(aClass)) {
			return Integer.class;
		}
		if (isShort(aClass)) {
			return Short.class;
		}
		if (isByte(aClass)) {
			return Byte.class;
		}
		if (isBoolean(aClass)) {
			return Boolean.class;
		}
		if (isChar(aClass)) {
			return Character.class;
		}
		if (isVoid(aClass)) {
			return Void.class;
		}
		return aClass;
	}

	public static boolean isPrimitive(Type type) {
		return type != null && Primitives.allPrimitiveTypes().contains(type);
	}

	public static boolean isWrapperClass(Class<?> klass) {
		return klass != null && Primitives.isWrapperType(klass);
	}

	public static boolean isNumber(Type type) {
		if (type == null) {
			return false;
		}
		return isDouble(type) || isFloat(type) || isLong(type) || isInteger(type) || isShort(type) || isByte(type);
	}

	public static boolean isDouble(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Double.class) || type.equals(Double.TYPE);
	}

	public static boolean isFloat(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Float.class) || type.equals(Float.TYPE);
	}

	public static boolean isInteger(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Integer.class) || type.equals(Integer.TYPE);
	}

	public static boolean isLong(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Long.class) || type.equals(Long.TYPE);
	}

	public static boolean isObject(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Object.class);
	}

	public static boolean isFile(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(File.class);
	}

	public static boolean isShort(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Short.class) || type.equals(Short.TYPE);
	}

	public static boolean isString(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(String.class);
	}

	public static boolean isDate(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Date.class);
	}

	public static boolean isVoid(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Void.class) || type.equals(Void.TYPE);
	}

	public static boolean isBoolean(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Boolean.class) || type.equals(Boolean.TYPE);
	}

	public static boolean isByte(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Byte.class) || type.equals(Byte.TYPE);
	}

	public static boolean isChar(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Character.class) || type.equals(Character.TYPE);
	}

	public static boolean isList(Type type) {
		if (type == null) {
			return false;
		}
		if (type.equals(List.class)) {
			return true;
		}
		Class<?> baseClass = getBaseClass(type);
		if (baseClass != null && baseClass.equals(List.class)) {
			return true;
		}
		return false;
	}

	public static boolean isEnum(Type type) {
		return (type instanceof Class && ((Class) type).isEnum());
	}

	public static EvaluationType kindOfType(Type type) {
		if (isBoolean(type)) {
			return EvaluationType.BOOLEAN;
		}
		else if (isInteger(type) || isLong(type) || isShort(type) || isChar(type) || isByte(type)) {
			return EvaluationType.ARITHMETIC_INTEGER;
		}
		else if (isFloat(type) || isDouble(type)) {
			return EvaluationType.ARITHMETIC_FLOAT;
		}
		else if (isString(type)) {
			return EvaluationType.STRING;
		}
		return EvaluationType.LITERAL;
	}

	public static boolean isAssignableTo(Object object, Type type) {
		return isTypeAssignableFrom(type, object != null ? object.getClass() : null);
	}

	public static boolean isAssignableTo(Collection<? extends Object> objects, Type type) {
		for (Object object : objects) {
			if (!isAssignableTo(object, type)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isTypeAssignableFrom(Type aType, Type anOtherType) {
		return isTypeAssignableFrom(aType, anOtherType, true);
	}

	/**
	 * Determines if the class or interface represented by supplied {@code aType} object is either the same as, or is a superclass or
	 * superinterface of, the class or interface represented by the specified {@code anOtherType} parameter. It returns {@code true} if so;
	 * otherwise false<br>
	 * This method also tried to resolve generics before to perform the assignability test
	 * 
	 * @param aType
	 * @param anOtherType
	 * @param permissive
	 *            is a flag indicating if basic conversion between primitive types is allowed: for example, an int may be assign to a float
	 *            value after required conversion.
	 * @return
	 */
	public static boolean isTypeAssignableFrom(Type aType, Type anOtherType, boolean permissive) {
		// Test if anOtherType instanceof aType

		/*if (aType instanceof CustomType || anOtherType instanceof CustomType) {
			logger.info("Called " + aType + " isAssignableFrom(" + anOtherType + ")");
			logger.info("En gros je me demande si " + anOtherType + " est bien une instance de " + aType + " anOtherType est un "
					+ anOtherType.getClass().getSimpleName());
		}*/

		// If supplied type is null return false
		if (aType == null || anOtherType == null) {
			return false;
		}

		if (aType.equals(anOtherType)) {
			return true;
		}

		if (aType instanceof ProxyType) {
			return isTypeAssignableFrom(((ProxyType) aType).getReferencedType(), anOtherType, permissive);
		}
		if (anOtherType instanceof ProxyType) {
			return isTypeAssignableFrom(aType, ((ProxyType) anOtherType).getReferencedType(), permissive);
		}
		if (anOtherType == ExplicitNullType.INSTANCE) {
			return true;
		}
		if (anOtherType == UndefinedType.INSTANCE) {
			return true;
		}
		if (anOtherType == DiamondType.INSTANCE) {
			return true;
		}
		if (anOtherType instanceof UnresolvedType) {
			return false;
		}
		if (aType instanceof UnresolvedType) {
			return false;
		}

		// Everything could be assigned to Object
		if (isObject(aType)) {
			return true;
		}

		// Special case for Custom types
		if (aType instanceof CustomType) {
			return ((CustomType) aType).isTypeAssignableFrom(anOtherType, permissive);
		}

		if (anOtherType instanceof CustomType && isTypeAssignableFrom(aType, ((CustomType) anOtherType).getBaseClass())) {
			return true;
		}

		if (isVoid(aType)) {
			return isVoid(anOtherType);
		}

		if (isBoolean(aType)) {
			return isBoolean(anOtherType);
		}

		if (isDouble(aType)) {
			return isDouble(anOtherType) || isFloat(anOtherType) || isLong(anOtherType) || isInteger(anOtherType) || isShort(anOtherType)
					|| isChar(anOtherType) || isByte(anOtherType);
		}

		if (isFloat(aType)) {
			return isDouble(anOtherType) && permissive || isFloat(anOtherType) || isLong(anOtherType) || isInteger(anOtherType)
					|| isShort(anOtherType) || isChar(anOtherType) || isByte(anOtherType);
		}

		if (isLong(aType)) {
			return isLong(anOtherType) || isInteger(anOtherType) || isShort(anOtherType) || isChar(anOtherType) || isByte(anOtherType);
		}

		if (isInteger(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) || isShort(anOtherType) || isChar(anOtherType)
					|| isByte(anOtherType);
		}

		if (isShort(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) && permissive || isShort(anOtherType) || isChar(anOtherType)
					|| isByte(anOtherType);
		}

		if (isChar(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) && permissive || isShort(anOtherType) && permissive
					|| isChar(anOtherType) || isByte(anOtherType);
		}

		if (isByte(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) && permissive || isShort(anOtherType) && permissive
					|| isChar(anOtherType) || isByte(anOtherType);
		}

		if (aType instanceof WildcardType && ((WildcardType) aType).getUpperBounds().length > 0) {
			if (anOtherType instanceof WildcardType) {
				// If two wildcards, perform check on both upper bounds
				return isTypeAssignableFrom(((WildcardType) aType).getUpperBounds()[0], ((WildcardType) anOtherType).getUpperBounds()[0],
						permissive);
			}
			if (permissive) {
				// We will compare base class only
				Class<?> c1 = getBaseClass(((WildcardType) aType).getUpperBounds()[0]);
				Class<?> c2 = getBaseClass(anOtherType);
				return isTypeAssignableFrom(c1, c2);
			}
			// Perform check on first upper bound only
			return isTypeAssignableFrom(((WildcardType) aType).getUpperBounds()[0], anOtherType, permissive);
		}

		if (anOtherType instanceof WildcardType && ((WildcardType) anOtherType).getUpperBounds().length == 1) {
			return isTypeAssignableFrom(aType, ((WildcardType) anOtherType).getUpperBounds()[0], permissive);
		}

		if (aType instanceof GenericArrayType) {
			// logger.info("Called "+aType+" isAssignableFrom("+anOtherType+")");
			// logger.info("anOtherType is a "+anOtherType.getClass());
			if (anOtherType instanceof GenericArrayType) {
				return isTypeAssignableFrom(((GenericArrayType) aType).getGenericComponentType(),
						((GenericArrayType) anOtherType).getGenericComponentType(), permissive);
			}
			else if (anOtherType instanceof Class && ((Class<?>) anOtherType).isArray()) {
				return isTypeAssignableFrom(((GenericArrayType) aType).getGenericComponentType(),
						((Class<?>) anOtherType).getComponentType(), permissive);
			}
			return false;
		}

		// Look if we are on same class
		if (aType instanceof Class && anOtherType instanceof Class) {
			return isClassAncestorOf((Class<?>) aType, (Class<?>) anOtherType);
		}

		if (!isClassAncestorOf(getBaseClass(aType), getBaseClass(anOtherType))) {
			return false;
		}

		// logger.info(""+getBaseClass(aType)+" is ancestor of "+getBaseClass(anOtherType));

		if (aType instanceof Class || anOtherType instanceof Class) {
			// One of two types is not parameterized, we cannot check, return true
			return true;
		}

		if (aType instanceof ParameterizedType && anOtherType instanceof WildcardType
				&& ((WildcardType) anOtherType).getUpperBounds().length > 0) {
			Type t = ((WildcardType) anOtherType).getUpperBounds()[0];
			return isTypeAssignableFrom(aType, t, permissive);
		}

		if (aType instanceof ParameterizedType && anOtherType instanceof ParameterizedType) {
			ParameterizedType t1 = (ParameterizedType) aType;
			ParameterizedType t2 = (ParameterizedType) anOtherType;

			// Now check that parameters size are the same
			if (t1.getActualTypeArguments().length != t2.getActualTypeArguments().length) {
				return false;
			}

			// Now, we have to compare parameter per parameter
			for (int i = 0; i < t1.getActualTypeArguments().length; i++) {
				Type st1 = t1.getActualTypeArguments()[i];
				if (isPureWildCard(st1) && t1.getRawType() instanceof Class
						&& ((Class<?>) t1.getRawType()).getTypeParameters().length > i) {
					// Fixed assignability issue with wildcards as natural bounds of generic type
					TypeVariable<?> TV1 = ((Class<?>) t1.getRawType()).getTypeParameters()[i];
					st1 = new DefaultWildcardType(TV1.getBounds(), new Type[0]);
				}
				Type st2 = t2.getActualTypeArguments()[i];
				if (isPureWildCard(st2) && t2.getRawType() instanceof Class
						&& ((Class<?>) t2.getRawType()).getTypeParameters().length > i) {
					// Fixed assignability issue with wildcards as natural bounds of generic type
					TypeVariable<?> TV2 = ((Class<?>) t2.getRawType()).getTypeParameters()[i];
					st2 = new DefaultWildcardType(TV2.getBounds(), new Type[0]);
				}
				if (!isTypeAssignableFrom(st1, st2, true)) {
					return false;
				}
			}
			return true;
		}

		// In this case, the type is not fully resolved, we only consider the first upper bound
		if (aType instanceof TypeVariable) {
			TypeVariable<?> tv = (TypeVariable<?>) aType;
			if (tv.getBounds() != null && tv.getBounds().length > 0 && tv.getBounds()[0] != null) {
				return isTypeAssignableFrom(tv.getBounds()[0], anOtherType);
			}
		}

		if (aType instanceof WildcardType) {
			LOGGER.warning("WildcardType not implemented yet !");
		}

		if (aType instanceof UndefinedType) {
			return true;
		}

		try {
			return org.apache.commons.lang3.reflect.TypeUtils.isAssignable(anOtherType, aType);
		} catch (NullPointerException e) {
			// Might happen is some circonstance
			// eg:
			// anOtherType=R
			// aType=org.openflexo.model.validation.ValidationRule<R, V>
			return false;
		}
		/*if (getBaseEntity() == type.getBaseEntity()) {
			// Base entities are the same, let's analyse parameters
		
			// If one of both paramters def is empty (parameters are not defined, as before java5)
			// accept it without performing a test which is impossible to perform
			if ((getParameters().size() == 0)
					|| (type.getParameters().size() == 0)) return true;
		
			// Now check that parameters size are the same
			if (getParameters().size() != type.getParameters().size()) return false;
		
			// Now, we have to compare parameter per parameter
			for (int i=0; i<getParameters().size(); i++) 
			{
				DMType localParam = getParameters().elementAt(i);
				DMType sourceParam = type.getParameters().elementAt(i);
		
				if (localParam.getKindOfType() == KindOfType.WILDCARD
						&& localParam.getUpperBounds().size()==1) {
					DMType resultingSourceParamType;
					if (sourceParam.getKindOfType() == KindOfType.WILDCARD
							&& sourceParam.getUpperBounds().size()==1) {
						resultingSourceParamType = sourceParam.getUpperBounds().firstElement().bound;
					}
					else {
						resultingSourceParamType = sourceParam;
					}
					if (!localParam.getUpperBounds().firstElement().bound.isAssignableFrom(resultingSourceParamType,permissive)) {
						return false; 
					}
						}
						else if (!localParam.equals(sourceParam)) {
							return false;
						}
					}
					return true;    			
				}
		
				// Else it's a true ancestor
				else {
					//DMType parentType = makeInstantiatedDMType(type.getBaseEntity().getParentType(),type);
					DMType parentType = makeInstantiatedDMType(getBaseEntity().getClosestAncestorOf(type.getBaseEntity()),type);
					return isAssignableFrom(parentType,permissive);
				}*/

		// return false;
	}

	public static boolean isOfType(Object object, Type aType) {
		if (aType instanceof ProxyType) {
			return isOfType(object, ((ProxyType) aType).getReferencedType());
		}
		if (aType instanceof CustomType) {
			return ((CustomType) aType).isOfType(object, true);
		}
		if (object == null) {
			return true;
		}
		return isTypeAssignableFrom(aType, object.getClass());
	}

	public static String simpleRepresentation(Type aType) {
		if (aType == null) {
			return "null";
		}
		if (aType.equals(Boolean.TYPE)) {
			return "boolean";
		}
		if (aType.equals(Character.TYPE)) {
			return "char";
		}
		if (aType.equals(Byte.TYPE)) {
			return "byte";
		}
		if (aType.equals(Short.TYPE)) {
			return "short";
		}
		if (aType.equals(Integer.TYPE)) {
			return "int";
		}
		if (aType.equals(Long.TYPE)) {
			return "long";
		}
		if (aType.equals(Float.TYPE)) {
			return "float";
		}
		if (aType.equals(Double.TYPE)) {
			return "double";
		}
		if (aType instanceof CustomType) {
			return ((CustomType) aType).simpleRepresentation();
		}
		if (aType instanceof Class) {
			if (((Class<?>) aType).isAnonymousClass()) {
				return ((Class<?>) aType).getSuperclass().getSimpleName();
			}
			return ((Class<?>) aType).getSimpleName();
		}
		else if (aType instanceof ParameterizedType) {
			ParameterizedType t = (ParameterizedType) aType;
			StringBuilder sb = new StringBuilder();
			sb.append(simpleRepresentation(t.getRawType())).append("<");
			boolean isFirst = true;
			for (Type st : t.getActualTypeArguments()) {
				sb.append(isFirst ? "" : ",").append(simpleRepresentation(st));
				isFirst = false;
			}
			sb.append(">");
			return sb.toString();
		}
		else if (aType instanceof WildcardType) {
			WildcardType t = (WildcardType) aType;
			StringBuffer sb = new StringBuffer();
			sb.append("?");

			if (t.getUpperBounds() != null && t.getUpperBounds().length > 0) {
				sb.append(" extends ");
				boolean isFirst = true;
				for (Type u : t.getUpperBounds()) {
					sb.append((isFirst ? "" : ",") + simpleRepresentation(u));
					isFirst = false;
				}
			}

			if (t.getLowerBounds() != null && t.getLowerBounds().length > 0) {
				sb.append(" super ");
				boolean isFirst = true;
				for (Type l : t.getLowerBounds()) {
					sb.append((isFirst ? "" : ",") + simpleRepresentation(l));
					isFirst = false;
				}
			}
			return sb.toString();
		}
		else if (aType instanceof DiamondType) {
			return "";
		}
		return aType.toString();
	}

	public static String fullQualifiedRepresentation(Type aType) {
		if (aType == null) {
			return null;
		}
		if (aType instanceof CustomType) {
			return ((CustomType) aType).fullQualifiedRepresentation();
		}
		if (aType instanceof Class) {
			return ((Class<?>) aType).getName();
		}
		else if (aType instanceof ParameterizedType) {
			ParameterizedType t = (ParameterizedType) aType;
			StringBuilder sb = new StringBuilder();
			sb.append(fullQualifiedRepresentation(t.getRawType())).append("<");
			boolean isFirst = true;
			for (Type st : t.getActualTypeArguments()) {
				sb.append(isFirst ? "" : ",").append(fullQualifiedRepresentation(st));
				isFirst = false;
			}
			sb.append(">");
			return sb.toString();
		}
		else if (aType instanceof WildcardType) {
			WildcardType t = (WildcardType) aType;
			StringBuffer sb = new StringBuffer();
			sb.append("?");

			if (t.getUpperBounds() != null && t.getUpperBounds().length > 0) {
				sb.append(" extends ");
				boolean isFirst = true;
				for (Type u : t.getUpperBounds()) {
					sb.append((isFirst ? "" : ",") + TypeUtils.fullQualifiedRepresentation(u));
					isFirst = false;
				}
			}

			if (t.getLowerBounds() != null && t.getLowerBounds().length > 0) {
				sb.append(" super ");
				boolean isFirst = true;
				for (Type l : t.getLowerBounds()) {
					sb.append((isFirst ? "" : ",") + TypeUtils.fullQualifiedRepresentation(l));
					isFirst = false;
				}
			}
			return sb.toString();
		}
		return aType.toString();
	}

	public static boolean isResolved(Type type) {
		if (!(type instanceof Class || type instanceof GenericArrayType && isResolved(((GenericArrayType) type).getGenericComponentType())
				|| type instanceof ParameterizedType || type instanceof CustomType)) {
			return false;
		}
		if (type instanceof ConnieType) {
			return ((ConnieType) type).isResolved();
		}
		return true;
	}

	public static boolean isPureWildCard(Type type) {
		return type instanceof WildcardType && ((((WildcardType) type).getUpperBounds() == null)
				|| (((WildcardType) type).getUpperBounds().length == 0 || ((((WildcardType) type).getUpperBounds().length == 1
						&& (((WildcardType) type).getUpperBounds()[0].equals(Object.class))))));
	}

	/**
	 * Return flag indicating if this type is considered as generic A generic type is a type that is parameterized with type variable(s). If
	 * this type is resolved but contains a type in it definition containing itself a generic definition, then this type is also generic
	 * (this 'isGeneric' property is recursively transmissible).
	 * 
	 * @return a flag indicating whether this type is resolved or not
	 */
	public static boolean isGeneric(Type type) {
		if (type instanceof CustomType) {
			return false;
		}
		if (type instanceof Class) {
			return false;
		}
		if (type instanceof GenericArrayType) {
			return isGeneric(((GenericArrayType) type).getGenericComponentType());
		}
		if (type instanceof ParameterizedType) {
			for (Type t : ((ParameterizedType) type).getActualTypeArguments()) {
				if (isGeneric(t)) {
					return true;
				}
			}
			return false;
		}
		if (type instanceof TypeVariable) {
			return true;
		}
		if (type instanceof WildcardType) {
			WildcardType w = (WildcardType) type;
			if (w.getUpperBounds() != null && w.getUpperBounds().length > 0) {
				for (Type b : w.getUpperBounds()) {
					if (isGeneric(b)) {
						return true;
					}
				}
			}
			if (w.getLowerBounds() != null && w.getLowerBounds().length > 0) {
				for (Type b : w.getLowerBounds()) {
					if (isGeneric(b)) {
						return true;
					}
				}
			}
			return false;
		}
		LOGGER.warning("Unexpected " + type + (type != null ? " of " + type.getClass() : ""));
		return false;
	}

	/**
	 * Build a new type infering implicit typing constraints<br>
	 * Raw classes are parameterized
	 * 
	 * @param aType
	 * @return
	 */
	public static Type makeInferedType(Type aType) {
		// We handle here the case where we ask to resolve a type in the context of an
		// unresolved type (a class with generic arguments not specified)
		// We make an indirection with an infered context computed with default bounds
		// declared in generic type
		if (aType instanceof Class) {
			Class<?> aClass = (Class<?>) aType;
			TypeVariable<?>[] params = aClass.getTypeParameters();
			if (params.length > 0) {
				Type[] args = new Type[params.length];
				for (int i = 0; i < params.length; i++) {
					args[i] = new DefaultWildcardType(params[i].getBounds(), new Type[0]);
				}
				return new ParameterizedTypeImpl(aClass, args);
			}
		}
		return aType;

	}

	/**
	 * Build and return a contextualized type, given a typing context<br>
	 * 
	 * Returned type is build from supplied base type. Type variable are infered and resolved using supplied context.
	 * 
	 * @param type
	 *            : type to instanciate
	 * @param context
	 *            : context used to instanciate type
	 * @return
	 */
	public static Type makeInstantiatedType(Type type, Type context) {
		if (type == null) {
			return null;
		}

		if (context instanceof JavaCustomType) {
			return makeInstantiatedType(type, ((JavaCustomType) context).getJavaType());
		}

		if (!isGeneric(type)) {
			return type;
		}

		if (context instanceof ParameterizedType) {
			ParameterizedType contextParameterizedType = (ParameterizedType) context;
			Type[] actualTypeArguments = new Type[contextParameterizedType.getActualTypeArguments().length];
			boolean contextualizeType = false;
			for (int i = 0; i < contextParameterizedType.getActualTypeArguments().length; i++) {
				Type currentTypeArgument = contextParameterizedType.getActualTypeArguments()[i];
				if (isPureWildCard(currentTypeArgument) && contextParameterizedType.getRawType() instanceof Class) {
					Class<?> rawClass = (Class<?>) contextParameterizedType.getRawType();
					// an argument is defined as a pure wildcard
					// we can improve contextualization by finding bounds as defined by TypeVariable
					Type[] bounds = rawClass.getTypeParameters()[i].getBounds();
					if (bounds.length != 1 || (bounds.length == 1 && bounds[0] != Object.class)) {
						// Those conditions are required to generate a more contextualized type
						contextualizeType = true;
					}
					actualTypeArguments[i] = new DefaultWildcardType(bounds, new Type[0]);
				}
				else {
					actualTypeArguments[i] = currentTypeArgument;
				}
			}
			if (contextualizeType) {
				ParameterizedType fullyContextualizedType = new ParameterizedTypeImpl(contextParameterizedType.getRawType(),
						actualTypeArguments);
				// In this case, we use the bounds defined by the TypeVariable, and we recall the method with this most contextualized type
				return makeInstantiatedType(type, fullyContextualizedType);
			}
		}

		// We handle here the case where we ask to resolve a type in the context of an
		// unresolved type (a class with generic arguments not specified)
		// We make an indirection with an infered context computed with default bounds
		// declared in generic type
		if (context instanceof Class && ((Class<?>) context).getTypeParameters().length > 0) {
			return makeInstantiatedType(type, makeInferedType(context));
		}

		if (type instanceof ParameterizedType) {
			Type[] actualTypeArguments = new Type[((ParameterizedType) type).getActualTypeArguments().length];
			for (int i = 0; i < ((ParameterizedType) type).getActualTypeArguments().length; i++) {
				actualTypeArguments[i] = makeInstantiatedType(((ParameterizedType) type).getActualTypeArguments()[i], context);
			}
			return new ParameterizedTypeImpl(((ParameterizedType) type).getRawType(), actualTypeArguments);
		}

		if (type instanceof GenericArrayType) {
			return new GenericArrayTypeImpl(makeInstantiatedType(((GenericArrayType) type).getGenericComponentType(), context));
		}

		if (type instanceof TypeVariable) {
			TypeVariable<?> tv = (TypeVariable<?>) type;
			GenericDeclaration gd = tv.getGenericDeclaration();
			// System.out.println(">>>>>> Trying to infer type of type variable " + tv + " name=" + tv.getName() + " GD="
			// + tv.getGenericDeclaration() + " context=" + simpleRepresentation(context));
			if (gd instanceof Class) {
				if (context instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) context;
					if (!parameterizedType.getRawType().equals(gd)) {
						// Searching tv in gd, but this is not the right class, find relevant super type
						Type relevantSuperType = getSuperInterfaceType(context, (Class<?>) gd);
						return makeInstantiatedType(type, relevantSuperType);
					}
					for (int i = 0; i < gd.getTypeParameters().length; i++) {
						if (gd.getTypeParameters()[i].equals(tv)) {

							// Found matching parameterized type
							if (i < ((ParameterizedType) context).getActualTypeArguments().length) {
								// logger.info("********* return instantiatedType for "+type+" context="+context+" gd="+gd);
								if (!((ParameterizedType) context).getRawType().equals(gd)) {
									return makeInstantiatedType(type, getSuperType(context));
								}
								return ((ParameterizedType) context).getActualTypeArguments()[i];
							}
							LOGGER.warning(
									"Could not retrieve parameterized type " + tv + " with context " + simpleRepresentation(context));
							return type;
						}
					}
				}
				else if (context instanceof Class) {
					// TODO: instead of returning the first resolved type, we should build a list and return the most specialized type
					Class<?> contextClass = (Class<?>) context;
					if (contextClass.getGenericSuperclass() != null) {
						Type attemptFromSuperClass = makeInstantiatedType(type, contextClass.getGenericSuperclass());
						if (!attemptFromSuperClass.equals(type)) {
							return attemptFromSuperClass;
						}
					}
					for (Type superInterface : contextClass.getGenericInterfaces()) {
						Type attemptFromSuperInterface = makeInstantiatedType(type, superInterface);
						if (!attemptFromSuperInterface.equals(type)) {
							return attemptFromSuperInterface;
						}
					}
					// Could not find any further resolution
					return type;

				}
				else if (context instanceof WildcardType) {
					if (((WildcardType) context).getUpperBounds() != null && ((WildcardType) context).getUpperBounds().length > 0) {
						// In this case, we use the default upper bound
						return makeInstantiatedType(type, ((WildcardType) context).getUpperBounds()[0]);
					}
				}
			}
			else if (gd instanceof Method) {
				return type;
			}
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Not found type variable " + tv + " in context " + context + " GenericDeclaration=" + tv.getGenericDeclaration()
						+ " bounds=" + (tv.getBounds().length > 0 ? tv.getBounds()[0] : Object.class));
			}
			return tv.getBounds().length > 0 ? tv.getBounds()[0] : Object.class;
		}

		if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			Type[] upperBounds = new Type[wt.getUpperBounds() != null ? wt.getUpperBounds().length : 0];
			if (wt.getUpperBounds() != null) {
				for (int i = 0; i < wt.getUpperBounds().length; i++) {
					upperBounds[i] = makeInstantiatedType(wt.getUpperBounds()[i], context);
				}
			}
			Type[] lowerBounds = new Type[wt.getLowerBounds() != null ? wt.getLowerBounds().length : 0];
			if (wt.getLowerBounds() != null) {
				for (int i = 0; i < wt.getLowerBounds().length; i++) {
					lowerBounds[i] = makeInstantiatedType(wt.getLowerBounds()[i], context);
				}
			}
			return new DefaultWildcardType(upperBounds, lowerBounds);
		}

		LOGGER.warning("Unexpected " + type);
		return type;

	}

	public static Type getSuperType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType myType = (ParameterizedType) type;
			if (myType.getRawType() instanceof Class) {
				Type superType = ((Class<?>) myType.getRawType()).getGenericSuperclass();
				if (superType instanceof ParameterizedType) {
					Type[] actualTypeArguments = new Type[((ParameterizedType) superType).getActualTypeArguments().length];
					for (int i = 0; i < ((ParameterizedType) superType).getActualTypeArguments().length; i++) {
						Type tv2 = ((ParameterizedType) superType).getActualTypeArguments()[i];
						actualTypeArguments[i] = makeInstantiatedType(tv2, type);
					}
					return new ParameterizedTypeImpl(((Class<?>) ((ParameterizedType) type).getRawType()).getSuperclass(),
							actualTypeArguments);
				}
				// System.out.println("super type of " + simpleRepresentation(type) + " is " + simpleRepresentation(superType));
				return superType;
			}
			else {
				return Object.class;
			}
		}
		else if (type instanceof Class) {
			return ((Class<?>) type).getGenericSuperclass();
		}
		if (type instanceof CustomType) {
			return getSuperType(((CustomType) type).getBaseClass());
		}

		return null;
	}

	public static Type getSuperInterfaceType(Type type, Class<?> searchedSuperType) {
		Type superType = getSuperType(type);
		if (isTypeAssignableFrom(searchedSuperType, superType)) {
			return superType;
		}
		Type[] superInterfaceTypes = getSuperInterfaceTypes(type);
		for (Type t : superInterfaceTypes) {
			if (isTypeAssignableFrom(searchedSuperType, t)) {
				return t;
			}
		}
		return null;
	}

	public static Type[] getSuperInterfaceTypes(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType myType = (ParameterizedType) type;
			if (myType.getRawType() instanceof Class) {
				return ((Class<?>) myType.getRawType()).getGenericInterfaces();
			}
			else {
				return new Type[0];
			}
		}
		else if (type instanceof Class) {
			return ((Class<?>) type).getGenericInterfaces();
		}
		if (type instanceof CustomType) {
			return getSuperInterfaceTypes(((CustomType) type).getBaseClass());
		}

		return new Type[0];
	}

	public static Object castTo(Object object, Type desiredType) {
		if (object == null) {
			return null;
		}

		// System.out.println("Object type: "+object.getClass());
		// System.out.println("desiredType: "+desiredType);
		if (object.getClass().equals(desiredType)) {
			return object;
		}

		if (desiredType.equals(String.class)) {
			return object.toString();
		}

		if (object instanceof Number) {
			if (TypeUtils.isByte(desiredType)) {
				return ((Number) object).byteValue();
			}
			if (TypeUtils.isShort(desiredType)) {
				return ((Number) object).shortValue();
			}
			if (TypeUtils.isInteger(desiredType)) {
				return ((Number) object).intValue();
			}
			if (TypeUtils.isLong(desiredType)) {
				return ((Number) object).longValue();
			}
			if (TypeUtils.isDouble(desiredType)) {
				return ((Number) object).doubleValue();
			}
			if (TypeUtils.isFloat(desiredType)) {
				return ((Number) object).floatValue();
			}
		}
		return object;
	}

	public static void reduceToMostSpecializedClasses(Collection<Class<?>> someClasses) {

		if (someClasses.size() <= 1) {
			return;
		}

		for (Class<?> reducedClass : new ArrayList<>(someClasses)) {
			if (someClasses.contains(reducedClass)) {
				for (Class<?> aClass : new ArrayList<>(someClasses)) {
					if (!aClass.equals(reducedClass) && aClass.isAssignableFrom(reducedClass)) {
						someClasses.remove(aClass);
					}
				}
			}
		}

	}

	public static Class<?> getMostSpecializedClass(Collection<Class<?>> someClasses) {

		if (someClasses.size() == 0) {
			return null;
		}
		if (someClasses.size() == 1) {
			return someClasses.iterator().next();
		}
		Class<?>[] array = someClasses.toArray(new Class[someClasses.size()]);

		for (int i = 0; i < someClasses.size(); i++) {
			for (int j = i + 1; j < someClasses.size(); j++) {
				Class<?> c1 = array[i];
				Class<?> c2 = array[j];
				if (c1.isAssignableFrom(c2)) {
					someClasses.remove(c1);
					return getMostSpecializedClass(someClasses);
				}
				if (c2.isAssignableFrom(c1)) {
					someClasses.remove(c2);
					return getMostSpecializedClass(someClasses);
				}
			}
		}

		// No parent were found, take first item
		LOGGER.warning("Undefined specializing criteria between " + someClasses);
		return someClasses.iterator().next();

	}

	/**
	 * Utility method used to lookup an object associated with a given class<br>
	 * Lookup is performed using inheritance properties declared for supplied class, using inherited classes and interfaces<br>
	 * The object for the most specialized class is returned
	 * 
	 * @param aClass
	 * @param storedObjectForClasses
	 * @return
	 */
	public static <T> T objectForClass(Class<?> aClass, Map<Class<?>, T> storedObjectForClasses) {
		return objectForClass(aClass, storedObjectForClasses, true);
	}

	/**
	 * Utility method used to lookup an object associated with a given class<br>
	 * Lookup is performed using inheritance properties declared for supplied class, using inherited classes and interfaces<br>
	 * The object for the most specialized class is returned
	 * 
	 * @param aClass
	 * @param storedObjectForClasses
	 * @param storeResultInMap
	 *            if set to true, use the map to store the result for future use (caching)
	 * @return
	 */
	public static <T> T objectForClass(Class<?> aClass, Map<Class<?>, T> storedObjectForClasses, boolean storeResultInMap) {
		if (aClass == null) {
			return null;
		}

		T returned = storedObjectForClasses.get(aClass);
		if (returned != null) {
			return returned;
		}

		// We first check for exact lookup

		Map<Class<?>, T> matchingClasses = new HashMap<>();

		// First on super class
		Class<?> superclass = aClass.getSuperclass();
		if (superclass != null) {
			returned = storedObjectForClasses.get(superclass);
			if (returned != null) {
				matchingClasses.put(superclass, returned);
			}
		}

		// Then on interfaces
		for (Class<?> superInterface : aClass.getInterfaces()) {
			returned = storedObjectForClasses.get(superInterface);
			if (returned != null) {
				matchingClasses.put(superInterface, returned);
			}
		}

		// If only one class match, nice, we return the value
		if (matchingClasses.size() == 1) {
			returned = matchingClasses.get(matchingClasses.keySet().iterator().next());
			if (storeResultInMap) {
				storedObjectForClasses.put(aClass, returned);
			}
			return returned;
		}

		if (matchingClasses.size() > 1) {

			// Ambigous, return first result
			returned = matchingClasses.get(matchingClasses.keySet().iterator().next());
			if (storeResultInMap) {
				storedObjectForClasses.put(aClass, returned);
			}
			return returned;
		}

		// No matching classes
		// Now, we run recursively

		// First on super class
		if (superclass != null) {
			returned = objectForClass(superclass, storedObjectForClasses, storeResultInMap);
			if (returned != null) {
				matchingClasses.put(superclass, returned);
			}
		}

		// Then on interfaces
		for (Class<?> superInterface : aClass.getInterfaces()) {
			returned = objectForClass(superInterface, storedObjectForClasses, storeResultInMap);
			if (returned != null) {
				matchingClasses.put(superInterface, returned);
			}
		}

		// If only one class match, nice, we return the value
		if (matchingClasses.size() == 1) {

			returned = matchingClasses.get(matchingClasses.keySet().iterator().next());
			if (storeResultInMap) {
				storedObjectForClasses.put(aClass, returned);
			}
			return returned;

		}

		if (matchingClasses.size() > 1) {

			// Ambigous, return most specialized

			Class<?> mostSpecialized = null;
			int bestDistance = 1001;
			for (Class<?> c : matchingClasses.keySet()) {
				if (distance(aClass, c) < bestDistance) {
					mostSpecialized = c;
					bestDistance = distance(aClass, c);
				}
			}

			returned = matchingClasses.get(mostSpecialized);
			if (storeResultInMap) {
				storedObjectForClasses.put(aClass, returned);
			}

			return returned;

		}

		return null;
	}

	/**
	 * Return distance between two classes
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	private static int distance(Class<?> c1, Class<?> c2) {
		if (c2.equals(c1)) {
			return 0;
		}

		if (c1.isAssignableFrom(c2)) {

			if (c2.getSuperclass() != null) {
				int d1 = distance(c1, c2.getSuperclass());
				if (d1 < 1000) {
					return d1 + 1;
				}
			}
			for (Class<?> superInterface : c2.getInterfaces()) {
				int d1 = distance(c1, superInterface);
				if (d1 < 1000) {
					return d1 + 1;
				}
			}
		}

		if (c2.isAssignableFrom(c1)) {

			if (c1.getSuperclass() != null) {
				int d2 = distance(c2, c1.getSuperclass());
				if (d2 < 1000) {
					return d2 + 1;
				}
			}

			for (Class<?> superInterface : c1.getInterfaces()) {
				int d2 = distance(c2, superInterface);
				if (d2 < 1000) {
					return d2 + 1;
				}
			}
		}
		return 1000;
	}

	/**
	 * <p>
	 * Retrieves all the type arguments for this parameterized type including owner hierarchy arguments such as {@code 
	 * Outer<K,V>.Inner<T>.DeepInner<E>} . The arguments are returned in a {@link Map} specifying the argument type for each
	 * {@link TypeVariable}.
	 * </p>
	 * 
	 * @param type
	 *            specifies the subject parameterized type from which to harvest the parameters.
	 * @return a map of the type arguments to their respective type variables.
	 */
	public static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> rawType) {
		return org.apache.commons.lang3.reflect.TypeUtils.getTypeArguments(type, rawType);
	}

	/**
	 * Retrieve resolved type for type variable at specified index relatively to specified type, for supplied Type
	 * 
	 * @param type
	 * @param index
	 * @return
	 */
	public static Type getTypeArgument(Type type, Class<?> rawType, int index) {
		return getTypeArguments(type, rawType).get(rawType.getTypeParameters()[index]);
	}
}
