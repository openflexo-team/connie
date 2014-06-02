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

public class testXMLModel {

    protected static final Logger     logger        = Logger.getLogger(testXMLModel.class.getPackage().getName());

    private static String             NAME_ATTR_URI = "name";

    private Map<String, testXMLIndiv> listIndiv;

    private Map<String, Type>         listType;

    private testXMLIndiv              root;

    public testXMLModel() {
        listIndiv = new HashMap<String, testXMLIndiv>();
        listType = new HashMap<String, Type>();
        root = null;
    }

    public Object addNewIndividual(Type aType) {
        if (aType instanceof testXMLType) {
            testXMLIndiv indiv = new testXMLIndiv();
            indiv.setType(aType);
            listIndiv.put(indiv.getUUID(), indiv);
            return indiv;
        }
        return null;
    }

    public void setRoot(IXMLIndividual<?, ?> anIndividual) {

        root = (testXMLIndiv) anIndividual;

    }

    public IXMLIndividual<?, ?> getRoot() {
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
            if (uri.equals(NAME_ATTR_URI)) {
                // Name
                listType.put(uri, StringAttribute.class);
                aType = StringAttribute.class;
            }
            else {
                aType = (testXMLType) new testXMLType(uri);
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
