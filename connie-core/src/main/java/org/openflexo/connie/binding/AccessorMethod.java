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

package org.openflexo.connie.binding;

import java.lang.reflect.Method;

import org.openflexo.kvc.KeyValueProperty;

/**
 * <p>
 * {@code AccessorMethod} is a class representing a KeyValueProperty accessor method.
 * </p>
 * <p>
 * Because many different accessors could be defined in a class, all implementing different class-specific levels (more or less specialized,
 * regarding parameters classes), we store these {@code AccessorMethods} in a particular order depending on the parameters specialization.
 * This order is implemented in this class through {@link Comparable} interface implementation. Note: this class has a natural ordering that
 * is inconsistent with equals, which means that {@code (x.compareTo(y)==0) == (x.equals(y))} condition is violated.
 * 
 * @author sylvain
 * @see KeyValueProperty
 */
public class AccessorMethod implements Comparable<Object> {

	/** Stores the related {@code KeyValueProperty} */
	protected KeyValueProperty keyValueProperty;

	/** Stores the related {@code Method} */
	protected Method method;

	/**
	 * Creates a new {@code AccessorMethod} instance.
	 * 
	 * @param aKeyValueProperty
	 *            a {@code KeyValueProperty} value
	 * @param aMethod
	 *            a {@code Method} value
	 */
	public AccessorMethod(KeyValueProperty aKeyValueProperty, Method aMethod) {

		super();
		keyValueProperty = aKeyValueProperty;
		method = aMethod;
	}

	/**
	 * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 * 
	 * @param object
	 *            an {@code Object} value
	 * @return an {@code int} value
	 * @exception ClassCastException
	 *                if an error occurs
	 */
	@Override
	public int compareTo(Object object) throws ClassCastException {

		if (object instanceof AccessorMethod) {

			AccessorMethod comparedAccessorMethod = (AccessorMethod) object;

			if (getMethod().getParameterTypes().length != comparedAccessorMethod.getMethod().getParameterTypes().length) {

				// Those objects could not be compared and should be treated as
				// equals
				// regarding the specialization of their parameters
				return 2;
			}
			for (int i = 0; i < getMethod().getParameterTypes().length; i++) {

				Class<?> localParameterType = getMethod().getParameterTypes()[i];
				Class<?> comparedParameterType = comparedAccessorMethod.getMethod().getParameterTypes()[i];

				if (!localParameterType.equals(comparedParameterType)) {

					boolean localParamIsParentOfComparedParam = localParameterType.isAssignableFrom(comparedParameterType);

					boolean localParamIsChildOfComparedParam = comparedParameterType.isAssignableFrom(localParameterType);

					if (localParamIsParentOfComparedParam) {
						return 1;
					}
					if (localParamIsChildOfComparedParam) {
						return -1;
					}
					// Those objects could not be compared
					return 2;
				}

			} // end of for

			// Those objects are equals regarding the specialization of
			// their parameters
			return 0;
		}
		throw new ClassCastException();
	}

	@Override
	public int hashCode() {
		return (toString()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.compareTo(obj) == 0;
	}

	/**
	 * Return the related {@code Method}
	 */
	public Method getMethod() {

		return method;
	}

}
