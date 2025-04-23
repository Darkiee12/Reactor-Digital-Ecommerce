package com.ecommerce.nashtech.modules.user.internal.types;

public record RawAddress(
    String address, 
    String city, 
    String state, 
    String country
) {}
