package com.ecommerce.nashtech.modules.cart.model;

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
@Table("carts")
public class Cart {

    @Id
    @Column("id")
    Long id;

    @Column("user_id")
    Long userId;

    @Column("created_at")
    LocalDateTime createdAt = LocalDateTime.now();
}
