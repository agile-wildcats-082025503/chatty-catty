package com.agilewildcats.chattyCatty.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmbeddingUtils {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(float[] vector) {
        try {
            return mapper.writeValueAsString(vector);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing embedding", e);
        }
    }

    public static float[] fromJson(String json) {
        try {
            return mapper.readValue(json, float[].class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing embedding", e);
        }
    }
}
