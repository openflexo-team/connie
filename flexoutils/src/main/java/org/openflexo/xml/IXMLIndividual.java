/*
 * (c) Copyright 2013-2014 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.xml;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * This interface defines additional methods to be defined by all XMLIndividual that 
 * will be manipulated by the XMLSaxHandler
 *
 * TODO: explain why it has to have a IXMLAttribute in generic type...
 *
 * @author xtof
 *
 */

public interface IXMLIndividual<IC, AC extends IXMLAttribute > {
	
	public abstract String getContentDATA();

	public abstract void setName(String name);

	public abstract String getFullyQualifiedName();

	public abstract String getName();

	public abstract Object getAttributeValue(String attributeName);

	public abstract AC getAttributeByName(String aName);

	public abstract Collection<? extends AC> getAttributes();
	
	public abstract Object createAttribute(String attrLName, Type aType, String value);

	public abstract String getAttributeStringValue(IXMLAttribute a);
		
	public abstract void addChild(IXMLIndividual<IC,AC> anIndividual);

	public abstract Set<IC> getChildren();

	public abstract IC getParent();

	public abstract Type getType();

	public abstract void setType(Type myClass);

	public abstract String getUUID();

	public abstract Element toXML(Document doc);


}
