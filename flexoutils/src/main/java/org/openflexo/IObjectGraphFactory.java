/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexoutils, a component of the software infrastructure 
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

package org.openflexo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * A Factory Interface used to create factories for graph of objects.
 * 
 * @author xtof
 * 
 */

public interface IObjectGraphFactory {

	// ***************************************************
	// Methods concerning the resulting Object graph

	/**
	 * Sets the context that contains the graph to be built
	 * 
	 * @param objectGraph
	 */
	public void setContext(Object objectGraph);

	/**
	 * Resets The Context, enables re-use of the same Factory in various contexts
	 */
	public void resetContext();

	public void addToRootNodes(Object anObject);

	public void setContextProperty(String propertyName, Object value);

	/**
	 * Retuns the type of Object corresponding to the given URI, it must be a type of object relevant in the context of the current graph to
	 * be built
	 * 
	 * @param typeURI
	 *            =&gt; URI of the type
	 * @param objectName
	 *            =&gt; the name of the object to be typed
	 * @param container
	 *            =&gt; the object containing the object to be typed
	 * @return the relevant Type
	 */
	public Type getTypeForObject(String typeURI, Object container, String objectName);

	// ***************************************************
	// Methods concerning Objects in the graph

	public Object getInstanceOf(Type aType, String name);

	//
	// public boolean objectHasAttributeNamed(Object object, Type attrType,
	// String attrName);
	public boolean objectHasAttributeNamed(Object object, String attrName);

	public Type getAttributeType(Object currentContainer, String localName);

	public void addAttributeValueForObject(Object object, String attrName, Object value);

	public void addChildToObject(Object child, Object container);

	// ***************************************************
	// Methods concerning deserialization

	public Object deserialize(String input) throws Exception, IOException;

	public Object deserialize(InputStream input) throws Exception, IOException;

}
