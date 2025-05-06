package com.ecommerce.nashtech.shared.error;

import com.ecommerce.nashtech.shared.json.JSON;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PROTECTED)
public abstract class BaseError extends RuntimeException {
    String message;
    String code;

    public String toJSON() {
        return JSON.stringify(this).unwrap();
    }
}
