package com.ecommerce.nashtech.modules.brand.dto;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public record CreateBrandDto(String name) {
}
