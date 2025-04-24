package com.ecommerce.nashtech.shared.response;

import java.time.Instant;

import com.ecommerce.nashtech.shared.json.JSON;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SuccessfulResponse<T> {
    @SuppressWarnings("unused")
    T data;
    @SuppressWarnings("unused")
    String instance;
    @SuppressWarnings("unused")
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
