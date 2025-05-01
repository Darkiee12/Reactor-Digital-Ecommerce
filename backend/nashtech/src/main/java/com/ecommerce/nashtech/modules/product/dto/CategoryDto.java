package com.ecommerce.nashtech.modules.product.dto;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CategoryDto {
    Long categoryId;
    String categoryName;
}