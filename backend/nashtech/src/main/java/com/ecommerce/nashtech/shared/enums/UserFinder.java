package com.ecommerce.nashtech.shared.enums;

import java.util.UUID;

public sealed interface UserFinder permits 
    UserFinder.ById,
    UserFinder.ByUsername,
    UserFinder.ByEmail,
    UserFinder.ByUuid 
{
    public final record ById(long id) implements UserFinder {}
    public final record ByUsername(String username) implements UserFinder {}
    public final record ByEmail(String email) implements UserFinder {}
    public final record ByUuid(UUID uuid) implements UserFinder {}
    public static UserFinder ByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ByUsername'");
    }
}
