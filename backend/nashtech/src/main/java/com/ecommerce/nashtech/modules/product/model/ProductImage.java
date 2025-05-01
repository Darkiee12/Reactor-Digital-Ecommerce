package com.ecommerce.nashtech.modules.product.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("product_image")
public class ProductImage {

    @Id
    @Column("id")
    Long id;

    @Column("product_id")
    Long productId;

    @Column("image_id")
    Long imageId;

    @Column("created_at")
    LocalDateTime createdAt = LocalDateTime.now();
}