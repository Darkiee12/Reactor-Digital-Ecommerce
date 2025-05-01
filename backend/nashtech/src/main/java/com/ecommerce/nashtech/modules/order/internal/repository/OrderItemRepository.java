package com.ecommerce.nashtech.modules.order.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.order.model.OrderItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {

    Flux<OrderItem> findByOrderId(Long orderId);

    Flux<OrderItem> findByProductId(Long productId);

    Mono<Void> deleteByOrderIdAndProductId(Long orderId, Long productId);
}
