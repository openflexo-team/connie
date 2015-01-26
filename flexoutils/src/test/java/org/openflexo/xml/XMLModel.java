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
