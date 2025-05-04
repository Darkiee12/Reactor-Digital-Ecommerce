package com.ecommerce.nashtech.modules.category.dto;

import com.ecommerce.nashtech.modules.category.model.Category;

import lombok.Builder;

@Builder
public record CategoryDto(
        String name,
        Long count) {
    public static CategoryDto from(Category category, Long totalProductCount) {
        return new CategoryDto(
                category.getName(),
                totalProductCount);
    }
}