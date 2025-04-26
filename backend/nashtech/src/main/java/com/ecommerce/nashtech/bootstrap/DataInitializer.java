package com.ecommerce.nashtech.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.account.error.RoleError.DuplicateRoleError;
import com.ecommerce.nashtech.modules.account.service.IRoleService;
import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.error.UserError;
import com.ecommerce.nashtech.modules.user.service.IUserService;
import com.ecommerce.nashtech.shared.enums.RoleEnum;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    IRoleService roleService;
    IUserService userService;
    TransactionalOperator tx;

    @Value("${spring.security.user.name}")
    @NonFinal
    String adminUsername;

    @Value("${spring.security.user.password}")
    @NonFinal
    String adminPassword;

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
        return Flux.just(RoleEnum.getAllRoles())
                .concatMap(role -> roleService.create(role)
                        .onErrorResume(DuplicateRoleError.class, e -> {
                            log.warn("Role already exists: {}", role);
                            return Mono.empty();
                        }))
                .then();
    }

    private Mono<Void> ensureDefaultAdmin() {
        String email = adminUsername + "@default.com";
        var finder = new UserFinder.ByUsername(adminUsername);
        var dto = new CreateUserDto(
                adminUsername,
                email,
                adminPassword,
                "Admin",
                "Admin",
                "Admin",
                "",
                "",
                "",
                "",
                "",
                "",
                "");
        return userService.find(finder)
                .onErrorResume(UserError.UserNotFoundError.class, e -> userService.unsafeCreate(dto))
                .onErrorResume(RuntimeException.class, e -> Mono.empty())
                .flatMap(account -> roleService.updateRole(RoleEnum.AdminRole.getId(), account.getId()))
                .as(tx::transactional);
    }
}
