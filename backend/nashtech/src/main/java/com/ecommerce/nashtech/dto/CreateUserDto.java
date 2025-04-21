package com.ecommerce.nashtech.dto;

import com.ecommerce.nashtech.models.User;
import com.ecommerce.nashtech.utils.Settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
    @NotBlank(message = "Username must not be blank")
    @Size(min = Settings.Username.MIN_LENGTH, max = Settings.Username.MAX_LENGTH, message = "Username must be between 3 and 32 characters")
    String username, 
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    @NotBlank(message = "Password is required")
    @Size(min = Settings.Password.MIN_LENGTH, max = Settings.Password.MAX_LENGTH, message = "Password must be between 8 and 32 characters")
    String password,
    @NotBlank(message = "First name is required")
    String firstName,
    @NotBlank(message = "Last name is required")
    String lastName,
    String middleName,

    @NotBlank(message = "Gender is required")
    @Size(min = 1, max = 10)
    String gender,

    @NotBlank(message = "Phone number region code is required")
    @Size(min = 2, max = 5, message = "Phone number region code must be between 2 and 5 characters")
    String phoneNumberRegionCode,
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    @NotBlank(message = "Phone number is required")
    String phoneNumber,

    String address,

    String city,

    String state,

    String country


) {
    public CreateAccountDTO toCreateAccountDTO() {
        return new CreateAccountDTO(username, email, password);
    }
    /*
     * * Convert this DTO to a partial User entity
     * * @return a User entity with only the fields that are needed to create a new user
     * 
     */
    public User toPartialUser() {
        var user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName);
        user.setGender(gender);
        return user;
    }
}
