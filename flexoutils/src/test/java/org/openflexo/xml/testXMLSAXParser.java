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

import junit.framework.TestCase;

import org.junit.Test;
import org.openflexo.IObjectGraphFactory;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class testXMLSAXParser extends TestCase {

    private void printXMLNode(testXMLIndiv node, String indent) {
        System.out.println(indent + "[N]" + node.getName());

        for (testXMLAttr a : node.getAttributes()) {

            System.out.println(indent + "-- " + a.getName() + " = " + node.getAttributeStringValue(a));
        }

        for (testXMLIndiv n : node.getChildren()) {
            printXMLNode(n, indent + "    ");
        }

    }

    private void printXMLTree(testXMLModel model) {

        testXMLIndiv root = (testXMLIndiv) model.getRoot();

        printXMLNode(root, "");
    }

    @Test
    public void testLibrary0Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_0.xml");
        assertNotNull(rsc);

        IObjectGraphFactory modelFactory = new testModelFactory();
        assertNotNull(modelFactory);

        testXMLModel model = new testXMLModel();
        assertNotNull(model);

        modelFactory.setContext(model);

        try {
            InputStream in = rsc.openInputStream();
            modelFactory.deserialize(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testLibrary1Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_1.xml");
        assertNotNull(rsc);

        IObjectGraphFactory modelFactory = new testModelFactory();
        assertNotNull(modelFactory);

        testXMLModel model = new testXMLModel();
        assertNotNull(model);

        modelFactory.setContext(model);

        try {
            InputStream in = rsc.openInputStream();
            modelFactory.deserialize(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLibrary2Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_2.xml");
        assertNotNull(rsc);

        IObjectGraphFactory modelFactory = new testModelFactory();
        assertNotNull(modelFactory);

        testXMLModel model = new testXMLModel();
        assertNotNull(model);

        modelFactory.setContext(model);

        try {
            InputStream in = rsc.openInputStream();
            modelFactory.deserialize(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testLibrary3Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_3.xml");
        assertNotNull(rsc);

        IObjectGraphFactory modelFactory = new testModelFactory();
        assertNotNull(modelFactory);

        testXMLModel model = new testXMLModel();
        assertNotNull(model);

        modelFactory.setContext(model);

        try {
            InputStream in = rsc.openInputStream();
            modelFactory.deserialize(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        printXMLTree(model);
    }

    @Test
    public void testMapParsing() {

        Resource rsc = ResourceLocator.locateResource("testXML/MapTest.xml");
        assertNotNull(rsc);

        IObjectGraphFactory mapFactory = new testMapFactory();
        assertNotNull(mapFactory);

        testMapModel model = new testMapModel();
        assertNotNull(model);

        mapFactory.setContext(model);

        try {
            InputStream in = rsc.openInputStream();
            mapFactory.deserialize(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(model.getValues().size(), 4);
        assertEquals(model.getName(), "lapin");

        // System.out.println(Runtime.getRuntime().totalMemory() -
        // Runtime.getRuntime().freeMemory());

        System.out.println("MAP: " + model.getValues());

        mapFactory.resetContext();

    }
}
