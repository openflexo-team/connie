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

public class testXMLIndiv implements IXMLIndividual<testXMLIndiv, testXMLAttr> {

    private String                   uuid;

    private static String            NAME_KEY        = "___The_NAME___";

    private Map<testXMLAttr, Object> attributeValues = null;
    private List<testXMLIndiv>       children        = null;
    private testXMLIndiv             parent          = null;

    private testXMLType              type            = null;

    private String                   _name           = null;

    public testXMLIndiv() {
        uuid = UUID.randomUUID().toString();
        attributeValues = new HashMap<testXMLAttr, Object>();
        children = new ArrayList<testXMLIndiv>();
    }

    @Override
    public String getContentDATA() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setName(String name) {
        _name = name;
    }

    @Override
    public String getFullyQualifiedName() {
        return _name;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Object getAttributeValue(String attributeName) {
        testXMLAttr attr = ((testXMLType) getType()).getAttributeByName(attributeName);
        return attributeValues.get(attr);
    }

    @Override
    public testXMLAttr getAttributeByName(String aName) {

        return ((testXMLType) getType()).getAttributeByName(aName);
    }

    @Override
    public Collection<? extends testXMLAttr> getAttributes() {
        return attributeValues.keySet();
    }

    @Override
    public Object createAttribute(String attrLName, Type aType, String value) {
        testXMLAttr attr = new testXMLAttr(attrLName);
        attr.addValue(this, value);
        return attr;
    }

    @Override
    public String getAttributeStringValue(IXMLAttribute a) {
        return attributeValues.get(a).toString();
    }

    @Override
    public void addChild(IXMLIndividual<testXMLIndiv, testXMLAttr> anIndividual) {
        children.add((testXMLIndiv) anIndividual);
        ((testXMLIndiv) anIndividual).setParent(this);
    }

    @Override
    public List<testXMLIndiv> getChildren() {
        return children;
    }

    public void setParent(testXMLIndiv container) {
        parent = container;
    }

    @Override
    public testXMLIndiv getParent() {
        return parent;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type myClass) {
        type = (testXMLType) myClass;
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

    public void setAttributeValue(testXMLAttr attr, Object value) {
        if (value instanceof testXMLModel.StringAttribute) {
            this.attributeValues.put(attr, ((testXMLModel.StringAttribute) value).getValue());
        }
        else {
            this.attributeValues.put(attr, value);
        }
    }
}
