package com.xxg.xtoolkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class JsonUtil {

    private final static ObjectMapper COMMON_MAPPER;
    private final static ObjectMapper SNAKE_CASE_MAPPER;

    static {
        COMMON_MAPPER = new ObjectMapper();
        COMMON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SNAKE_CASE_MAPPER = new ObjectMapper();
        SNAKE_CASE_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SNAKE_CASE_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public static String toJSONString(Object obj) throws JsonProcessingException {
        return COMMON_MAPPER.writeValueAsString(obj);
    }

    public static <T> T parseJSON(String json, Class<T> clazz) throws IOException {
        return COMMON_MAPPER.readValue(json, clazz);
    }

    public static String toSnakeCaseJSONString(Object obj) throws JsonProcessingException {
        return SNAKE_CASE_MAPPER.writeValueAsString(obj);
    }

    public static <T> T parseSnakeCaseJSON(String json, Class<T> clazz) throws IOException {
        return SNAKE_CASE_MAPPER.readValue(json, clazz);
    }
}
