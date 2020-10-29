package com.zy.tools.serialization.jdk.raw;

import com.zy.tools.serialization.jdk.api.ObjectOutput;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Objects;

public class RawJDKObjectOutput implements ObjectOutput {
    @Getter
    private final ObjectOutputStream oos;

    public RawJDKObjectOutput(OutputStream oos) throws IOException {
        this(new ObjectOutputStream(oos));
    }

    protected RawJDKObjectOutput(ObjectOutputStream oos) {
        if (Objects.isNull(oos)) {
            throw new IllegalArgumentException("oos cannot be null.");
        }
        this.oos = oos;
    }

    @Override
    public void writeObject(Object object) throws IOException {
        oos.writeObject(object);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        oos.writeBoolean(v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        oos.writeByte(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        oos.writeShort(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        oos.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        oos.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        oos.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        oos.writeDouble(v);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        oos.writeUTF(v);
    }

    @Override
    public void writeBytes(byte[] v) throws IOException {
        if (Objects.isNull(v)) {
            oos.writeInt(-1);
        } else {
            writeBytes(v, 0, v.length);
        }
    }

    @Override
    public void writeBytes(byte[] v, int offset, int length) throws IOException {
        if (Objects.isNull(v)) {
            oos.writeInt(-1);
        } else {
            oos.writeInt(length);
            oos.write(v, offset, length);
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        oos.flush();
    }
}
