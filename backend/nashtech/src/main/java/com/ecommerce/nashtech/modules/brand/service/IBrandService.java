package com.ecommerce.nashtech.modules.brand.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;

import com.ecommerce.nashtech.modules.brand.dto.BrandDto;
import com.ecommerce.nashtech.modules.brand.dto.CreateBrandDto;
import com.ecommerce.nashtech.modules.brand.dto.UpdateBrandDto;
import com.ecommerce.nashtech.modules.brand.model.Brand;

public interface IBrandService {

    Mono<BrandDto> findDtoById(Long id);

    Flux<BrandDto> findAll(Pageable pageable);

    Mono<Long> count();

    Mono<Brand> create(CreateBrandDto dto);

    Mono<Brand> update(Long id, UpdateBrandDto dto);

    Mono<Void> delete(Long id);

    Mono<Void> uploadImage(Long id, FilePart logo, String altText);

    Flux<Brand> search(String searchTerm, Pageable pageable);

    Mono<Long> countByNameContainingIgnoreCase(String searchTerm);
}
