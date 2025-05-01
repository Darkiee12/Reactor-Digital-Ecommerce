package com.ecommerce.nashtech.modules.brand.dto;

import com.ecommerce.nashtech.modules.brand.model.Brand;

import lombok.Builder;

@Builder
public record BrandDto(
        String name,
        String logo) {

    public static BrandDto from(Brand brand) {
        return new BrandDto(
                brand.getName(),
                null
        // brand.getLogo() //TODO: Implement logo handling
        );
    }
}
