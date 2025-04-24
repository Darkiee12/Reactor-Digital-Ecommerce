package com.ecommerce.nashtech.modules.user.dto;

import com.ecommerce.nashtech.modules.account.internal.validation.PasswordValidation;
import com.ecommerce.nashtech.modules.account.internal.validation.UsernameValidation;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @Size(min = UsernameValidation.MIN_LENGTH, max = UsernameValidation.MAX_LENGTH, message = "Username must be between 3 and 32 characters") 
        @Nullable String username,

        @Email(message = "Email should be valid") 
        @Nullable String email,

        @Size(min = PasswordValidation.MIN_LENGTH, max = PasswordValidation.MAX_LENGTH, message = "Password must be between 8 and 32 characters") 
        @Nullable String password,

        @Nullable String firstName,

        @Nullable String lastName, 
        
        @Nullable String middleName,

        @Size(min = 1, max = 10) 
        @Nullable String gender,

        @Size(min = 2, max = 5, message = "Phone number region code must be between 2 and 5 characters") 
        @Nullable String phoneNumberRegionCode,

        @Size(max = 20, message = "Phone number must be less than 20 characters") 
        @Nullable String phoneNumber,

        @Nullable String address,

        @Nullable String city,

        @Nullable String state,

        @Nullable String country
){}
