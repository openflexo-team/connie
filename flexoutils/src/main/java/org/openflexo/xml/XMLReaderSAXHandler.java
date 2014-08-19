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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import org.openflexo.IObjectGraphFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * This SaxHandler is used to de-serialize any XML file
 * 
 * @author xtof
 * 
 */

public class XMLReaderSAXHandler extends DefaultHandler2 {

	protected static final Logger logger             = Logger.getLogger(XMLReaderSAXHandler.class.getPackage().getName());

	public static final String    NAMESPACE_Property = "Namespace";

	private Object                currentContainer   = null;
	private Object                currentObject      = null;
	private Type                  currentObjectType        = null;

	private final StringBuffer    cdataBuffer        = new StringBuffer();

	private final Stack<Object>   indivStack         = new Stack<Object>();

	private IObjectGraphFactory   factory            = null;


	public XMLReaderSAXHandler(IObjectGraphFactory aFactory) {
		super();
		factory = aFactory;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		String NSPrefix = "p"; // default

		currentObject = null;

		// ************************************
		// Current element is not contained => root node, set NameSpace
		if (currentContainer == null) {
			if (uri != null && !uri.isEmpty()) {
				List<String> namespace = new ArrayList<String>();
				namespace.add(uri);
				namespace.add(NSPrefix);
				factory.setContextProperty(NAMESPACE_Property, namespace);
			}

		}
		if (indivStack.isEmpty()) {
			currentContainer = null;
		}

		try {

			if (uri.length() == 0) {
				// If there is no base uri, we use the localName of the XML Tag
				currentObjectType = factory.getTypeForObject(localName, currentContainer, localName);
			}			
			else {
				if (currentContainer != null) {
					// find if there is an object property corresponding
					if (factory.objectHasAttributeNamed(currentContainer, localName)) {
						currentObjectType = factory.getAttributeType(currentContainer, localName);
					}
					else {
						currentObjectType = factory.getTypeForObject(uri + "#" + localName, currentContainer, localName);

					}
				}
				else {
					currentObjectType = factory.getTypeForObject(uri + "#" + localName, null, localName);
				}
			}


			// creates individual if it is a complex Type
			if (currentObjectType != null) {

				currentObject = factory.getInstanceOf(currentObjectType, localName);

				cdataBuffer.delete(0, cdataBuffer.length());
			}

			if (currentObject != null) {
				// ************************************
				// processing Attributes

				int len = attributes.getLength();

				for (int i = 0; i < len; i++) {

					Type aType = null;
					String typeName = attributes.getType(i);
					String attrQName = attributes.getQName(i);
					String attrName = attributes.getLocalName(i);
					String attrURI = attributes.getURI(i);
					NSPrefix = "p"; // default

					if (attrQName != null && attrName != null && currentContainer == null) {
						// we only set prefix if there is no other Root Element
						NSPrefix = attrQName;
						NSPrefix.replace(attrName, "").replace(":", "");
					}

					if (typeName.equals(XMLCst.CDATA_TYPE_NAME)) {
						aType = String.class;
						if (attrName.equals(""))
							attrName = attrQName;

					}

					factory.addAttributeValueForObject(currentObject, attrName, attributes.getValue(i));

				}

				// ************************************
				// Current element is not contained in another one, it is root!
				if (currentContainer == null && currentObject != null) {

					factory.addToRootNodes(currentObject);
				}

				if (currentObject != null) {
					indivStack.push(currentObject);
				}
				currentContainer = currentObject;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public void endElement(String uri, String localName, String qName) throws SAXException {


		boolean isAttribute = false;

		if (currentContainer != null) {
			isAttribute = factory.objectHasAttributeNamed(currentContainer, localName);
		}

		// CDATA allocation

		String str = cdataBuffer.toString().trim();

		// Element is a simple attribute of current container => only allocate
		// String
		if (isAttribute && currentObject == null) {
			currentObject = currentContainer;
			if (str.length() > 0) {
				factory.addAttributeValueForObject(currentObject, localName, str);
				cdataBuffer.delete(0, cdataBuffer.length());
			}
		}
		else {
			if (!indivStack.isEmpty()) {
				currentObject = indivStack.pop();
			}

			// node stack management

			if (!indivStack.isEmpty()) {
				currentContainer = indivStack.lastElement();
			}
			else {
				currentContainer = null;
			}

			isAttribute = factory.objectHasAttributeNamed(currentContainer, localName);

			// Allocation of CDATA information depends on the type of entity we
			// have to allocate content to (Individual or Attribute)
			// As such, it depends on the interpretation that has been done of
			// XSD MetaModel
			//
			// Same stands for individuals to be allocated to ObjectProperties

			if (str.length() > 0) {
				factory.addAttributeValueForObject(currentObject, XMLCst.CDATA_ATTR_NAME, str);
				cdataBuffer.delete(0, cdataBuffer.length());
			}

			// ************************************
			// Current element is contained in another one

			if (currentContainer != null && currentContainer != currentObject) {

				if (isAttribute) {
					factory.addAttributeValueForObject(currentContainer, localName, currentObject);

				}
				else {
					factory.addChildToObject(currentObject, currentContainer);
				}
			}
		}

		currentObject = currentContainer;

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		if (length > 0)
			cdataBuffer.append(ch, start, length);
	}
}
