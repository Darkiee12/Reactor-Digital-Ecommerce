package com.ecommerce.nashtech.modules.product.internal.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.category.model.Category;
import com.ecommerce.nashtech.modules.product.model.Product;
import com.ecommerce.nashtech.modules.product.model.ProductCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductCategoryRepository extends R2dbcRepository<ProductCategory, Void> {

    Flux<ProductCategory> findByProductId(Long productId);

    Flux<ProductCategory> findByCategoryId(Long categoryId);

    @Query("""
                    SELECT c.*
                    FROM categories c
                    INNER JOIN product_category pc ON c.id = pc.category_id
                    WHERE pc.product_id = :productId
            """)
    Flux<Category> findAllCategoriesByProductId(Long productId);

    @Query("""
                    SELECT c.*
                    FROM categories c
                    INNER JOIN product_category pc ON c.id = pc.category_id
                    INNER JOIN products p ON p.id = pc.product_id
                    WHERE p.uuid = :productUuid
            """)
    Flux<Category> findAllCategoriesByProductUuid(UUID productUuid);

    @Query("""
                    SELECT p.*
                    FROM products p
                    INNER JOIN product_category pc ON p.id = pc.product_id
                    WHERE pc.category_id = :categoryId
                    LIMIT :limit OFFSET :offset
            """)
    Flux<Product> findAllProductsByCategoryId(Long categoryId, int limit, int offset);

    @Query("""
                    SELECT COUNT(*)
                    FROM product_category pc
                    WHERE pc.category_id = :categoryId
            """)
    Mono<Long> countByCategoryId(Long categoryId);

    Mono<Void> deleteByProductIdAndCategoryId(Long productId, Long categoryId);
}
