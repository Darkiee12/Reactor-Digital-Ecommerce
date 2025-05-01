package com.ecommerce.nashtech.modules.product.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.ecommerce.nashtech.shared.json.Serde;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.r2dbc.postgresql.codec.Json;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FullProductDto {
    String name;
    UUID uuid;
    String brandName;
    String description;
    BigDecimal price;
    @JsonSerialize(using = Serde.Serializer.class)
    @JsonDeserialize(using = Serde.Deserializer.class)
    Json specifications;
    Integer stockQuantity;
    String type;
    Long createdAt;
    Long updatedAt;
    List<UUID> imagesUuid;
    List<String> categories;
}
