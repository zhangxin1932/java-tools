package com.zy.tools.undefined.serialization.jdk.custom;

import com.zy.tools.undefined.serialization.jdk.raw.RawJDKObjectOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class CustomJDKObjectOutput extends RawJDKObjectOutput {
    public CustomJDKObjectOutput(OutputStream oos) throws IOException {
        super(oos);
    }

    @Override
    public void writeObject(Object object) throws IOException {
        if (Objects.isNull(object)) {
            getOos().writeByte(0);
        } else {
            getOos().writeByte(1);
            getOos().writeObject(object);
        }
    }

    @Override
    public void writeUTF(String v) throws IOException {
        if (Objects.isNull(v)) {
            getOos().writeInt(-1);
        } else {
            getOos().writeInt(v.length());
            getOos().writeUTF(v);
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        getOos().flush();
    }
}
