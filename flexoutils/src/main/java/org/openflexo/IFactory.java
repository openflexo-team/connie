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

package org.openflexo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * A Factory Interface used to create factories for graph of objects.
 * 
 * @author xtof
 * 
 */

public interface IFactory {

    // ***************************************************
    // Methods concerning the resulting Object graph

    public void setContext(Object objectGraph);

    public void resetContext();

    public void setRoot(Object anObject);

    public void setNamespace(String uri, String nSPrefix);

    /**
     * Retuns the type of Object corresponding to the given URI, it must be a
     * type of object relevant in the context of the current graph to be built
     * 
     * @param uri
     * @return the relevant Type
     */
    public Type getTypeFromURI(String uri);

    // ***************************************************
    // Methods concerning Objects in the graph

    public Object getInstanceOf(Type aType, String name);

    public boolean objectHasAttributeNamed(Object object, Type attrType, String attrName);

    public void addAttributeValueForObject(Object object, String attrName, Object value);

    public void addChildToObject(Object child, Object container);

    // ***************************************************
    // Methods concerning deserialization

    public void deserialize(String input) throws IOException;

    public void deserialize(InputStream input) throws IOException;

}
