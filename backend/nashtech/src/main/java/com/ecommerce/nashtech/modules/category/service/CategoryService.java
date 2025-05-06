package com.ecommerce.nashtech.modules.category.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.category.internal.repository.CategoryRepository;
import com.ecommerce.nashtech.modules.category.model.Category;
import com.ecommerce.nashtech.modules.product.dto.ProductCategoryCountDto;
import com.ecommerce.nashtech.modules.product.service.IProductService;
import com.ecommerce.nashtech.modules.category.dto.CategoryDto;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CategoryService implements ICategoryService {
    CategoryRepository categoryRepo;
    IProductService productService;

    @Override
    public Flux<CategoryDto> findAll(Pageable pageable) {
        return categoryRepo.findAllBy(pageable).collectList().flatMapMany(categories -> {
            var categoryIds = categories.stream().map(Category::getId).toList();
            return productService.countByCategoryIds(categoryIds)
                    .collectMap(ProductCategoryCountDto::categoryId, ProductCategoryCountDto::count)
                    .flatMapMany(categoryCountMap -> {
                        return Flux.fromIterable(categories)
                                .map(category -> {
                                    var count = categoryCountMap.getOrDefault(category.getId(), 0L);
                                    return CategoryDto.from(category, count);
                                });
                    });
        });
    }

    @Override
    public Mono<Long> count() {
        return categoryRepo.count();
    }
}
