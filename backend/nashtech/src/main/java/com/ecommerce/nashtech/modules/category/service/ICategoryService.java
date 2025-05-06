package com.ecommerce.nashtech.modules.category.service;

import org.springframework.data.domain.Pageable;

import com.ecommerce.nashtech.modules.category.dto.CategoryDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICategoryService {
    Flux<CategoryDto> findAll(Pageable pageable);

    Mono<Long> count();
}
