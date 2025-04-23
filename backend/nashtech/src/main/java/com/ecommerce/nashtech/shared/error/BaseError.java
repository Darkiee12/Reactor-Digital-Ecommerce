package com.ecommerce.nashtech.shared.error;

import com.ecommerce.nashtech.shared.json.JSON;
import com.ecommerce.nashtech.shared.response.ErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PROTECTED)
public abstract class BaseError extends Throwable {
    String message;
    String code;

    public ErrorResponse toErrorResponse(String instance) {
        return ErrorResponse.build(this.message, this.code, instance);
    }

    public String toJSON(){
        return JSON.stringify(this).unwrap();
    }

}
