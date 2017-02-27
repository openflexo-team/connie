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

package org.openflexo.connie.type;

import java.lang.reflect.Type;

/**
 * API for a custom type
 * 
 * @author sylvain
 * 
 */
public interface CustomType extends Type {

	/**
	 * Determines if the class or interface represented by this {@code CustomType} object is either the same as, or is a superclass or
	 * superinterface of, the class or interface represented by the specified {@code anOtherType} parameter. It returns {@code true} if so;
	 * otherwise false<br>
	 * This method also tried to resolve generics before to perform the assignability test
	 * 
	 * @param aType
	 * @param permissive
	 *            is a flag indicating if basic conversion between primitive types is allowed: for example, an int may be assign to a float
	 *            value after required conversion.
	 * @return
	 */
	public boolean isTypeAssignableFrom(Type aType, boolean permissive);

	/**
	 * Determine if supplied object is of type beeing represented by this type
	 * 
	 * @param aType
	 * @param permissive
	 * @return
	 */
	public boolean isOfType(Object object, boolean permissive);

	/**
	 * Return simple (human understandable) representation for this type
	 * 
	 * @return
	 */
	public String simpleRepresentation();

	/**
	 * Return full qualified representation (machine understandable) representation for this type
	 * 
	 * @return
	 */
	public String fullQualifiedRepresentation();

	/**
	 * Return base java class
	 * 
	 * @return
	 */
	public Class<?> getBaseClass();

	/**
	 * Return a String encoding configuration of this type<br>
	 * This String might be used to serialize configuration of this type (excluding type name)
	 * 
	 * @return
	 */
	public String getSerializationRepresentation();

	/**
	 * Return boolean indicating if this type has been fully resolved
	 * 
	 * @return
	 */
	public boolean isResolved();

	/**
	 * Called when a {@link CustomType} is not fully resolved<br>
	 * The factory must be supplied and should reference required context
	 * 
	 * @param factory
	 */
	public void resolve(CustomTypeFactory<?> factory);
}
