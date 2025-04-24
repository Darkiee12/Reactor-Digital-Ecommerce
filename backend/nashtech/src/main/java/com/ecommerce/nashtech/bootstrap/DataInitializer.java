package com.ecommerce.nashtech.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.error.RoleError.DuplicateRoleError;
import com.ecommerce.nashtech.modules.account.service.IAccountService;
import com.ecommerce.nashtech.modules.account.service.IRoleService;
import com.ecommerce.nashtech.security.config.SecurityConfig;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final IRoleService roleService;
    private final IAccountService accountService;
    private final SecurityConfig securityConfig;
    private final TransactionalOperator tx;

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent e) {
        initialize()
            .as(tx::transactional) 
            .doOnSuccess(v -> log.info("Initialization complete"))
            .doOnError(err -> log.error("Initialization failed", err))
            .subscribe();
    }

    private Mono<Void> initialize() {
        return ensureRoles()
                .then(ensureDefaultAdmin());
    }

    private Mono<Void> ensureRoles() {
        return Flux.just(IRoleService.USER_ROLE, IRoleService.ADMIN_ROLE, IRoleService.MODERATOR_ROLE)
                .concatMap(role ->
                    roleService.create(role)
                        .onErrorResume(DuplicateRoleError.class, e -> {
                            log.warn("Role already exists: {}", role);
                            return Mono.empty();
                        })
                )
                .then();
    }

    private Mono<Void> ensureDefaultAdmin() {
        String email = adminUsername + "@default.com";
        var finder = new UserFinder.ByUsername(adminUsername);
        var dto = new CreateAccountDto(
                adminUsername,
                email,
                securityConfig.passwordEncoder().encode(adminPassword)
        );

        return accountService.find(finder)
                .switchIfEmpty(accountService.create(dto))
                .flatMap(account -> roleService.updateRole(IRoleService.ADMIN_ROLE.getId(), account.getId()))
                .as(tx::transactional);
    }
}
