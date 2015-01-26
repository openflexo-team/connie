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

    protected static final Logger LOGGER     = Logger.getLogger(XMLRootElementInfo.class.getPackage().getName());

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
            LOGGER.warning("Several attributes with the same Name?");
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
