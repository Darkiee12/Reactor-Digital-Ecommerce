package com.ecommerce.nashtech.shared.json;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ecommerce.nashtech.shared.types.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSON {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Result<String, JsonProcessingException> stringify(Object obj) {
        return Result.wrap(() ->
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
        );
    }

    public static <T> Result<T, ? extends Exception> parse(String json, Class<T> clazz) {
        return Result.wrap(() ->
            mapper.readValue(json, clazz)
        );
    }

    public static String fromArgs(String... args) {
        Map<String, Object> map = new LinkedHashMap<>();
    
        for (int i = 0; i < args.length; i += 2) {
            String key = args[i];
            String value = (i + 1 < args.length) ? args[i + 1] : "";
    
            map.put(key, value);
        }
    
        return stringify(map).unwrap();
    }

    public static void log(Object obj){
        var result = stringify(obj);
        switch(result){
            case Result.Ok<String, JsonProcessingException> ok -> {
                log.info("JSON: {}", ok.get());
            }
            case Result.Err<String, JsonProcessingException> err -> {
                log.error("Error converting to JSON: {}", err.get());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T object){
        return Result.wrap(() -> mapper.readValue(mapper.writeValueAsBytes(object), (Class<T>) object.getClass())).unwrap();
    }

    public static Map<String, Object> mapify(Object obj){
        return mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    
}
