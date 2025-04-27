package com.ecommerce.nashtech.shared.enums;

import java.util.UUID;

public sealed interface ImageFinder permits ImageFinder.ByUuid, ImageFinder.ByObjectKey {
    public final record ByUuid(UUID uuid) implements ImageFinder {
    }

    public final record ByObjectKey(String objectKey) implements ImageFinder {
    }
}
