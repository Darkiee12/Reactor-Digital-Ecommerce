package com.ecommerce.nashtech.modules.product.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.product.dto.FullProductDto;
import com.ecommerce.nashtech.modules.product.model.Product;
import com.ecommerce.nashtech.shared.enums.ProductFinder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

    Mono<Product> find(ProductFinder finder);

    Mono<FullProductDto> getFullProduct(ProductFinder finder);

    Flux<FullProductDto> findAllByBrand(Pageable pageable, Long id);

    Mono<Long> countByBrand(Long id);

    Flux<FullProductDto> findAllByCategory(Pageable pageable, Long id);

    Mono<Long> countByCategory(Long id);

}
