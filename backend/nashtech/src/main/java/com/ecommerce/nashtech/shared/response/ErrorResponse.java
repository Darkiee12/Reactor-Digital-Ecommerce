package com.ecommerce.nashtech.shared.response;

import java.time.Instant;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.ecommerce.nashtech.shared.error.BaseError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@FieldDefaults(level = lombok.AccessLevel.PUBLIC)
@Data
@AllArgsConstructor
public class ErrorResponse implements BaseResponse {
    String instance;
    String timestamp;
    String message;
    String code;

    @Builder
    public static class Err {
        BaseError error;
        String instance;

        public ErrorResponse buildResponse() {
            return new ErrorResponse(
                    instance,
                    Instant.now().toString(),
                    error.getMessage(),
                    error.getCode());

        }
    }

    public static ErrorResponse build(BaseError error, String instance) {
        return ErrorResponse.Err.builder()
                .error(error)
                .instance(instance)
                .build()
                .buildResponse();
    }

    public ResponseEntity<String> asResponse() {
        return ResponseEntity.badRequest().body(toJSON());
    }

    public ResponseEntity<String> asResponse(HttpStatusCode status) {
        return ResponseEntity.status(status).body(toJSON());
    }

    public Mono<ResponseEntity<String>> asMonoResponse() {
        return Mono.just(asResponse());
    }

    public Mono<ResponseEntity<String>> asMonoResponse(HttpStatusCode status) {
        return Mono.just(asResponse(status));
    }

}
