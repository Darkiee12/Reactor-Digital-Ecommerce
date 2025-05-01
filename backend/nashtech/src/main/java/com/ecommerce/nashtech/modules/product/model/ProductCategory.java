package com.ecommerce.nashtech.modules.product.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("product_category")
public class ProductCategory {

    @Column("product_id")
    Long productId;

    @Column("category_id")
    Long categoryId;
}
