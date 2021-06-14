package com.zy.tools.serialization.jdk.raw;

import com.zy.tools.serialization.jdk.ObjectInput;
import com.zy.tools.serialization.jdk.ObjectOutput;
import com.zy.tools.serialization.jdk.Serialization;
import com.zy.tools.serialization.jdk.SerializationEnum;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RawJDKSerialization implements Serialization {
    @Override
    public byte getContentTypeId() {
        return SerializationEnum.RAW_JDK.getContentTypeId();
    }

    @Override
    public String getContentType() {
        return SerializationEnum.RAW_JDK.getContentType();
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException {
        return new RawJDKObjectInput(is);
    }

    @Override
    public ObjectOutput serialize(OutputStream os) throws IOException {
        return new RawJDKObjectOutput(os);
    }
}
