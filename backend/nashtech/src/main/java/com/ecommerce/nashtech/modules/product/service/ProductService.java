package com.ecommerce.nashtech.modules.product.service;

import com.ecommerce.nashtech.modules.brand.internal.repository.BrandRepository;
import com.ecommerce.nashtech.modules.brand.model.Brand;
import com.ecommerce.nashtech.modules.category.model.Category;
import com.ecommerce.nashtech.modules.product.dto.FullProductDto;
import com.ecommerce.nashtech.modules.product.error.ProductError;
import com.ecommerce.nashtech.modules.product.internal.repository.ProductRepository;
import com.ecommerce.nashtech.modules.product.internal.repository.ProductCategoryRepository;
import com.ecommerce.nashtech.modules.product.model.Product;
import com.ecommerce.nashtech.shared.enums.ProductFinder;
import com.ecommerce.nashtech.shared.types.Option;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class ProductService implements IProductService {

    ProductRepository productRepo;
    ProductCategoryRepository productCategoryRepo;
    BrandRepository brandRepo;
    TransactionalOperator txOperator;

    @Override
    public Mono<Product> find(ProductFinder finder) {
        var product = switch (finder) {
            case ProductFinder.ById id -> productRepo.findById(id.id());
            case ProductFinder.ByUuid uuid -> productRepo.findByUuid(uuid.uuid());
        };
        return product.filter(prod -> !prod.getDeleted())
                .switchIfEmpty(Mono.error(ProductError.ProductNotFoundError.build(Option.none())));
    }

    @Override
    public Mono<FullProductDto> getFullProduct(ProductFinder finder) {
        return find(finder)
                .flatMap(product -> {
                    Mono<Brand> brandMono = brandRepo.findById(product.getBrandId());
                    Mono<List<String>> categoriesMono = productCategoryRepo
                            .findAllCategoriesByProductId(product.getId())
                            .map(Category::getName)
                            .collectList();
                    return Mono.zip(brandMono, categoriesMono)
                            .map(tuple -> {
                                var brand = tuple.getT1();
                                var categories = tuple.getT2();
                                return FullProductDto.builder()
                                        .brandName(brand.getName())
                                        .name(product.getName())
                                        .uuid(product.getUuid())
                                        .description(product.getDescription())
                                        .price(product.getPrice())
                                        .specifications(product.getSpecifications())
                                        .stockQuantity(product.getStockQuantity())
                                        .type(product.getType())
                                        .createdAt(product.getCreatedAt())
                                        .updatedAt(product.getUpdatedAt())
                                        .imagesUuid(null) // Todo: Add logic to fetch images
                                        .categories(categories)
                                        .build();
                            });
                });
    }

    @Override
    public Flux<FullProductDto> findAllByBrand(Pageable pageable, Long id) {
        var limit = pageable.getPageSize();
        var offset = pageable.getPageNumber() * limit;
        return productRepo.findAllByBrand(id, limit, offset)
                .filter(product -> !product.getDeleted())
                .flatMap(product -> {
                    var finder = new ProductFinder.ById(product.getId());
                    return getFullProduct(finder);
                });
    }

    @Override
    public Mono<Long> countByBrand(Long id) {
        return productRepo.countByBrandId(id)
                .filter(count -> count > 0)
                .switchIfEmpty(Mono.error(ProductError.ProductNotFoundError.build(Option.none())));
    }

    @Override
    public Flux<FullProductDto> findAllByCategory(Pageable pageable, Long id) {
        var limit = pageable.getPageSize();
        var offset = pageable.getPageNumber() * limit;
        return productCategoryRepo.findAllProductsByCategoryId(id, limit, offset)
                .filter(product -> !product.getDeleted())
                .flatMap(product -> {
                    var finder = new ProductFinder.ById(product.getId());
                    return getFullProduct(finder);
                });
    }

    @Override
    public Mono<Long> countByCategory(Long id) {
        return productCategoryRepo.countByCategoryId(id)
                .filter(count -> count > 0)
                .switchIfEmpty(Mono.error(ProductError.ProductNotFoundError.build(Option.none())));
    }

}
