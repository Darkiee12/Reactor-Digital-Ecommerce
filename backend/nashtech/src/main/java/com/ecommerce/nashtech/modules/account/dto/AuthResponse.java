package com.ecommerce.nashtech.modules.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("accessToken") String accessToken,
        @JsonProperty("message") String message) {
}