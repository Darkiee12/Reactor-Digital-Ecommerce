package com.ecommerce.nashtech.modules.product.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;

import com.ecommerce.nashtech.modules.image.model.Image;
import com.ecommerce.nashtech.modules.product.dto.FullProductDto;
import com.ecommerce.nashtech.modules.product.dto.ProductBrandCountDto;
import com.ecommerce.nashtech.modules.product.dto.ProductCategoryCountDto;
import com.ecommerce.nashtech.modules.product.model.Product;
import com.ecommerce.nashtech.shared.enums.ProductFinder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

    Mono<Product> find(ProductFinder finder);

    Mono<FullProductDto> getFullProduct(ProductFinder finder);

    Flux<FullProductDto> findAllByBrand(Pageable pageable, Long id);

    Mono<Long> countByBrand(Long id);

    // Mono<Long> countDistinctByBrand(Long id);

    Flux<ProductBrandCountDto> countByBrandIds(List<Long> brandIds);

    Flux<FullProductDto> findAllByCategory(Pageable pageable, Long id);

    Flux<ProductCategoryCountDto> countByCategoryIds(List<Long> categoryIds);

    Mono<Long> countByCategory(Long id);

    Mono<Image> uploadProductImage(UUID productUuid,
            FilePart filePart,
            String altText);

    Flux<Image> uploadProductImages(UUID productUuid,
            Flux<FilePart> fileParts,
            String altText);

}
