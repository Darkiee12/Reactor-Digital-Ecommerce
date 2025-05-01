package com.ecommerce.nashtech.modules.user.dto;
import java.util.Set;
import java.util.UUID;

import com.ecommerce.nashtech.shared.json.JSON;

import io.swagger.v3.oas.annotations.media.Schema;
public record FullUserDto(
    @Schema(example="string") UUID uuid, 
    @Schema(example="string") String username, 
    @Schema(example="string") String fullName, 
    @Schema(example="string") String email, 
    @Schema(example="string") String gender,
    @Schema(example="string") String phoneNumber, 
    @Schema(example="string") String address, 
    @Schema(example="string") Set<String> roles,
    @Schema(example="string") String createdAt,
    @Schema(example="string") String updatedAt,
    @Schema(example="string") boolean deleted
) {
   
    @Override
    public String toString(){
        return JSON.stringify(this).unwrapOr("Error converting to JSON");
    }
}
