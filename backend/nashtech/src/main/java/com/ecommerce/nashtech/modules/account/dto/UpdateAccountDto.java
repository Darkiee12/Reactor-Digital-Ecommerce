package com.ecommerce.nashtech.modules.account.dto;

public record UpdateAccountDto(
    String username,
    String email,
    String password
) {
    
}
