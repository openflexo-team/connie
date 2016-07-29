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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openflexo.IObjectGraphFactory;
import org.xml.sax.SAXException;

public class MapFactory implements IObjectGraphFactory {

    private static final Logger LOGGER    = Logger.getLogger(ModelFactory.class.getPackage().getName());

    private SAXParserFactory    factory   = null;
    private SAXParser           saxParser = null;
    private XMLReaderSAXHandler handler   = null;

    private MapModel        context   = null;

    private NodeBuffer          _node     = null;

    private static String       NODE_TAG  = "Node";
    private static String       MAP_TAG   = "Map";
    private static String       KEY_ATTR  = "key";
    private static String       NAME_ATTR = "name";

    MapFactory() {
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        handler = new XMLReaderSAXHandler(this);
        _node = new NodeBuffer();

        try {
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            saxParser = factory.newSAXParser();

        } catch (Exception e) {
            LOGGER.warning("Cannot create PARSER: " + e.getMessage());
        }
    }

    private class NodeBuffer  {
        public String  value;
        public Integer key;

		public String getURI() {
            return "NodeBuffer";
        }
    }

    @Override
    public Object getInstanceOf(Type aType, String name) {
        if (aType == MapModel.class) {
            return context;
        }
        else if (aType == NodeBuffer.class) {
            return _node;
        }
        return null;
    }

    @Override
    public Type getTypeForObject(String typeURI, Object container, String objectName) {

        if (typeURI.equals(KEY_ATTR)) {
            return Integer.class;
        }
        else if (typeURI.equals(MAP_TAG)) {
            return MapModel.class;
        }
        else if (typeURI.equals(NODE_TAG)) {
            return NodeBuffer.class;
        }
        else
            return String.class;
    }

    @Override
    public void addToRootNodes(Object anObject) {
        // irrelevant in this context
        return;
    }

    @Override
    public Object deserialize(String input) throws IOException {
        if (context != null) {
            try {
                saxParser.parse(input, handler);
            } catch (SAXException e) {
                LOGGER.warning("Cannot parse document: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return context;

        }
        else {
            LOGGER.warning("Context is not set for parsing, aborting");
        }
        return null;
    }

    @Override
    public Object deserialize(InputStream input) throws IOException {
        if (context != null) {
            try {
                saxParser.parse(input, handler);
            } catch (SAXException e) {
                LOGGER.warning("Cannot parse document: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return context;

        }
        else {
            LOGGER.warning("Context is not set for parsing, aborting");
        }
        return null;
    }

    @Override
    public void setContextProperty(String propertyName, Object value) {
        // Irrelevant for this test
    }

    @Override
    public void setContext(Object objectGraph) {
        context = (MapModel) objectGraph;
    }

    @Override
    public void resetContext() {
        context = null;
    }

    @Override
    public void addAttributeValueForObject(Object object, String attrName, Object value) {
        if (object instanceof NodeBuffer) {
            if (attrName.equals(XMLCst.CDATA_ATTR_NAME)) {
                _node.value = (String) value;

            }
            else if (attrName.equals(KEY_ATTR)) {
                _node.key = Integer.decode((String) value);
            }
        }
        else {
            if (attrName.equals(NAME_ATTR)) {
                context.setName((String) value);
            }
            else if (attrName.equals(NODE_TAG)) {
                context.addNode(((NodeBuffer) value).key, ((NodeBuffer) value).value);
            }
        }

    }

    @Override
    public boolean objectHasAttributeNamed(Object object, String localName) {
        if (object instanceof MapModel) {
            return localName.equals(NODE_TAG);
        }
        return false;
    }

    @Override
    public void addChildToObject(Object currentObject, Object currentContainer) {
        LOGGER.warning("This method is irrelevant in this context");
        return;

    }

    @Override
    public Type getAttributeType(Object currentContainer, String localName) {
        if (localName.equals(NODE_TAG)) {
            return MapModel.class;
        }
        else
            return String.class;
    }
}
