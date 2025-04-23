package com.ecommerce.nashtech.modules.user.internal.types;

public record RawPhoneNumber(
    String phoneNumber, 
    String regionCode
) {
    
    @Override
    public String toString() {
        return regionCode + phoneNumber;
    }
}
