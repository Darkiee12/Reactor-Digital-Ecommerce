package com.ecommerce.nashtech.modules.product.error;

import com.ecommerce.nashtech.shared.error.BaseError;
import com.ecommerce.nashtech.shared.types.Option;

public sealed abstract class ProductError extends BaseError permits
        ProductError.ProductNotFoundError {

    protected ProductError(String message, String code) {
        super(message, code);
    }

    public static final class ProductNotFoundError extends ProductError {
        private static final String CODE = "PRODUCT_100";

        private ProductNotFoundError(Option<String> identifier) {
            super(
                    switch (identifier) {
                        case Option.Some<String> id -> "Product not found: " + id.get();
                        case Option.None<String> none -> "Product not found";
                    },
                    CODE);
        }

        public static ProductNotFoundError build(Option<String> identifier) {
            return new ProductNotFoundError(identifier);
        }
    }
}
