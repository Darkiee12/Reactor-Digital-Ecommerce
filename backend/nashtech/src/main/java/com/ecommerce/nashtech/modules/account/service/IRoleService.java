package com.ecommerce.nashtech.modules.account.service;

import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.nashtech.modules.account.model.Role;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRoleService {
    
    public static final String DEFAULT_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String MODERATOR_ROLE = "MODERATOR";

    Mono<Role> findByName(String name);

    @Transactional
    Mono<Role> create(String roleName);

    Flux<Role> findByAccount(UserFinder finder);

    @Transactional
    Mono<Void> updateRole(long roleId, long accountId);
}
