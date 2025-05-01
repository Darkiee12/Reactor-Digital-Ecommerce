package com.ecommerce.nashtech.modules.account.dto;

import java.util.Collection;
import java.util.UUID;

import com.ecommerce.nashtech.modules.account.model.Account;
import com.ecommerce.nashtech.modules.account.model.Role;

public record FullAccountDto(
        String username,
        String password,
        String email,
        UUID uuid,
        Collection<Role> roles) {

    public static FullAccountDto fromAccount(Account account, Collection<Role> roles) {
        return new FullAccountDto(
                account.getUsername(),
                account.getPassword(),
                account.getEmail(),
                account.getUuid(),
                roles);
    }

    public String[] getRoles() {
        return roles.stream()
                .map(Role::getName)
                .toArray(String[]::new);
    }
}
