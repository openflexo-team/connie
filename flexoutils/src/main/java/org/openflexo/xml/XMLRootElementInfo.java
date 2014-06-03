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
 * MERCHANTABILITY or FITNESS FOR A PARTObjectULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Data structure used to hold information parsed for the root element of a XML
 * Document
 * 
 * @author xtof
 * 
 */
public class XMLRootElementInfo {

    protected static final Logger logger     = Logger.getLogger(XMLRootElementInfo.class.getPackage().getName());

    protected SAXParserFactory    factory    = null;
    protected SAXParser           saxParser  = null;
    protected XMLReaderSAXHandler handler    = null;

    private Map<String, String>   namespaces = null;

    private Map<String, String>   attributes = null;

    private String                name;

    private String                URI;

    private String                qName;

    XMLRootElementInfo() {
        namespaces = new HashMap<String, String>();
        attributes = new HashMap<String, String>();
    }

    public void addNamespace(String prefix, String uri) {
        if (namespaces.get(prefix) == null) {
            namespaces.put(prefix, uri);
        }
    }

    public String getNamespaceByPrefix(String prefix) {
        return namespaces.get(prefix);
    }

    public void addAttribute(String name, String value) {
        if (attributes.get(name) == null) {
            attributes.put(name, value);
        }
        else {
            logger.warning("Several attributes with the same Name?");
        }
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String uRI) {
        URI = uRI;
    }

    public void setQName(String name) {
        qName = name;

    }

    public String getQName() {
        return qName;

    }

    public String toString() {
        return new String("[" + name + "]" + "/" + namespaces + "/" + attributes);
    }
}
