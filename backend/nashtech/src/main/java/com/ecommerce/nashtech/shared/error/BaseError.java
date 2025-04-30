package com.ecommerce.nashtech.shared.error;

import org.springframework.http.ResponseEntity;

import com.ecommerce.nashtech.shared.json.JSON;
import com.ecommerce.nashtech.shared.response.ErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

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
