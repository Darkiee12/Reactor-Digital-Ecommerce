package com.ecommerce.nashtech.modules.account.dto;

import com.ecommerce.nashtech.modules.account.internal.validation.PasswordValidation;
import com.ecommerce.nashtech.modules.account.internal.validation.UsernameValidation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountDto(
    @NotBlank(message = "Username must not be blank") @Size(min = UsernameValidation.MIN_LENGTH, max = UsernameValidation.MAX_LENGTH, message = "Username must be between 3 and 32 characters") 
    String username,
    @Email
    @NotBlank(message = "Email must not be blank")
    String email,

    @NotBlank(message = "Password must not be blank")
    @Size(min = PasswordValidation.MIN_LENGTH, max = PasswordValidation.MAX_LENGTH, message = "Password must be between 8 and 32 characters")
    String password
) {
    
}
