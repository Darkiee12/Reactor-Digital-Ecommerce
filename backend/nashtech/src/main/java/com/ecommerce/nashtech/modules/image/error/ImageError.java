package com.ecommerce.nashtech.modules.image.error;

import com.ecommerce.nashtech.modules.image.error.ImageError.ImageSizeLimitExceededError;
import com.ecommerce.nashtech.shared.error.BaseError;

import com.ecommerce.nashtech.shared.types.Option;

public sealed abstract class ImageError extends BaseError
        permits ImageError.ImageNotFoundError,
        ImageError.ImageValidationError,
        ImageError.ImageStorageError,
        ImageError.ImageDeletionError, ImageSizeLimitExceededError {

    protected ImageError(String message, String code) {
        super(message, code);
    }

    /**
     * Thrown when an image cannot be found by its UUID or key.
     */
    public static final class ImageNotFoundError extends ImageError {
        private static final String code = "IMAGEnone100";

        private ImageNotFoundError(Option<String> identifier) {
            super(
                    switch (identifier) {
                        case Option.Some<String> id -> "Image not found: " + id.get();
                        case Option.None<String> none -> "Image not found";
                    },
                    code);
        }

        public static ImageNotFoundError build(Option<String> identifier) {
            return new ImageNotFoundError(identifier);
        }
    }

    /**
     * Thrown when the provided image data fails validation rules.
     */
    public static final class ImageValidationError extends ImageError {
        private static final String code = "IMAGEnone101";

        private ImageValidationError(String detail) {
            super("Invalid image data: " + detail, code);
        }

        public static ImageValidationError build(String detail) {
            return new ImageValidationError(detail);
        }
    }

    /**
     * Thrown when storing the image in object storage (e.g., MinIO) fails.
     */
    public static final class ImageStorageError extends ImageError {
        private static final String code = "IMAGEnone102";

        private ImageStorageError(Option<String> cause) {
            super(
                    switch (cause) {
                        case Option.Some<String> c -> "Failed to store image: " + c.get();
                        case Option.None<String> none -> "Failed to store image";
                    },
                    code);
        }

        public static ImageStorageError build(Option<String> cause) {
            return new ImageStorageError(cause);
        }
    }

    /**
     * Thrown when deleting the image from object storage or database fails.
     */
    public static final class ImageDeletionError extends ImageError {
        private static final String code = "IMAGEnone103";

        private ImageDeletionError(Option<String> identifier) {
            super(
                    switch (identifier) {
                        case Option.Some<String> id -> "Failed to delete image: " + id.get();
                        case Option.None<String> none -> "Failed to delete image";
                    },
                    code);
        }

        public static ImageDeletionError build(Option<String> identifier) {
            return new ImageDeletionError(identifier);
        }
    }

    /**
     * Thrown when the image exceeds the configured size limit (e.g., 10MB).
     */
    public static final class ImageSizeLimitExceededError extends ImageError {
        private static final String code = "IMAGE_104";

        private ImageSizeLimitExceededError(long actualSize, long maxSize) {
            super(
                    "Image size too large: " + actualSize + " bytes (max " + maxSize + " bytes)",
                    code);
        }

        public static ImageSizeLimitExceededError build(long actualSize, long maxSize) {
            return new ImageSizeLimitExceededError(actualSize, maxSize);
        }
    }
}
