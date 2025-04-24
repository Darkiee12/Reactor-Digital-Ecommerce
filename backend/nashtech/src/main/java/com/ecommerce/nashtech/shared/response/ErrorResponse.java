package com.ecommerce.nashtech.shared.response;

import java.time.Instant;

import com.ecommerce.nashtech.shared.error.BaseError;
import com.ecommerce.nashtech.shared.json.JSON;


public class ErrorResponse extends BaseError{
    public String instance;
    public String timestamp;

    public static class ErrorResponseObject {
        public String message;
        public String code;
        public String instance;
        public String timestamp;
    }

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

    @Override
    public String toJSON(){
        return JSON.fromArgs(
            "message", this.message,
            "code", this.code,
            "instance", this.instance,
            "timestamp", this.timestamp
        );
    }

}
