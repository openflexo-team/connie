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
import java.util.List;

import org.xml.sax.SAXException;

public class ModelFactory extends saxBasedObjectGraphFactory {

    private XMLModel                 model            = null;

    private XMLModel.StringAttribute attrStringBuffer = null;

    @Override
    public Object getInstanceOf(Type aType, String name) {
        if (aType instanceof XMLType) {
            XMLIndiv _inst = (XMLIndiv) model.addNewIndividual(aType);
            _inst.setName(name);
            return _inst;
        }
        else if (aType == XMLModel.StringAttribute.class) {
            if (attrStringBuffer == null)
                attrStringBuffer = model.new StringAttribute();
            return attrStringBuffer;
        }
        return null;
    }

    @Override
    public Type getTypeForObject(String typeURI, Object container, String objectName) {
        return model.getTypeFromURI(typeURI);
    }

    @Override
    public Object deserialize(String input) throws IOException {
        if (model != null) {

            try {
                saxParser.parse(input, handler);
            } catch (SAXException e) {
                logger.warning("Cannot parse document: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return this.model;

        }
        else {
            logger.warning("Context is not set for parsing, aborting");
        }
        return null;
    }

    @Override
    public Object deserialize(InputStream input) throws IOException {
        if (model != null) {

            try {
                saxParser.parse(input, handler);
            } catch (SAXException e) {
                logger.warning("Cannot parse document: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return this.model;

        }
        else {
            logger.warning("Context is not set for parsing, aborting");
        }
        return null;
    }

    @Override
    public void addToRootNodes(Object anObject) {
        model.setRoot((XMLIndiv) anObject);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setContextProperty(String propertyName, Object value) {
        if (propertyName.equals(XMLReaderSAXHandler.NAMESPACE_Property)) {
            model.setNamespace(((List<String>) value).get(0), ((List<String>) value).get(1));
        }

    }

    @Override
    public void setContext(Object objectGraph) {
        model = (XMLModel) objectGraph;

    }

    @Override
    public void resetContext() {
        model = null;
    }

    @Override
    public boolean objectHasAttributeNamed(Object object, String attrName) {
        if (object instanceof XMLIndiv) {

            XMLAttr attr = ((XMLIndiv) object).getAttributeByName(attrName);
            /*
                        if (aType == testXMLModel.StringAttribute.class) {
                            return true;
                        }
                        else {*/
            return (attr != null);
            // }
        }
        return false;
    }

    @Override
    public void addAttributeValueForObject(Object object, String attrName, Object value) {

        if (object instanceof XMLIndiv) {
            XMLAttr attr = ((XMLIndiv) object).getAttributeByName(attrName);

            if (attr == null) {
                attr = (XMLAttr) ((XMLIndiv) object).createAttribute(attrName, String.class, (String) value);
            }
            else {

                attr.addValue(((XMLIndiv) object), value);

            }
        }
        else if (object instanceof XMLModel.StringAttribute) {
            ((XMLModel.StringAttribute) object).setValue((String) value);
        }
    }

    @Override
    public void addChildToObject(Object currentObject, Object currentContainer) {
        if (currentContainer instanceof XMLIndiv) {
            ((XMLIndiv) currentContainer).addChild((IXMLIndividual<XMLIndiv, XMLAttr>) currentObject);
        }

    }

    @Override
    public Type getAttributeType(Object currentContainer, String localName) {
        return XMLModel.StringAttribute.class;
    }
}
