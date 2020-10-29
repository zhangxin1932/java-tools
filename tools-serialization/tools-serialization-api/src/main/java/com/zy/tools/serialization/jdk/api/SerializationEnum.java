package com.zy.tools.serialization.jdk.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializationEnum {

    RAW_JDK((byte) 2, "x-application/rawjdk"),
    CUSTOM_JDK((byte) 3, "x-application/jdk"),
    HESSIAN((byte) 4, "x-application/hessian2"),
    KRYO((byte) 5, "x-application/kryo"),
    PROTOSTUFF((byte) 6, "x-application/protostuff"),
    GSON((byte) 7, "x-application/gson"),
    FASTJSON((byte) 8, "x-application/fastjson"),

    ;

    private byte contentTypeId;
    private String contentType;
}
