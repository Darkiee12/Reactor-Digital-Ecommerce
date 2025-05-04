package com.ecommerce.nashtech.modules.brand.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ecommerce.nashtech.modules.brand.dto.BrandDto;
import com.ecommerce.nashtech.modules.brand.dto.UpdateBrandDto;
import com.ecommerce.nashtech.modules.brand.error.BrandError;
import com.ecommerce.nashtech.modules.brand.internal.repository.BrandRepository;
import com.ecommerce.nashtech.modules.brand.model.Brand;
import com.ecommerce.nashtech.modules.product.dto.ProductBrandCountDto;
import com.ecommerce.nashtech.modules.product.service.IProductService;
import com.ecommerce.nashtech.shared.types.Option;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BrandService implements IBrandService {
    BrandRepository brandRepo;
    IProductService productService;
    R2dbcEntityTemplate template;
    TransactionalOperator txOperator;

    private Mono<Brand> findById(Long id) {
        return brandRepo.findById(id)
                .switchIfEmpty(Mono.error(BrandError.BrandNotFoundError.build(Option.some(id))));
    }

    @Override
    public Mono<Long> count() {
        return brandRepo.count();
    }

    @Override
    public Mono<BrandDto> findDtoById(Long id) {
        var productCount = productService.countByBrand(id);
        // var productDistinctCount = productService.countDistinctByBrand(id);
        var brandMono = findById(id);
        return Mono.zip(brandMono, productCount
        // , productDistinctCount
        )
                .map(tuple -> {
                    var brand = tuple.getT1();
                    var count = tuple.getT2();
                    // var distinctCount = tuple.getT3();
                    return BrandDto.from(brand, count);
                });
    }

    @Override
    public Flux<BrandDto> findAll(Pageable pageable) {
        return brandRepo.findAllBy(pageable)
                .collectList()
                .flatMapMany(brands -> {
                    var brandIds = brands.stream().map(Brand::getId).toList();
                    return productService.countByBrandIds(brandIds)
                            .collectMap(ProductBrandCountDto::brandId, ProductBrandCountDto::count)
                            .flatMapMany(countMap -> Flux.fromIterable(brands)
                                    .map(brand -> {
                                        Long productCount = countMap.getOrDefault(brand.getId(), 0L);

                                        return BrandDto.from(brand, productCount);
                                    }));

                });
    }

    @Override
    public Mono<Brand> create(BrandDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.name());
        return template.insert(Brand.class)
                .using(brand)
                .as(txOperator::transactional);
    }

    @Override
    public Mono<Brand> update(Long id, UpdateBrandDto dto) {
        return findById(id).map(brand -> Brand.builder().name(dto.name()).id(brand.getId()).build());
    }

    @Override
    public Mono<Void> delete(Long id) {
        return findById(id)
                .flatMap(brandRepo::delete)
                .as(txOperator::transactional);
    }
}
