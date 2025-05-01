package com.ecommerce.nashtech.modules.brand.error;

import com.ecommerce.nashtech.shared.error.BaseError;
import com.ecommerce.nashtech.shared.types.Option;

/**
 * Defines Brand-specific domain errors for reactive flows.
 */
public sealed abstract class BrandError extends BaseError permits
        BrandError.BrandNotFoundError,
        BrandError.DuplicateBrandNameError {

    protected BrandError(String message, String code) {
        super(message, code);
    }

    public static final class BrandNotFoundError extends BrandError {
        private static final String CODE = "BRAND_100";

        private BrandNotFoundError(Option<Long> id) {
            super(
                    switch (id) {
                        case Option.Some<Long> v -> "Brand not found: " + v.get();
                        case Option.None<Long> n -> "Brand not found";
                    },
                    CODE);
        }

        public static BrandNotFoundError build(Option<Long> id) {
            return new BrandNotFoundError(id);
        }
    }

    public static final class DuplicateBrandNameError extends BrandError {
        private static final String CODE = "BRAND_101";

        private DuplicateBrandNameError() {
            super("Brand name already exists", CODE);
        }

        public static DuplicateBrandNameError build() {
            return new DuplicateBrandNameError();
        }
    }
}
