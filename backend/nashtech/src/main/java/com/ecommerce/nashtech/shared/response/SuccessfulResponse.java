package com.ecommerce.nashtech.shared.response;

import java.time.Instant;

import com.ecommerce.nashtech.shared.json.JSON;

import lombok.Data;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
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
        return JSON.stringify(object).unwrapOr("{}");
    }
}
