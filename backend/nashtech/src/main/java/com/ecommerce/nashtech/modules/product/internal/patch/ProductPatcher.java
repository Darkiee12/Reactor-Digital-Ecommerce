package com.ecommerce.nashtech.modules.product.internal.patch;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.product.dto.UpdateProductDto;
import com.ecommerce.nashtech.modules.product.model.Product;
import com.ecommerce.nashtech.shared.util.ReflectionPatcher;

@Service
public class ProductPatcher extends ReflectionPatcher<Product, UpdateProductDto> {
    public ProductPatcher() {
        super(Set.of("id", "uuid", "createdAt", "updatedAt"));
    }
}
