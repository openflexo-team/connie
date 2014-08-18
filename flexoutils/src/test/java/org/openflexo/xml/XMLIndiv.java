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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLIndiv  {

    private final String                   uuid;

    private static String            NAME_KEY        = "___The_NAME___";

    private Map<XMLAttr, Object> attributeValues = null;
    private List<XMLIndiv>       children        = null;
    private XMLIndiv             parent          = null;

    private XMLType              type            = null;

    private String                   _name           = null;

    public XMLIndiv() {
        uuid = UUID.randomUUID().toString();
        attributeValues = new HashMap<XMLAttr, Object>();
        children = new ArrayList<XMLIndiv>();
    }

    public String getContentDATA() {
        // TODO Auto-generated method stub
        return null;
    }


    public void setName(String name) {
        _name = name;
    }

    public String getFullyQualifiedName() {
        return _name;
    }

    public String getName() {
        return _name;
    }

    public Object getAttributeValue(String attributeName) {
        XMLAttr attr = ((XMLType) getType()).getAttributeByName(attributeName);
        return attributeValues.get(attr);
    }

    public XMLAttr getAttributeByName(String aName) {

        return ((XMLType) getType()).getAttributeByName(aName);
    }

    public Collection<? extends XMLAttr> getAttributes() {
        return attributeValues.keySet();
    }

    public Object createAttribute(String attrLName, Type aType, String value) {
        XMLAttr attr = new XMLAttr(attrLName);
        attr.addValue(this, value);
        return attr;
    }

    public String getAttributeStringValue(XMLAttr a) {
        return attributeValues.get(a).toString();
    }

    public void addChild(XMLIndiv anIndividual) {
        children.add(anIndividual);
        anIndividual.setParent(this);
    }

    public List<XMLIndiv> getChildren() {
        return children;
    }

    public void setParent(XMLIndiv container) {
        parent = container;
    }

    public XMLIndiv getParent() {
        return parent;
    }

    public Type getType() {
        return type;
    }
   public void setType(Type myClass) {
        type = (XMLType) myClass;
    }

    public String getUUID() {
        return uuid;
    }

    public Element toXML(Document doc) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setAttributeValue(XMLAttr attr, Object value) {
        if (value instanceof XMLModel.StringAttribute) {
            this.attributeValues.put(attr, ((XMLModel.StringAttribute) value).getValue());
        }
        else {
            this.attributeValues.put(attr, value);
        }
    }
}
