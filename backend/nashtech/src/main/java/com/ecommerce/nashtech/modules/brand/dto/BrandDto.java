package com.ecommerce.nashtech.modules.brand.dto;

import com.ecommerce.nashtech.modules.brand.model.Brand;

import lombok.Builder;

@Builder
public record BrandDto(
        Long id,
        String name,
        Long count) {

    public static BrandDto from(Brand brand, Long totalProductCount) {
        return new BrandDto(
                brand.getId(),
                brand.getName(),
                totalProductCount);
    }
}
