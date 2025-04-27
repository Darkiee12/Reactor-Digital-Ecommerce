package com.ecommerce.nashtech.modules.cart.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.cart.model.Cart;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends R2dbcRepository<Cart, Long> {

    Mono<Cart> findById(Long id);

    Mono<Cart> findByUserId(Long userId);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByUserId(Long userId);
}
