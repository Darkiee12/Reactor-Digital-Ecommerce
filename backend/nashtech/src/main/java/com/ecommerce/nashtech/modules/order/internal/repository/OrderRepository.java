package com.ecommerce.nashtech.modules.order.internal.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.order.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {

    Mono<Order> findById(Long id);

    Mono<Order> findByUuid(UUID uuid);

    Flux<Order> findByUserId(Long userId);

    Flux<Order> findByStatus(String status);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByUuid(UUID uuid);
}
