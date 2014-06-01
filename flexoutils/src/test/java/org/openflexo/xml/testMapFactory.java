package org.openflexo.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.openflexo.IFactory;

public class testMapFactory implements IFactory {

    @Override
    public Object newInstance(Type aType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type getTypeFromURI(String uri) {
        return Object.class;
    }

    @Override
    public void setRoot(Object anObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object deserialize(String input) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object deserialize(InputStream input) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNamespace(String uri, String nSPrefix) {
        // TODO Auto-generated method stub

    }

}
