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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
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

    protected static final Logger logger = Logger.getLogger(XMLRootElementReader.class.getPackage().getName());

    private final LocalHandler    handler;

    private SAXParser             saxParser;

    XMLRootElementReader() {
        super();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);

        handler = new LocalHandler();

        try {
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            saxParser = factory.newSAXParser();

        } catch (Exception e) {
            logger.warning("Cannot create PARSER: " + e.getMessage());
        }

    }

    /**
     * Parses only the first root element of an XML File and returns info in
     * XMLRootElementInfo instance
     * 
     * @param rsc
     * @return
     * @throws IOException
     */
    public XMLRootElementInfo readRootElement(Resource rsc) throws IOException {

        XMLRootElementInfo info = new XMLRootElementInfo();

        InputStream input = rsc.openInputStream();

        if (info != null && input != null) {

            try {
                handler.setInfo(info);
                saxParser.parse(input, handler);
            } catch (stopParsingException e) {
                // Stop the parser after parsing first element
            } catch (SAXException e) {
                logger.warning("Cannot parse document: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
        }
        else {
            logger.warning("Unable to parse root element for document: " + rsc);
        }
        return info;

    }

    public class stopParsingException extends SAXException {
        // an Exception used only to break parsing
    }

    class LocalHandler extends DefaultHandler2 {

        private XMLRootElementInfo _info;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

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

                    Type aType = null;
                    String typeName = attributes.getType(i);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            throw new stopParsingException();
        }

        public void setInfo(XMLRootElementInfo info) {
            _info = info;

        }
    }

}
