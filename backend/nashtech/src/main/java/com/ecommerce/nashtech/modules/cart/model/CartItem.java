package com.ecommerce.nashtech.modules.cart.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table("cart_item")
public class CartItem {

    @Id
    @Column("id")
    Long id;

    @Column("cart_id")
    Long cartId;

    @Column("product_id")
    Long productId;

    @Column("quantity")
    Integer quantity;
}
