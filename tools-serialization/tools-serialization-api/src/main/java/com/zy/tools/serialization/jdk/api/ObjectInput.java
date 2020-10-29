package com.zy.tools.serialization.jdk.api;

import java.io.IOException;
import java.lang.reflect.Type;

public interface ObjectInput extends DataInput {
    Object readObject() throws IOException, ClassNotFoundException;

    <T> T readObject(Class<T> tClass) throws IOException, ClassNotFoundException;

    <T> T readObject(Class<T> tClass, Type type) throws IOException, ClassNotFoundException;
}
