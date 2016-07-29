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
