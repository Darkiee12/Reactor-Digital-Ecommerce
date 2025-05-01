package com.ecommerce.nashtech.shared.enums;

import java.util.UUID;

public sealed interface ProductFinder permits
        ProductFinder.ById,
        ProductFinder.ByUuid {
    public final record ById(long id) implements ProductFinder {
    }

    public final record ByUuid(UUID uuid) implements ProductFinder {
    }
}
