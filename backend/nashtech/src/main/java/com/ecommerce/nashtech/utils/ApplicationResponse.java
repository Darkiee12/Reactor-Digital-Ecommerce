package com.ecommerce.nashtech.utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.nashtech.errors.BaseError;

import io.jsonwebtoken.lang.Arrays;

public class ApplicationResponse {
    public static class ErrResponse {
        public String message;
        public String code;
        public String instance;
        public String timestamp;

        public static final String ExampleObject = """
                    {
                        "message": "string",
                        "code": "string",
                        "instance": "string",
                        "timestamp": "string"
                    }
                """;

        private ErrResponse(BaseError error, String instance) {
            this.message = error.getMessage();
            this.code = error.getCode();
            this.timestamp = Instant.now().toString();
            this.instance = instance;
        }

        public static String build(BaseError error, String instance) {
            var object = new ErrResponse(error, instance);
            return JSON.stringify(object).unwrapOr("{}");
        }

        public static String customError(String instance, String... args) {
            List<String> errorList = new ArrayList<>(Arrays.asList(args));
            errorList.add("instance");
            errorList.add(instance);
            errorList.add("timestamp");
            errorList.add(Instant.now().toString());
            return JSON.fromArgs(errorList.toArray(new String[0]));
        }

        @Override
        public String toString() {
            return JSON.fromArgs("message", this.message, "code", this.code, "instance", this.instance, "timestamp",
                    this.timestamp

            );
        }
    }

    public static class LoginResponse{
        public String message = "Login successfully";
        public String token;

        public static final String ExampleObject = """
        {
            "message": "string",
            "token": "string"
        }""";

        public LoginResponse(String token) {
            this.token = token;
        }

        public static String build(String token) {
            var object = new LoginResponse(token);
            return JSON.stringify(object).unwrapOr("{}");
        }
    }

}
