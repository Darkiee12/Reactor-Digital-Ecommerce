package com.ecommerce.nashtech.modules.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("order_item")
public class OrderItem {

    @Id
    @Column("id")
    Long id;

    @Column("order_id")
    Long orderId;

    @Column("product_id")
    Long productId;

    @Column("quantity")
    Integer quantity;

    @Column("unit_price")
    BigDecimal unitPrice;
}
