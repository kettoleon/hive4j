package com.github.kettoleon.hive4j.adapters.llama2.misc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;

public class StringUtils {

    public static int parseIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static <T> T parseJson(String json, Class<T> cls) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, cls);
    }

    public static List<String> parseJsonToListOfStrings(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class,String.class));
    }
}
