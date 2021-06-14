package com.zy.tools.serialization.jdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serialization {
    byte getContentTypeId();
    String getContentType();
    ObjectInput deserialize(InputStream is) throws IOException;
    ObjectOutput serialize(OutputStream os) throws IOException;
}
