package com.ecommerce.nashtech.modules.product.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.image.model.Image;
import com.ecommerce.nashtech.modules.product.dto.UpdateProductDto;
import com.ecommerce.nashtech.modules.product.error.ProductError;
import com.ecommerce.nashtech.modules.product.service.ProductService;
import com.ecommerce.nashtech.shared.enums.ProductFinder;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.util.Router;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/products")
@RestController
public class ProductController implements IProductController {

    ProductService productService;
    Router router = new Router("/api/v1/products");

    @Override
    @GetMapping("/uuid/{uuid}")
    public Mono<ResponseEntity<String>> getProductByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid) {
        var instance = router.getURI("uuid", uuid);
        var finder = new ProductFinder.ByUuid(uuid);
        return productService
                .getFullProduct(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @GetMapping("/id/{id}")
    public Mono<ResponseEntity<String>> getProductById(
            ServerWebExchange exchange,
            @PathVariable Long id) {
        var instance = router.getURI("id", id);
        var finder = new ProductFinder.ById(id);
        return productService
                .getFullProduct(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @GetMapping("/brand/{brandId}")
    public Mono<ResponseEntity<String>> getProductsByBrandId(
            ServerWebExchange exchange,
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var instance = router.getURI("brandId", brandId);
        Pageable pageable = PageRequest.of(page, size);
        return productService
                .findAllByBrand(pageable, brandId)
                .collectList()
                .zipWith(productService.countByBrand(brandId))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(productPage -> SuccessfulResponse.WithPageableData.of(productPage, instance)
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());

    }

    @Override
    @GetMapping("/category/{categoryId}")
    public Mono<ResponseEntity<String>> getProductsByCategoryId(
            ServerWebExchange exchange,
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var instance = router.getURI("categoryId", categoryId);
        Pageable pageable = PageRequest.of(page, size);
        return productService
                .findAllByCategory(pageable, categoryId)
                .collectList()
                .zipWith(productService.countByCategory(categoryId))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(productPage -> SuccessfulResponse.WithPageableData.of(productPage, instance)
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @PostMapping(value = "{productUuid}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadProductImages(
            ServerWebExchange exchange,
            @RequestPart("files") Flux<FilePart> fileParts,
            @PathVariable UUID productUuid,
            @RequestPart("altText") String altText) {
        var instance = router.getURI("image", productUuid);
        return productService
                .uploadProductImages(productUuid, fileParts, altText)
                .collectList()
                .map(images -> SuccessfulResponse.WithData.<List<Image>>builder()
                        .item(images)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @GetMapping(value = "{productUuid}/image/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> getProductImageMetadata(
            ServerWebExchange exchange,
            @PathVariable UUID productUuid) {
        var instance = router.getURI("image", "metadata", productUuid);
        return productService.getMetadataOfAllImages(productUuid)
                .collectList()
                .map(images -> SuccessfulResponse.WithData.<List<Image>>builder()
                        .item(images)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());

    }

    @GetMapping("/search")
    public Mono<ResponseEntity<String>> searchProductsByName(
            @RequestParam("name") String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var instance = router.getURI("search", searchTerm);
        Pageable pageable = PageRequest.of(page, size);
        return productService.findProducts(searchTerm, pageable)
                .collectList()
                .zipWith(productService.countByName(searchTerm))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(productPage -> SuccessfulResponse.WithPageableData.of(productPage, instance)
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());

    }

    @PatchMapping(value = "/uuid/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> updateProduct(
            ServerWebExchange exchange,
            @PathVariable UUID uuid,
            @RequestBody UpdateProductDto updateProductDto) {
        var instance = router.getURI("uuid", uuid);
        var finder = new ProductFinder.ByUuid(uuid);
        return productService
                .updateProduct(finder, updateProductDto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }
}
