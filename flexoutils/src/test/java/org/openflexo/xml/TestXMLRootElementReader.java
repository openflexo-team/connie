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

import junit.framework.TestCase;

import org.junit.Test;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class TestXMLRootElementReader extends TestCase {

    protected static XMLRootElementReader reader = new XMLRootElementReader();

    @Test
    public void testLibrary0Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_0.xml");
        assertNotNull(rsc);

        XMLRootElementInfo result = null;

        try {
            result = reader.readRootElement(rsc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(result.getName(), "Library");

    }

    @Test
    public void testLibrary1Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_1.xml");
        assertNotNull(rsc);

        XMLRootElementInfo result = null;

        try {
            result = reader.readRootElement(rsc.openInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(result.getName(), "Library");

    }

    @Test
    public void testLibrary2Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_2.xml");
        assertNotNull(rsc);

        XMLRootElementInfo result = null;

        try {
            result = reader.readRootElement(rsc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(result.getName(), "Library");
    }

    @Test
    public void testLibrary3Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_3.xml");
        assertNotNull(rsc);

        XMLRootElementInfo result = null;

        try {
            result = reader.readRootElement(rsc.openInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
        assertNotNull(result.getNamespaceByPrefix("p"));
        assertEquals(result.getName(), "Library");

    }

    @Test
    public void testMapParsing() {

        Resource rsc = ResourceLocator.locateResource("testXML/MapTest.xml");
        assertNotNull(rsc);
        XMLRootElementInfo result = null;

        try {
        	if (rsc instanceof FileResourceImpl) {
        		result = reader.readRootElement(((FileResourceImpl) rsc).getFile());
        	}
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
        assertNotNull(result.getURI());
        assertEquals(result.getName(), "Map");

    }

    @Test
    public void testVPParsing() {

        Resource rsc = ResourceLocator.locateResource("testXML/SampleUML.xml");
        assertNotNull(rsc);
        XMLRootElementInfo result = null;

        try {
            result = reader.readRootElement(rsc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(result.getName(), "ViewPoint");
        System.out.println(result.toString());

    }
}
