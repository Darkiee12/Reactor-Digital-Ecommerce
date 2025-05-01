package com.ecommerce.nashtech.modules.category.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.category.model.Category;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryRepository extends R2dbcRepository<Category, Long> {

    Mono<Category> findById(Long id);

    Mono<Category> findByName(String name);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByName(String name);
}
