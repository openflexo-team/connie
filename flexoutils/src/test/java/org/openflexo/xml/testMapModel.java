/*
 * (c) Copyright 2014- Openflexo
 *
 * This file is part of the Openflexo Software Infrastructure.
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

import java.util.HashMap;
import java.util.Map;

public class testMapModel {

    public String               name;

    public Map<Integer, String> aMap;

    testMapModel() {
        aMap = new HashMap<Integer, String>();
    }

    testMapModel(String aName) {
        name = aName;
        aMap = new HashMap<Integer, String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getValues() {
        return aMap;
    }

    public void addNode(Integer key, String value) {
        if (value != null)
            aMap.put(key, value);
    }

}
