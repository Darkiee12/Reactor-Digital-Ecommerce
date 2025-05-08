package com.ecommerce.nashtech.modules.review.model;

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
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@Table("reviews")
public class Review {

    @Id
    @Column("id")
    Long id;

    @Column("uuid")
    UUID uuid;

    @Column("product_uuid")
    UUID productId;

    @Column("account_uuid")
    UUID userId;

    @Column("rating")
    Integer rating;

    @Column("comment")
    String comment;

    @Column("created_at")
    Long createdAt;
}
