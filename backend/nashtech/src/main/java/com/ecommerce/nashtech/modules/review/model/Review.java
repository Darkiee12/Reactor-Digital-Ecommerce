package com.ecommerce.nashtech.modules.review.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("reviews")
public class Review {

    @Id
    @Column("id")
    Long id;

    @Column("uuid")
    UUID uuid = UUID.randomUUID();

    @Column("product_id")
    Long productId;

    @Column("user_id")
    Long userId;

    @Column("rating")
    Integer rating;

    @Column("comment")
    String comment;

    @Column("created_at")
    LocalDateTime createdAt = LocalDateTime.now();
}
