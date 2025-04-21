package com.ecommerce.nashtech.dto;

import com.ecommerce.nashtech.utils.JSON;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInDTO(
        @NotBlank(message = "Username must not be blank") @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters") String username,
        @NotBlank(message = "Password is required") @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters") String password) {
    @Override
    public String toString() {
        return JSON.stringify(this).unwrapOr("Error converting to JSON");
    }
}
