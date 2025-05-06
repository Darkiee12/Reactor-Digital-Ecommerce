package com.ecommerce.nashtech.modules.product.service;

import com.ecommerce.nashtech.modules.brand.internal.repository.BrandRepository;
import com.ecommerce.nashtech.modules.brand.model.Brand;
import com.ecommerce.nashtech.modules.category.model.Category;
import com.ecommerce.nashtech.modules.image.model.Image;
import com.ecommerce.nashtech.modules.image.service.ImageService;
import com.ecommerce.nashtech.modules.product.dto.FullProductDto;
import com.ecommerce.nashtech.modules.product.dto.ProductBrandCountDto;
import com.ecommerce.nashtech.modules.product.dto.ProductCategoryCountDto;
import com.ecommerce.nashtech.modules.product.dto.UpdateProductDto;
import com.ecommerce.nashtech.modules.product.error.ProductError;
import com.ecommerce.nashtech.modules.product.internal.repository.ProductRepository;
import com.ecommerce.nashtech.modules.product.internal.patch.ProductPatcher;
import com.ecommerce.nashtech.modules.product.internal.repository.ProductCategoryRepository;
import com.ecommerce.nashtech.modules.product.internal.repository.ProductImageRepository;
import com.ecommerce.nashtech.modules.product.model.Product;
import com.ecommerce.nashtech.modules.product.model.ProductImage;
import com.ecommerce.nashtech.shared.enums.ProductFinder;
import com.ecommerce.nashtech.shared.types.Option;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class ProductService implements IProductService {

    ProductRepository productRepo;
    ProductCategoryRepository productCategoryRepo;
    ProductImageRepository productImageRepo;
    BrandRepository brandRepo;
    TransactionalOperator txOperator;
    R2dbcEntityTemplate template;
    ImageService imageService;
    ProductPatcher productPatcher;

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
                    Mono<List<ProductImage>> productImages = productImageRepo.findAllByProductUuid(product.getUuid())
                            .collectList();
                    return Mono.zip(brandMono, categoriesMono, productImages)
                            .map(tuple -> {
                                var brand = tuple.getT1();
                                var categories = tuple.getT2();
                                var imagesUuid = tuple.getT3().stream().map(ProductImage::getImageUuid).toList();
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
                                        .imagesUuid(imagesUuid)
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
        return productRepo.countByBrandId(id);
    }

    @Override
    public Flux<ProductBrandCountDto> countByBrandIds(List<Long> brandIds) {
        if (brandIds.isEmpty()) {
            return Flux.empty();
        }
        return template.getDatabaseClient()
                .sql("SELECT brand_id AS brandId, COUNT(*) AS count FROM products WHERE brand_id = ANY(:ids) GROUP BY brand_id")
                .bind("ids", brandIds.toArray(new Long[0]))
                .map((row, metadata) -> new ProductBrandCountDto(
                        row.get("brandId", Long.class),
                        row.get("count", Long.class)))
                .all();
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

    @Override
    public Flux<ProductCategoryCountDto> countByCategoryIds(List<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return Flux.empty();
        }
        return template.getDatabaseClient()
                .sql("SELECT category_id AS categoryId, COUNT(*) AS count FROM product_category WHERE category_id = ANY(:ids) GROUP BY category_id")
                .bind("ids", categoryIds.toArray(new Long[0]))
                .map((row, metadata) -> new ProductCategoryCountDto(
                        row.get("categoryId", Long.class),
                        row.get("count", Long.class)))
                .all();
    }

    @Override
    public Mono<Image> uploadProductImage(UUID productUuid,
            FilePart filePart,
            String altText) {
        return imageService.uploadImage(filePart, altText)
                .flatMap(image -> {
                    ProductImage link = ProductImage.builder()
                            .productUuid(productUuid)
                            .imageUuid(image.getUuid())
                            .createdAt(Instant.now().toEpochMilli())
                            .build();
                    return productImageRepo.save(link)
                            .thenReturn(image);
                })
                .as(txOperator::transactional);
    }

    @Override
    public Flux<Image> uploadProductImages(UUID productUuid,
            Flux<FilePart> fileParts,
            String altText) {
        return fileParts
                .flatMap(file -> uploadProductImage(productUuid, file, altText))
                .as(txOperator::transactional);
    }

    public Flux<Image> getProductImages(UUID productUuid) {
        return productImageRepo.findAllByProductUuid(productUuid)
                .flatMap(link -> imageService.getImageMetadata(link.getImageUuid()));
    }

    public Flux<Image> getMetadataOfAllImages(UUID productUuid) {
        return productImageRepo.findAllByProductUuid(productUuid)
                .flatMap(link -> imageService.getImageMetadata(link.getImageUuid()));
    }

    public Flux<Image> getImagesByUuids(List<UUID> imageUuids) {
        return Flux.fromIterable(imageUuids)
                .flatMap(imageService::getImageMetadata);
    }

    public Flux<Image> getImagesByUuids(Flux<UUID> imageUuids) {
        return imageUuids
                .flatMap(imageService::getImageMetadata);
    }

    public Flux<FullProductDto> findProductsByNamePrefix(String namePrefix) {
        if (namePrefix == null || namePrefix.trim().isEmpty()) {
            return Flux.empty();
        }
        return productRepo.findByNameStartingWithIgnoreCase(namePrefix)
                .flatMap(id -> {
                    var finder = new ProductFinder.ById(id.getId());
                    return getFullProduct(finder);
                });
    }

    public Mono<Long> countByNamePrefix(String namePrefix) {
        if (namePrefix == null || namePrefix.trim().isEmpty()) {
            return Mono.just(0L);
        }
        return productRepo.countByNameStartingWithIgnoreCase(namePrefix);
    }

    public Flux<FullProductDto> findProducts(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Flux.empty();
        }
        return productRepo.findByNameContainingIgnoreCase(searchTerm, pageable)
                .flatMap(id -> {
                    var finder = new ProductFinder.ById(id.getId());
                    return getFullProduct(finder);
                });

    }

    public Mono<Long> countByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Mono.just(0L);
        }
        return productRepo.countByNameContainingIgnoreCase(searchTerm);
    }

    public Mono<Product> updateProduct(ProductFinder finder, UpdateProductDto updateProductDto) {
        return find(finder)
                .flatMap(product -> {
                    productPatcher.patch(product, updateProductDto);
                    return productRepo.save(product);
                })
                .as(txOperator::transactional);
    }

}
