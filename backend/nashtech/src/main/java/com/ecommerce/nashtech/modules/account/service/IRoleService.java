package com.ecommerce.nashtech.modules.account.service;

import com.ecommerce.nashtech.modules.account.model.Role;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRoleService {
    Mono<Role> findById(long id);

    Mono<Role> create(Role role);

    Flux<Role> findByAccount(UserFinder finder);

    Mono<Void> updateRole(long roleId, long accountId);

    Mono<Void> assignDefaultRole(long accountId);
}
