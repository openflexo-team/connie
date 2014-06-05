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

import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openflexo.IObjectGraphFactory;

/**
 * An ObjectGraphFactory using a SaxParser to deserialize XMLDocuments into
 * Objects Graph
 * 
 * @author xtof
 * 
 */
public abstract class saxBasedObjectGraphFactory implements IObjectGraphFactory {

    protected static final Logger logger    = Logger.getLogger(saxBasedObjectGraphFactory.class.getPackage().getName());

    protected SAXParserFactory    factory   = null;
    protected SAXParser           saxParser = null;
    protected XMLReaderSAXHandler handler   = null;

    public saxBasedObjectGraphFactory() {
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

}
