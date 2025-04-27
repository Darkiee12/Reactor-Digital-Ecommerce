package com.ecommerce.nashtech.modules.review.model;

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
@Table("review_image")
public class ReviewImage {

    @Id
    @Column("id")
    Long id;

    @Column("review_id")
    Long reviewId;

    @Column("image_id")
    Long imageId;

    @Column("created_at")
    LocalDateTime createdAt = LocalDateTime.now();
}
