package com.ecommerce.nashtech.bootstrap;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.model.Account;
import com.ecommerce.nashtech.modules.account.model.Role;
import com.ecommerce.nashtech.modules.account.service.IAccountService;
import com.ecommerce.nashtech.modules.account.service.IRoleService;
import com.ecommerce.nashtech.modules.account.service.RoleService;
import com.ecommerce.nashtech.security.config.SecurityConfig;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Transactional
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    IAccountService accountService;
    IRoleService roleService;
    SecurityConfig securityConfig;

    @Value("${spring.security.user.name}")
    @NonFinal String adminUsername;

    @Value("${spring.security.user.password}")
    @NonFinal String adminPassword;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        initializeRolesAndAdmin()
            .doOnSuccess(v -> System.out.println("Initialization completed successfully"))
            .doOnError(e -> System.err.println("Initialization failed: " + e.getMessage()))
            .subscribe();
    }

    private Mono<Void> initializeRolesAndAdmin() {
        return createRoles()
            .then(createDefaultAdminIfNotExists());
    }

    private Mono<Void> createRoles() {
        return Flux.fromIterable(Set.of(
                IRoleService.ADMIN_ROLE,
                IRoleService.DEFAULT_ROLE,
                IRoleService.MODERATOR_ROLE
            ))
            .flatMap(roleName -> roleService.create(roleName)
                .doOnSuccess(role -> System.out.println("Created role: " + role.getName()))
                .doOnError(e -> System.err.println("Failed to create role " + roleName + ": " + e.getMessage()))
            )
            .then();
    }

    private Mono<Void> createDefaultAdminIfNotExists() {
        var email = adminUsername + "@default.com";
        var dto = new CreateAccountDto(
            adminUsername,
            email,
            securityConfig.passwordEncoder().encode(adminPassword)
        );
        var adminRole = roleService.findByName(IRoleService.ADMIN_ROLE);
        var account = accountService.create(dto);
        return Mono.zip(adminRole, account)
            .flatMap(zipped -> {
                Role role = zipped.getT1();
                Account admin = zipped.getT2();
                return roleService.updateRole(role.getId(), admin.getId());
            })
            .onErrorResume(e -> Mono.empty());
            
            
    }
}