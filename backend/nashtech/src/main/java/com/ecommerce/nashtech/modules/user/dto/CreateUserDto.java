package com.ecommerce.nashtech.modules.user.dto;

import jakarta.validation.constraints.NotBlank;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.internal.validation.PasswordValidation;
import com.ecommerce.nashtech.modules.account.internal.validation.UsernameValidation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
        @NotBlank(message = "Username must not be blank") @Size(min = UsernameValidation.MIN_LENGTH, max = UsernameValidation.MAX_LENGTH, message = "Username must be between 3 and 32 characters") String username,
        @Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") @Size(min = PasswordValidation.MIN_LENGTH, max = PasswordValidation.MAX_LENGTH, message = "Password must be between 8 and 32 characters") String password,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName, String middleName,

        @NotBlank(message = "Gender is required") @Size(min = 1, max = 10) String gender,

        @NotBlank(message = "Phone number region code is required") @Size(min = 2, max = 5, message = "Phone number region code must be between 2 and 5 characters") String phoneNumberRegionCode,
        @Size(max = 20, message = "Phone number must be less than 20 characters") @NotBlank(message = "Phone number is required") String phoneNumber,

        String address,

        String city,

        String state,

        String country

) {

    public CreateAccountDto toAccountDto() {
        return new CreateAccountDto(username, email, password);
    }
}
