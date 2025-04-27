package com.ecommerce.nashtech.modules.product.internal.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.product.model.Product;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {

    Mono<Product> findById(Long id);

    Mono<Product> findByUuid(UUID uuid);

    Mono<Product> findByName(String name);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<Boolean> existsByName(String name);
}
