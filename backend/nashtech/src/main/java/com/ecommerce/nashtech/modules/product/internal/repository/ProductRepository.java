package com.ecommerce.nashtech.modules.product.internal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.product.dto.ProductBrandCountDto;
import com.ecommerce.nashtech.modules.product.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {

    Mono<Product> findById(Long id);

    Mono<Product> findByUuid(UUID uuid);

    Mono<Product> findByName(String name);

    @Query("""
                SELECT P.*
                FROM productS AS p
                INNER JOIN brands AS b
                ON p.brand_id = b.id
                WHERE b.id = :id
                LIMIT :limit OFFSET :offset
            """)
    Flux<Product> findAllByBrand(Long id, int limit, int offset);

    @Query("""
                SELECT COUNT(*)
                FROM products AS p
                INNER JOIN brands AS b
                ON p.brand_id = b.id
                WHERE b.id = :id
            """)
    Mono<Long> countByBrandId(Long id);

    @Query("""
                SELECT P.*
                FROM products AS p
                INNER JOIN categories AS c
                ON p.category_id = c.id
                WHERE c.id = :id
                LIMIT :limit OFFSET :offset
            """)
    Flux<Product> findAllByCategory(Long id, int limit, int offset);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<Boolean> existsByName(String name);
}
