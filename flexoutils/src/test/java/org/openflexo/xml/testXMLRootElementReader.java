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

import junit.framework.TestCase;

import org.junit.Test;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class testXMLRootElementReader extends TestCase {

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
            result = reader.readRootElement(rsc);
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
            result = reader.readRootElement(rsc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(result.getName(), "Library");

    }

    @Test
    public void testMapParsing() {

        Resource rsc = ResourceLocator.locateResource("testXML/MapTest.xml");
        assertNotNull(rsc);
        XMLRootElementInfo result = null;

        try {
            result = reader.readRootElement(rsc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(result);
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

    }
}
