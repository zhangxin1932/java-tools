package com.zy.tools.undefined.serialization.jdk.custom;

import com.zy.tools.undefined.serialization.jdk.api.ObjectInput;
import com.zy.tools.undefined.serialization.jdk.api.ObjectOutput;
import com.zy.tools.undefined.serialization.jdk.api.Serialization;
import com.zy.tools.undefined.serialization.jdk.api.SerializationEnum;

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