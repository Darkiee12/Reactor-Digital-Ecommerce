package com.ecommerce.nashtech.modules.account.service;


import com.ecommerce.nashtech.modules.account.model.Role;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRoleService {
    
    public static final Role USER_ROLE = new Role(1L, "USER");
    public static final Role ADMIN_ROLE = new Role(2L, "ADMIN");
    public static final Role MODERATOR_ROLE = new Role(3L, "MODERATOR");

    Mono<Role> findById(long id);

    Mono<Role> create(Role role);

    Flux<Role> findByAccount(UserFinder finder);

    Mono<Void> updateRole(long roleId, long accountId);
}
