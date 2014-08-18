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

import java.lang.reflect.Type;

public class XMLAttr  {

    private String _name = null;

    XMLAttr(String name) {
        super();
        _name = name;
    }

    public boolean isSimpleAttribute() {
        return true;
    }

    public boolean isElement() {
        return false;
    }
    
    public void addValue(XMLIndiv indiv, Object value) {
        indiv.setAttributeValue(this, value);
    }

    public Type getAttributeType() {
        return String.class;
    }

    public String getName() {
        return _name;
    }

}
