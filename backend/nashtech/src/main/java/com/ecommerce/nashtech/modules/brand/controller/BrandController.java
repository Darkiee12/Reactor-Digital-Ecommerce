package com.ecommerce.nashtech.modules.brand.controller;

import com.ecommerce.nashtech.modules.brand.dto.UpdateBrandDto;
import com.ecommerce.nashtech.modules.brand.error.BrandError;
import com.ecommerce.nashtech.modules.brand.dto.CreateBrandDto;
import com.ecommerce.nashtech.modules.brand.service.IBrandService;
import com.ecommerce.nashtech.modules.product.error.ProductError;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.util.Router;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/brands")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BrandController implements IBrandController {

    IBrandService brandService;
    Router router = new Router("/api/brands");

    @Override
    @GetMapping
    public Mono<ResponseEntity<String>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var instance = router.getURI("");
        Pageable pageable = PageRequest.of(page, size);
        return brandService.findAll(pageable)
                .collectList()
                .zipWith(brandService.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(brandPage -> SuccessfulResponse.WithPageableData.of(brandPage, instance)
                        .asResponse());
    }

    @Override
    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> getById(@PathVariable Long id) {
        var instance = router.getURI(id);
        return brandService.findDtoById(id)
                .map(brand -> SuccessfulResponse.WithData.builder().item(brand).instance(instance)
                        .build().toJSON())
                .map(ResponseEntity::ok)
                .onErrorResume(BrandError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());

    }

    @Override
    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestBody Mono<CreateBrandDto> dto) {
        var instance = router.getURI("");

        return dto.flatMap(brandService::create)
                .map(brand -> SuccessfulResponse.WithData.builder().item(brand).instance(instance)
                        .build().asResponse())
                .onErrorResume(BrandError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @PatchMapping("/{id}")
    public Mono<ResponseEntity<String>> update(@PathVariable Long id, @RequestBody UpdateBrandDto dto) {
        var instance = router.getURI(id);
        return brandService.update(id, dto)
                .map(brand -> SuccessfulResponse.WithData.builder().item(brand).instance(instance)
                        .build().asResponse())
                .onErrorResume(BrandError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable Long id) {
        var instance = router.getURI(id);
        return brandService.delete(id)
                .then(Mono.just(ResponseEntity.ok().body("Deleted successfully")))
                .onErrorResume(BrandError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @PostMapping(value = "{brandId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadBrandImage(
            ServerWebExchange exchange,
            @RequestPart("file") Mono<FilePart> filePart,
            @PathVariable Long brandId,
            @RequestPart("altText") String altText) {
        var instance = router.getURI(brandId, "image");
        return filePart
                .flatMap(file -> brandService.uploadImage(brandId, file, altText))
                .map(image -> SuccessfulResponse.WithMessage.builder()
                        .message("Brand image has been updated successfully.")
                        .instance(instance)
                        .build().asResponse())
                .onErrorResume(ProductError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @GetMapping("/search")
    public Mono<ResponseEntity<String>> search(@RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var instance = router.getURI("search");
        Pageable pageable = PageRequest.of(page, size);
        return brandService.search(searchTerm, pageable)
                .collectList()
                .zipWith(brandService.countByNameContainingIgnoreCase(searchTerm))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(brandPage -> SuccessfulResponse.WithPageableData.of(brandPage, instance)
                        .asResponse());
    }

}
