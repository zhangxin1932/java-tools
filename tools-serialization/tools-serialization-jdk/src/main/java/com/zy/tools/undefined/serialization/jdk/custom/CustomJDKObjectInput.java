package com.zy.tools.undefined.serialization.jdk.custom;

import com.zy.tools.undefined.serialization.jdk.raw.RawJDKObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class CustomJDKObjectInput extends RawJDKObjectInput {
    public final static int MAX_BYTE_ARRAY_LENGTH = 8 * 1024 * 1024;

    public CustomJDKObjectInput(InputStream ois) throws IOException {
        super(ois);
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        byte b = getOis().readByte();
        if (b == 0) {
            return null;
        }
        return getOis().readObject();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> tClass) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> tClass, Type type) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }

    @Override
    public String readUTF() throws IOException {
        int length = getOis().readInt();
        if (length < 0) {
            return null;
        }
        return getOis().readUTF();
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = getOis().readInt();
        if (length < 0) {
            return null;
        } else if (length == 0) {
            return new byte[0];
        } else if (length > MAX_BYTE_ARRAY_LENGTH) {
            throw new IOException("bytes is too large." + length);
        } else {
            byte[] bytes = new byte[length];
            getOis().readFully(bytes);
            return bytes;
        }
    }
}
