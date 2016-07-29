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
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;
import org.openflexo.IObjectGraphFactory;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class TestXMLSAXParser extends TestCase {

    private void printXMLNode(XMLIndiv node, String indent) {
        System.out.println(indent + "[N]" + node.getName());

        for (XMLAttr a : node.getAttributes()) {

            System.out.println(indent + "-- " + a.getName() + " = " + node.getAttributeStringValue(a));
        }

        for (XMLIndiv n : node.getChildren()) {
            printXMLNode(n, indent + "    ");
        }

    }

    private void printXMLTree(XMLModel model) {

        XMLIndiv root = (XMLIndiv) model.getRoot();

        printXMLNode(root, "");
    }

    @Test
    public void testLibrary0Parser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_0.xml");
        assertNotNull(rsc);

        IObjectGraphFactory modelFactory = new ModelFactory();
        assertNotNull(modelFactory);

        XMLModel model = new XMLModel();
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

        IObjectGraphFactory modelFactory = new ModelFactory();
        assertNotNull(modelFactory);

        XMLModel model = new XMLModel();
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

        IObjectGraphFactory modelFactory = new ModelFactory();
        assertNotNull(modelFactory);

        XMLModel model = new XMLModel();
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

        IObjectGraphFactory modelFactory = new ModelFactory();
        assertNotNull(modelFactory);

        XMLModel model = new XMLModel();
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

        IObjectGraphFactory mapFactory = new MapFactory();
        assertNotNull(mapFactory);

        MapModel model = new MapModel();
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
