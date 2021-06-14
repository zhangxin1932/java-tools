package com.zy.tools.serialization.jdk.custom;

import com.zy.tools.serialization.jdk.ObjectInput;
import com.zy.tools.serialization.jdk.ObjectOutput;
import com.zy.tools.serialization.jdk.Serialization;
import com.zy.tools.serialization.jdk.SerializationEnum;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CustomJDKSerialization implements Serialization {
    @Override
    public byte getContentTypeId() {
        return SerializationEnum.CUSTOM_JDK.getContentTypeId();
    }

    @Override
    public String getContentType() {
        return SerializationEnum.CUSTOM_JDK.getContentType();
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException {
        return new CustomJDKObjectInput(is);
    }

    @Override
    public ObjectOutput serialize(OutputStream os) throws IOException {
        return new CustomJDKObjectOutput(os);
    }
}
