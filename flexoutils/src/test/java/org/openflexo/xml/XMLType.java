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

public class XMLType implements Type, IXMLType {

    private static final Object NAME_ATTR = "name";

    private String              _uri      = null;

    Map<String, XMLAttr>    attributeNames;

    XMLType(String uri) {
        super();
        _uri = uri;
        attributeNames = new HashMap<String, XMLAttr>();
    }

    public void createAttribute(String name) {
        if (!hasAttribute(name))
            attributeNames.put(name, new XMLAttr(name));
    }

    public Boolean hasAttribute(String name) {
        return attributeNames.containsKey(name);
    }

    public XMLAttr getAttributeByName(String name) {
        XMLAttr attr = attributeNames.get(name);
        if (attr == null && name.equals(NAME_ATTR)) {
            attr = new XMLAttr(name);
            attributeNames.put(name, attr);
        }
        return attr;
    }

    @Override
    public String getURI() {
        return _uri;
    }

}
