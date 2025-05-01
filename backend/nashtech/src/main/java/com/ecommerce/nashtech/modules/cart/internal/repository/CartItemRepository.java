package com.ecommerce.nashtech.modules.cart.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.cart.model.CartItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CartItemRepository extends R2dbcRepository<CartItem, Long> {

    Flux<CartItem> findByCartId(Long cartId);

    Flux<CartItem> findByProductId(Long productId);

    Mono<Void> deleteByCartIdAndProductId(Long cartId, Long productId);
}
