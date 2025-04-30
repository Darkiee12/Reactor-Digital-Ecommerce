package com.ecommerce.nashtech.modules.product.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ecommerce.nashtech.shared.json.Serde;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.r2dbc.postgresql.codec.Json;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("products")
public class Product {

    @Id
    @Column("id")
    Long id;

    @Column("uuid")
    UUID uuid = UUID.randomUUID();

    @Column("brand_id")
    Long brandId;

    @Column("name")
    String name;

    @Column("description")
    String description;

    @Column("price")
    BigDecimal price;

    @Column("stock_quantity")
    Integer stockQuantity;

    @Column("specifications")
    @JsonSerialize(using = Serde.Serializer.class)
    @JsonDeserialize(using = Serde.Deserializer.class)
    Json specifications;

    @Column("type")
    String type;

    @Column("is_deleted")
    Boolean deleted;

    @Column("created_at")
    Long createdAt;

    @Column("updated_at")
    Long updatedAt;
}