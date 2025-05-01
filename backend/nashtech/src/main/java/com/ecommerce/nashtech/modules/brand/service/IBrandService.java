package com.ecommerce.nashtech.modules.brand.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import com.ecommerce.nashtech.modules.brand.dto.BrandDto;
import com.ecommerce.nashtech.modules.brand.dto.UpdateBrandDto;
import com.ecommerce.nashtech.modules.brand.model.Brand;

public interface IBrandService {

    Mono<BrandDto> findDtoById(Long id);

    Flux<BrandDto> findAll(Pageable pageable);

    Mono<Long> count();

    Mono<Brand> create(BrandDto dto);

    Mono<Brand> update(Long id, UpdateBrandDto dto);

    Mono<Void> delete(Long id);
}
