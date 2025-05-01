package com.ecommerce.nashtech.modules.brand.dto;

import lombok.Builder;

import javax.annotation.Nullable;

@Builder
public record UpdateBrandDto(
        @Nullable String name,
        @Nullable String logo) {
}
