package com.ecommerce.nashtech.shared.response;

import java.time.Instant;

import com.ecommerce.nashtech.shared.json.JSON;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Data
public class SuccessfulResponse<T> {
    T data;
    String instance;
    String timestamp;

    private SuccessfulResponse(T data, String instance){
        this.data = data;
        this.instance = instance;
        this.timestamp = Instant.now().toString();
    }

    public static <T> String build(T data, String instance){
        var object = new SuccessfulResponse<>(data, instance);
        return JSON
            .stringify(object)
            .inspectErr(err -> log.error("Serialization failed", err))
            .unwrapOr("{}");
    }
}
