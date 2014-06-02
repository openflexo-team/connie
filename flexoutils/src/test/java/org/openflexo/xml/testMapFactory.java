/*
 * (c) Copyright 2014- Openflexo
 *
 * This file is part of the Openflexo Software Infrastructure.
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openflexo.IFactory;
import org.xml.sax.SAXException;

public class testMapFactory implements IFactory {

    private static final Logger logger    = Logger.getLogger(testModelFactory.class.getPackage().getName());

    private SAXParserFactory    factory   = null;
    private SAXParser           saxParser = null;
    private XMLReaderSAXHandler handler   = null;

    private testMapModel        context   = null;

    private NodeBuffer          _node     = null;

    private static String       NODE_TAG  = "Node";
    private static String       MAP_TAG   = "Map";
    private static String       KEY_ATTR  = "key";
    private static String       NAME_ATTR = "name";

    testMapFactory() {
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        handler = new XMLReaderSAXHandler(this);
        _node = new NodeBuffer();

        try {
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            saxParser = factory.newSAXParser();

        } catch (Exception e) {
            logger.warning("Cannot create PARSER: " + e.getMessage());
        }
    }

    private class NodeBuffer {
        public String  value;
        public Integer key;
    }

    @Override
    public Object getInstanceOf(Type aType, String name) {
        if (aType == testMapModel.class) {
            return context;
        }
        else if (aType == NodeBuffer.class) {
            return _node;
        }
        return null;
    }

    @Override
    public Type getTypeFromURI(String uri) {

        if (uri.equals(KEY_ATTR)) {
            return Integer.class;
        }
        else if (uri.equals(MAP_TAG)) {
            return testMapModel.class;
        }
        else if (uri.equals(NODE_TAG)) {
            return NodeBuffer.class;
        }
        else
            return String.class;
    }

    @Override
    public void setRoot(Object anObject) {
        // irrelevant in this context
        return;
    }

    @Override
    public void deserialize(String input) throws IOException {
        if (context != null) {
            try {
                saxParser.parse(input, handler);
            } catch (SAXException e) {
                logger.warning("Cannot parse document: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return;

        }
        else {
            logger.warning("Context is not set for parsing, aborting");
        }
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        if (context != null) {
            try {
                saxParser.parse(input, handler);
            } catch (SAXException e) {
                logger.warning("Cannot parse document: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return;

        }
        else {
            logger.warning("Context is not set for parsing, aborting");
        }
    }

    @Override
    public void setNamespace(String uri, String nSPrefix) {
        // Irrelevant for this test
    }

    @Override
    public void setContext(Object objectGraph) {
        context = (testMapModel) objectGraph;
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
    public boolean objectHasAttributeNamed(Object object, Type currentType, String localName) {
        if (object instanceof testMapModel) {
            return localName.equals(NODE_TAG);
        }
        return false;
    }

    @Override
    public void addChildToObject(Object currentObject, Object currentContainer) {
        logger.warning("This method is irrelevant in this context");
        return;

    }
}
