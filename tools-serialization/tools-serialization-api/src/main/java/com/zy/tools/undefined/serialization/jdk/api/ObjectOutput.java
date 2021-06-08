package com.zy.tools.undefined.serialization.jdk.api;

import java.io.IOException;

public interface ObjectOutput extends DataOutput {
    void writeObject(Object object) throws IOException;
}
