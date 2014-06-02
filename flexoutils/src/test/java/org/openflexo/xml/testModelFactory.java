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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openflexo.IFactory;
import org.xml.sax.SAXException;

public class testModelFactory implements IFactory {

    protected static final Logger logger    = Logger.getLogger(testModelFactory.class.getPackage().getName());

    protected SAXParserFactory    factory   = null;
    protected SAXParser           saxParser = null;
    protected XMLReaderSAXHandler handler   = null;

    private testXMLModel          model     = null;

    testModelFactory() {
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        handler = new XMLReaderSAXHandler(this);

        try {
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            saxParser = factory.newSAXParser();

        } catch (Exception e) {
            logger.warning("Cannot create PARSER: " + e.getMessage());
        }
    }

    @Override
    public Object getInstanceOf(Type aType) {
        if (aType instanceof testXMLType) {
            testXMLIndiv _inst = (testXMLIndiv) model.addNewIndividual(aType);
            // _inst.setName();
            return (Object) _inst;
        }
        else {
            // Fail Back to String
            // Beware of memory Leaks!!!
            return new String();
        }
    }

    @Override
    public Type getTypeFromURI(String uri) {
        return model.getTypeFromURI(uri);
    }

    @Override
    public void deserialize(String input) throws IOException {
        if (model != null) {

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
        if (model != null) {

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
    public void setRoot(Object anObject) {
        model.setRoot((testXMLIndiv) anObject);
    }

    @Override
    public void setNamespace(String uri, String nSPrefix) {
        model.setNamespace(uri, nSPrefix);

    }

    @Override
    public void setContext(Object objectGraph) {
        model = (testXMLModel) objectGraph;

    }

    @Override
    public void resetContext() {
        model = null;
    }

    @Override
    public boolean objectHasAttributeNamed(Object object, Type aType, String attrName) {
        if (object instanceof testXMLIndiv) {

            testXMLAttr attr = ((testXMLIndiv) object).getAttributeByName(attrName);

            return (attr != null && attr.getAttributeType() == aType);
        }
        return false;
    }

    @Override
    public void setAttributeValueForObject(Object object, String attrName, Object value) {
        if (object instanceof testXMLIndiv) {
            testXMLAttr attr = ((testXMLIndiv) object).getAttributeByName(attrName);

            if (attr == null) {
                attr = (testXMLAttr) ((testXMLIndiv) object).createAttribute(attrName, String.class, (String) value);
            }
            else {

                attr.addValue(((testXMLIndiv) object), value);

            }
        }
        else if (object instanceof String) {
            ((String) object).concat((String) value);
        }
    }

    @Override
    public void addChildToObject(Object currentObject, Object currentContainer) {
        if (currentContainer instanceof testXMLIndiv) {
            ((testXMLIndiv) currentContainer).addChild((IXMLIndividual<testXMLIndiv, testXMLAttr>) currentObject);
        }

    }
}
