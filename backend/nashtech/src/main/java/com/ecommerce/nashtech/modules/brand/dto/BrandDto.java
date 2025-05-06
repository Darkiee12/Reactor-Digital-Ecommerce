package com.ecommerce.nashtech.modules.brand.dto;

import com.ecommerce.nashtech.modules.brand.model.Brand;

import lombok.Builder;

@Builder
public record BrandDto(
        String name,
        String logo,
        Long count) {

    public static BrandDto from(Brand brand, Long totalProductCount) {
        return new BrandDto(
                brand.getName(),
                null,
                totalProductCount
        // brand.getLogo() //TODO: Implement logo handling
        );
    }
}
