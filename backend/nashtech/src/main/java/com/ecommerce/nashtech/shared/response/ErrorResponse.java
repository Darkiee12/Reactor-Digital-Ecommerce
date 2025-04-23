package com.ecommerce.nashtech.shared.response;

import java.time.Instant;

import com.ecommerce.nashtech.shared.error.BaseError;


public class ErrorResponse extends BaseError{
    public String instance;
    public String timestamp;

    public static final String Example = """
            {
                "message": "string",
                "code": "string",
                "instance": "string",
                "timestamp": "string"
            }
        """;

    private ErrorResponse(String message, String code, String instance) {
        super(message, code);
        this.timestamp = Instant.now().toString();
        this.instance = instance;
    }

    public static ErrorResponse build(String message, String code, String instance) {
        var object = new ErrorResponse(message, code, instance);
        return object;
    }

}
