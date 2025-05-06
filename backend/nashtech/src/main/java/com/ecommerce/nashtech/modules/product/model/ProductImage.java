package com.ecommerce.nashtech.modules.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("product_image")

public class ProductImage {

    @Id
    @Column("id")
    Long id;

    @Column("product_uuid")
    UUID productUuid;

    @Column("image_uuid")
    UUID imageUuid;

    @Column("created_at")
    Long createdAt;
}