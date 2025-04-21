package com.ecommerce.nashtech.dto;

import java.util.UUID;

import com.ecommerce.nashtech.utils.JSON;

import io.swagger.v3.oas.annotations.media.Schema;

public record AccountDto(
    @Schema(example="string") UUID uuid, 
    @Schema(example="string") String username, 
    @Schema(example="string") String email, 
    @Schema(example="i64") long createdAt) {
    @Override
    public String toString() {
        return JSON.stringify(this).unwrapOr("Error converting to JSON");
    }
}
