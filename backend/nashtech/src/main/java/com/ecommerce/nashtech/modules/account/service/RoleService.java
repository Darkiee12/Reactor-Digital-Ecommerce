package com.ecommerce.nashtech.modules.account.service;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.account.internal.repository.RoleRepository;
import com.ecommerce.nashtech.modules.account.model.Role;
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

    @Override
    public Mono<Role> findByName(String name) {
        return roleRepo.findByName(name)
            .switchIfEmpty(Mono.error(new RuntimeException("Role not found")));

    }

    @Override
    public Mono<Role> create(String roleName) {
        return roleRepo.existsByName(roleName)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("Role already exists"));
                }
                Role role = new Role();
                role.setName(roleName);
                return roleRepo.save(role);
            });
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
        return roleRepo.updateRole(roleId, accountId);
    }


    


}
