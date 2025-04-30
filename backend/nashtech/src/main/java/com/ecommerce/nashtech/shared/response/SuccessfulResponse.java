package com.ecommerce.nashtech.shared.response;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

public interface SuccessfulResponse extends BaseResponse {

    String getInstance();

    String getTimestamp();

    default ResponseEntity<String> asResponse() {
        return ResponseEntity.ok(toJSON());
    }

    default Mono<ResponseEntity<String>> asMonoResponse() {
        return Mono.just(asResponse());
    }

    @Builder
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    @Data
    public static final class WithData<T> implements SuccessfulResponse {
        T item;
        String instance;
        String timestamp;

        public WithData(T item, String instance, String timestamp) {
            this.timestamp = Instant.now().toString();
            this.instance = instance;
            this.item = item;
        }

        @Override
        public String getInstance() {
            return instance;
        }

        @Override
        public String getTimestamp() {
            return timestamp;
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public final class WithPageableData<T> implements SuccessfulResponse {

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Builder
        public static class Metadata {
            int page;
            int size;
            int totalPages;
            long totalItems;
            boolean hasNext;
            boolean hasPrevious;
        }

        List<T> items;
        Metadata page;
        String instance;
        String timestamp;

        private WithPageableData(Page<T> pageData, String instance) {
            this.items = pageData.getContent();
            this.page = Metadata.builder()
                    .page(pageData.getNumber())
                    .size(pageData.getSize())
                    .totalPages(pageData.getTotalPages())
                    .totalItems(pageData.getTotalElements())
                    .hasNext(pageData.hasNext())
                    .hasPrevious(pageData.hasPrevious())
                    .build();
            this.instance = instance;
            this.timestamp = Instant.now().toString();
        }

        public static <T> WithPageableDataBuilder<T> builder() {
            return new WithPageableDataBuilder<>();
        }

        public static <T> WithPageableDataBuilder<T> builder(Page<T> pageData) {
            return new WithPageableDataBuilder<T>().page(pageData);
        }

        public static <T> WithPageableData<T> of(Page<T> pageData, String instance) {
            Objects.requireNonNull(pageData, "pageData must not be null");
            return new WithPageableData<>(pageData, instance);
        }

        @Override
        public String getInstance() {
            return instance;
        }

        @Override
        public String getTimestamp() {
            return timestamp;
        }

        public static final class WithPageableDataBuilder<T> {
            private Page<T> pageData;
            private String instance;

            private WithPageableDataBuilder() {
            }

            public WithPageableDataBuilder<T> page(Page<T> pageData) {
                this.pageData = pageData;
                return this;
            }

            public WithPageableDataBuilder<T> instance(String instance) {
                this.instance = instance;
                return this;
            }

            public WithPageableData<T> build() {
                Objects.requireNonNull(pageData, "pageData must not be null");
                return new WithPageableData<>(pageData, instance);
            }
        }
    }

    @Builder
    @Data
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static final class WithMessage implements SuccessfulResponse {
        String message;
        String instance;
        String timestamp;

        public WithMessage(String message, String instance, String timestamp) {
            this.instance = instance;
            this.timestamp = Instant.now().toString();
            this.message = message;
        }

        @Override
        public String getInstance() {
            return instance;
        }

        @Override
        public String getTimestamp() {
            return timestamp;
        }
    }

}
