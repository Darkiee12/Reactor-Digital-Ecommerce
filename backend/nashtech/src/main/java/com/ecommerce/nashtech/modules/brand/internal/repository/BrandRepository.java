package com.ecommerce.nashtech.modules.brand.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.brand.model.Brand;
import reactor.core.publisher.Mono;

@Repository
public interface BrandRepository extends R2dbcRepository<Brand, Long> {

    Mono<Brand> findById(Long id);

    Mono<Brand> findByName(String name);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByName(String name);
}
