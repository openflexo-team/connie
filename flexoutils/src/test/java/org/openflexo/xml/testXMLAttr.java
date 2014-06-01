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

public class testXMLAttr implements IXMLAttribute {

    private String _name  = null;

    private String _value = null;

    testXMLAttr(String name) {
        super();
        _name = name;
    }

    @Override
    public boolean isSimpleAttribute() {
        return true;
    }

    @Override
    public boolean isElement() {
        return false;
    }

    @Override
    public void addValue(IXMLIndividual<?, ?> indiv, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Type getAttributeType() {
        return String.class;
    }

    @Override
    public String getName() {
        return _name;
    }

}
