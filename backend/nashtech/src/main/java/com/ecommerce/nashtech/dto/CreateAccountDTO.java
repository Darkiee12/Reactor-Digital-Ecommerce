package com.ecommerce.nashtech.dto;

import com.ecommerce.nashtech.utils.Settings;
import com.ecommerce.nashtech.utils.JSON;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountDTO(
    @NotBlank(message = "Username must not be blank")
    @Size(min = Settings.Username.MIN_LENGTH, max = Settings.Username.MAX_LENGTH, message = "Username must be between 3 and 32 characters")
    String username, 
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    @NotBlank(message = "Password is required")
    @Size(min = Settings.Password.MIN_LENGTH, max = Settings.Password.MAX_LENGTH, message = "Password must be between 8 and 32 characters")
    String password
    ) {
    

    @Override
    public String toString() {
        return JSON.stringify(this).unwrapOr("Error converting to JSON");
    }
}