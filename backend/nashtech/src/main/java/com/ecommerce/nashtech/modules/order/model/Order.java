package com.ecommerce.nashtech.modules.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("orders")
public class Order {

    @Id
    @Column("id")
    Long id;

    @Column("uuid")
    UUID uuid = UUID.randomUUID();

    @Column("user_id")
    Long userId;

    @Column("total_amount")
    BigDecimal totalAmount;

    @Column("status")
    String status = "PENDING";

    @Column("created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    LocalDateTime updatedAt = LocalDateTime.now();
}
