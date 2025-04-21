package com.ecommerce.nashtech.errors;


import com.ecommerce.nashtech.utils.ApplicationResponse.ErrResponse;
import com.ecommerce.nashtech.utils.JSON;

public sealed interface BaseError permits AccountError {
    String getCode();
    String getMessage();

    public static final String ExampleObject = """
        {
            "message": "string",
            "code": "string"
        }
        """;
    default String toJSON(){
        return JSON.stringify(this).unwrap();
    }

    default String toErrResponse(String instance){
        return ErrResponse.build(this, instance);
    }
}
