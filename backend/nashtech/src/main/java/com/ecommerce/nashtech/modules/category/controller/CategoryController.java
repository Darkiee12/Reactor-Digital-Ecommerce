package com.ecommerce.nashtech.modules.category.controller;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.nashtech.modules.category.service.ICategoryService;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.util.Router;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CategoryController implements ICategoryController {
    ICategoryService categoryService;
    Router router = new Router("/api/categories");

    @Override
    @GetMapping
    public Mono<ResponseEntity<String>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var instance = router.getURI("");
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.findAll(pageable)
                .collectList()
                .zipWith(categoryService.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(categoryPage -> SuccessfulResponse.WithPageableData.of(categoryPage, instance)
                        .asResponse());
    }

}
