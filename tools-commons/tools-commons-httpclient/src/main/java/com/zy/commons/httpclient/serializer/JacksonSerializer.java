package com.zy.commons.httpclient.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Slf4j
public class JacksonSerializer implements Serializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public <T> byte[] serializer(T value) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            log.error("failed to serialized.", e);
            return new byte[0];
        }
    }

    @Override
    public <T> T deserializer(byte[] bytes, Class<T> returnType) {
        try {
            return OBJECT_MAPPER.readValue(bytes, returnType);
        } catch (IOException e) {
            log.error("failed to deserialized.", e);
        }
        return null;
    }

    @Override
    public <T> T deserializer(byte[] bytes) {
        try {
            return OBJECT_MAPPER.readValue(bytes, new TypeReference<T>() {});
        } catch (IOException e) {
            log.error("failed to deserialized.", e);
        }
        return null;
    }
}
