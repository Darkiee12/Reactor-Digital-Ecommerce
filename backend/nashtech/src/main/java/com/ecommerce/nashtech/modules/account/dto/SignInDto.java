package com.ecommerce.nashtech.modules.account.dto;

import com.ecommerce.nashtech.modules.account.internal.validation.PasswordValidation;
import com.ecommerce.nashtech.modules.account.internal.validation.UsernameValidation;
import com.ecommerce.nashtech.shared.json.JSON;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record SignInDto(
        @NotBlank(message = "Username must not be blank") 
        @Size(min = UsernameValidation.MIN_LENGTH, max = UsernameValidation.MAX_LENGTH, message = "Username must be between 3 and 32 characters") 
        String username,
        @NotBlank(message = "Password is required") 
        @Size(min = PasswordValidation.MIN_LENGTH, max = PasswordValidation.MAX_LENGTH, message = "Password must be between 8 and 32 characters") 
        String password) {
    @Override
    public String toString() {
        return JSON.stringify(this).unwrapOr("Error converting to JSON");
    }
}
