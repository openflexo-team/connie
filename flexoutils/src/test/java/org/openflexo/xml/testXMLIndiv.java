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
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class testXMLIndiv implements IXMLIndividual<testXMLIndiv, testXMLAttr> {

	private String uuid;
	
	public testXMLIndiv() {
		uuid = UUID.randomUUID().toString();
	}

	@Override
	public String getContentDATA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttributeValue(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public testXMLAttr getAttributeByName(String aName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends testXMLAttr> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object createAttribute(String attrLName, Type aType, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttributeStringValue(IXMLAttribute a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChild(IXMLIndividual<testXMLIndiv, testXMLAttr> anIndividual) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<testXMLIndiv> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public testXMLIndiv getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setType(Type myClass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUUID() {
		return uuid;
	}

	@Override
	public Element toXML(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
