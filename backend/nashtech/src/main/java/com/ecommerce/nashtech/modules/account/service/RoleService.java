package com.ecommerce.nashtech.modules.account.service;

import java.time.Instant;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.account.error.RoleError;
import com.ecommerce.nashtech.modules.account.internal.repository.RoleRepository;
import com.ecommerce.nashtech.modules.account.model.Role;
import com.ecommerce.nashtech.shared.enums.RoleEnum;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class RoleService implements IRoleService {
    RoleRepository roleRepo;
    TransactionalOperator txOperator;
    R2dbcEntityTemplate template;

    @Override
    public Mono<Role> findById(long id) {
        return roleRepo.findById(id);

    }

    @Override
    public Mono<Role> create(Role role) {
        return roleRepo.existsById(role.getId())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(RoleError.DuplicateRoleError.build()))
                .flatMap(empty -> template.insert(Role.class).using(role))
                .as(txOperator::transactional);
    }

    @Override
    public Flux<Role> findByAccount(UserFinder finder) {
        return switch (finder) {
            case UserFinder.ById id -> roleRepo.findByAccountId(id.id());
            case UserFinder.ByUsername username -> roleRepo.findByAccountUsername(username.username());
            case UserFinder.ByEmail email -> roleRepo.findByAccountEmail(email.email());
            case UserFinder.ByUuid uuid -> roleRepo.findByAccountUuid(uuid.uuid());
        };
    }

    @Override
    public Mono<Void> updateRole(long roleId, long accountId) {
        long millis = Instant.now().toEpochMilli();
        return roleRepo.updateRole(roleId, accountId, millis);
    }

    @Override
    public Mono<Void> assignDefaultRole(long accountId) {
        System.out.println("Assigning default role to account with ID: " + accountId);
        var defaultRoleId = RoleEnum.UserRole.getRole().getId();
        return updateRole(defaultRoleId, accountId);
    }

}
