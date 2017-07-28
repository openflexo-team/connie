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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openflexo.rm.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * This SaxParser only reads the first root element of an XML File and exits.
 * 
 * @author xtof
 * 
 */

public class XMLRootElementReader {

	protected static final Logger LOGGER = Logger.getLogger(XMLRootElementReader.class.getPackage().getName());

	private final LocalHandler handler;
	private SAXParser saxParser;
	private boolean parseFirstLevelElements = false;
	private String firstLevelElementName;

	public XMLRootElementReader() {
		this(false, null);
	}

	public XMLRootElementReader(boolean parseFirstLevelElements, String firstLevelElementName) {
		super();

		this.parseFirstLevelElements = parseFirstLevelElements;
		this.firstLevelElementName = firstLevelElementName;

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setXIncludeAware(true);

		handler = new LocalHandler();

		try {
			factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			saxParser = factory.newSAXParser();

		} catch (Exception e) {
			LOGGER.warning("Cannot create PARSER: " + e.getMessage());
		}

	}

	/**
	 * Parses only the first root element of an XML File and returns info in XMLRootElementInfo instance
	 * 
	 * @param rsc
	 * @return
	 * @throws IOException
	 */
	synchronized public XMLRootElementInfo readRootElement(Resource rsc) throws IOException {

		InputStream input = rsc.openInputStream();
		return readRootElement(input);
	}

	synchronized public XMLRootElementInfo readRootElement(File xmlFile) throws IOException {
		InputStream input = new FileInputStream(xmlFile);
		return readRootElement(input);
	}

	synchronized public XMLRootElementInfo readRootElement(InputStream input) throws IOException {

		XMLRootElementInfo info = new XMLRootElementInfo(parseFirstLevelElements, firstLevelElementName);

		if (info != null && input != null) {

			try {
				handler.setInfo(info);
				saxParser.parse(input, handler);
				input.close();
			} catch (StopParsingException e) {
				// Stop the parser after parsing first element
			} catch (SAXException e) {
				throw new IOException(e.getMessage());
			}
		}
		else {
			LOGGER.warning("Unable to parse root element for document");
		}
		return info;

	}

	// an Exception used only to break parsing
	private class StopParsingException extends SAXException {
	}

	/**
	 * SAX handler used to only read the first XML tag
	 * 
	 * @author xtof
	 *
	 */
	class LocalHandler extends DefaultHandler2 {

		private XMLRootElementInfo _info;
		private int level = 0;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			if (level == 0) {

				String NSPrefix = "xmlns:p"; // default

				try {
					_info.setName(localName);
					_info.setQName(qName);
					_info.setURI(uri);

					if (qName != null && localName != null) {
						NSPrefix = qName;
						NSPrefix = NSPrefix.replace(localName, "").replace(":", "");
						if (!NSPrefix.isEmpty() && !uri.isEmpty())
							_info.addNamespace(NSPrefix, uri);
					}

					// ************************************
					// processing Attributes

					int len = attributes.getLength();

					for (int i = 0; i < len; i++) {

						// Unused Type aType = null;
						// Unused String typeName = attributes.getType(i);
						String attrQName = attributes.getQName(i);
						String attrName = attributes.getLocalName(i);
						String attrURI = attributes.getURI(i);
						NSPrefix = "xmlns:p"; // default

						if (qName != null && localName != null) {
							NSPrefix = attrQName;
							NSPrefix = NSPrefix.replace(attrName, "").replace(":", "");
						}
						if (!attrURI.isEmpty()) {
							_info.addNamespace(NSPrefix, attrURI);
						}

						if (attrName.equals(""))
							attrName = attrQName;

						if (!attrName.isEmpty()) {
							_info.addAttribute(attrName, attributes.getValue(i));
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (level == 1 && _info.parseFirstLevelElements() && _info.getFirstLevelElementName().equals(localName)) {
				XMLElementInfo el = _info.addElement(uri, localName, qName);

				String NSPrefix = "xmlns:p"; // default

				int len = attributes.getLength();

				for (int i = 0; i < len; i++) {

					// Unused Type aType = null;
					// Unused String typeName = attributes.getType(i);
					String attrQName = attributes.getQName(i);
					String attrName = attributes.getLocalName(i);
					String attrURI = attributes.getURI(i);
					NSPrefix = "xmlns:p"; // default

					if (qName != null && localName != null) {
						NSPrefix = attrQName;
						NSPrefix = NSPrefix.replace(attrName, "").replace(":", "");
					}
					if (!attrURI.isEmpty()) {
						el.addNamespace(NSPrefix, attrURI);
					}

					if (attrName.equals(""))
						attrName = attrQName;

					if (!attrName.isEmpty()) {
						el.addAttribute(attrName, attributes.getValue(i));
					}

				}
			}

			if (!_info.parseFirstLevelElements()) {
				throw new StopParsingException();
			}

			level++;

			// throw new StopParsingException();
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			super.endElement(uri, localName, qName);
			level--;
		}

		public void setInfo(XMLRootElementInfo info) {
			_info = info;

		}
	}

}
