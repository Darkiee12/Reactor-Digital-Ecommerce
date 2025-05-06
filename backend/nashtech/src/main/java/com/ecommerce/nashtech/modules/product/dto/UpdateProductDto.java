package com.ecommerce.nashtech.modules.product.dto;

import java.math.BigDecimal;

public record UpdateProductDto(
        String name,
        String description,
        BigDecimal price,
        Long stockQuantity) {

}
