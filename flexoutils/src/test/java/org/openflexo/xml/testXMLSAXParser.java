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
import org.openflexo.IFactory;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class testXMLSAXParser extends TestCase {

    @Test
    public void testLibraryParser() {

        Resource rsc = ResourceLocator.locateResource("testXML/example_library_0.xml");
        assertNotNull(rsc);

        IFactory modelFactory = new testModelFactory();

        assertNotNull(modelFactory);

        try {
            testXMLModel model = (testXMLModel) modelFactory.deserialize(rsc.openInputStream());
            assertNotNull(model);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testMapParsing() {

        Resource rsc = ResourceLocator.locateResource("testXML/MapTest.xml");
        assertNotNull(rsc);

        IFactory mapFactory = new testMapFactory();

        assertNotNull(mapFactory);

        try {
            testMapModel model = (testMapModel) mapFactory.deserialize(rsc.openInputStream());
            assertNotNull(model);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
