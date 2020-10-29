package com.zy.tools.serialization.jdk.raw;

import com.zy.tools.serialization.jdk.api.ObjectInput;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.Objects;

public class RawJDKObjectInput implements ObjectInput {
    @Getter
    private final ObjectInputStream ois;

    public RawJDKObjectInput(InputStream ois) throws IOException {
        this(new ObjectInputStream(ois));
    }

    protected RawJDKObjectInput(ObjectInputStream ois) {
        if (Objects.isNull(ois)) {
            throw new IllegalArgumentException("ois cannot be null.");
        }
        this.ois = ois;
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> tClass) throws IOException, ClassNotFoundException {
        return (T) ois.readObject();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> tClass, Type type) throws IOException, ClassNotFoundException {
        return (T) ois.readObject();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return ois.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return ois.readByte();
    }

    @Override
    public short readShort() throws IOException {
        return ois.readShort();
    }

    @Override
    public int readInt() throws IOException {
        return ois.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return ois.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return ois.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return ois.readDouble();
    }

    @Override
    public String readUTF() throws IOException {
        return ois.readUTF();
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = ois.readInt();
        if (length < 0) {
            return null;
        } else if (length == 0) {
            return new byte[]{};
        } else {
            byte[] bytes = new byte[length];
            ois.readFully(bytes);
            return bytes;
        }
    }
}
