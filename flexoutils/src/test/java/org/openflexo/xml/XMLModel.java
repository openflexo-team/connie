/*
 * (c) Copyright 2013-2014 Openflexo
 *
 * This file is part of Openflexo Software Infrastructure .
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class XMLModel {

    protected static final Logger     LOGGER        = Logger.getLogger(XMLModel.class.getPackage().getName());

    private static String             NAME_ATTR     = "name";
    private static String             NAME_ATTR_URI = "http://www.example.org/Library#name";

    private final Map<String, XMLIndiv> listIndiv;

    private final Map<String, Type>         listType;

    private XMLIndiv              root;

    public XMLModel() {
        listIndiv = new HashMap<String, XMLIndiv>();
        listType = new HashMap<String, Type>();
        root = null;
    }

    public Object addNewIndividual(Type aType) {
        if (aType instanceof XMLType) {
            XMLIndiv indiv = new XMLIndiv();
            indiv.setType(aType);
            listIndiv.put(indiv.getUUID(), indiv);
            return indiv;
        }
        return null;
    }

    public void setRoot(XMLIndiv anIndividual) {

        root = anIndividual;

    }

    public XMLIndiv getRoot() {
        return root;
    }

    public void setNamespace(String uri, String prefix) {
        // TODO Auto-generated method stub

    }

    public String getNamespacePrefix() {
        return "ATEST";
    }

    public String getNamespaceURI() {
        return "http://a.test.org/";
    }

    public Type getTypeFromURI(String uri) {
        Type aType = listType.get(uri);
        if (aType == null) {
            if (uri.equals(NAME_ATTR_URI) || uri.equals(NAME_ATTR)) {
                // Name
                listType.put(uri, StringAttribute.class);
                aType = StringAttribute.class;
            }
            else {
                aType = new XMLType(uri);
                listType.put(uri, aType);
            }
        }
        return aType;
    }

    // Inner class to store a String Value
    public class StringAttribute {
        private String value = null;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
