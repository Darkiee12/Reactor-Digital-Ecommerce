package com.ecommerce.nashtech.dto;
import java.util.Collection;
import java.util.UUID;

import com.ecommerce.nashtech.models.Role;
import com.ecommerce.nashtech.utils.JSON;

import io.swagger.v3.oas.annotations.media.Schema;
public record UserDto(
    @Schema(example="string") UUID uuid, 
    @Schema(example="string") String username, 
    @Schema(example="string") String fullName, 
    @Schema(example="string") String email, 
    @Schema(example="string") String gender,
    @Schema(example="string") String phoneNummber, 
    @Schema(example="string") String address, 
    @Schema(example="[]") Collection<Role> roles, 
    @Schema(example="string") long createdAt 
) {
   
    @Override
    public String toString(){
        return JSON.stringify(this).unwrapOr("Error converting to JSON");
    }
}
