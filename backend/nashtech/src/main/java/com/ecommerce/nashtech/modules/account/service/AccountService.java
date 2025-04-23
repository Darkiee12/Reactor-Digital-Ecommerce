package com.ecommerce.nashtech.modules.account.service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.dto.FullAccountDto;
import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.modules.account.internal.repository.AccountRepository;
import com.ecommerce.nashtech.modules.account.model.Account;
import com.ecommerce.nashtech.security.config.SecurityConfig;
import com.ecommerce.nashtech.shared.enums.UserFinder;
import com.ecommerce.nashtech.shared.types.Option;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AccountService implements IAccountService {
    AccountRepository accountRepo;
    RoleService roleService;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Override
    public Mono<Account> create(CreateAccountDto dto) {
        return accountRepo.existsByUsername(dto.username())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(AccountError.DuplicateUsernameError.build());
                }
                return accountRepo.existsByEmail(dto.email());
            })
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("Email already exists"));
                }
                Account account = new Account();
                account.setUsername(dto.username());
                account.setEmail(dto.email());
                account.setPassword(passwordEncoder.encode(dto.password()));
                account.setUuid(UUID.randomUUID());
                

                return accountRepo.save(account);
            });
    }

    @Override
    public Mono<Account> find(UserFinder find){
        return switch (find) {
            case UserFinder.ById id -> accountRepo.findById(id.id());
            case UserFinder.ByUsername username -> accountRepo.findByUsername(username.username());
            case UserFinder.ByEmail email -> accountRepo.findByEmail(email.email());
            case UserFinder.ByUuid uuid -> accountRepo.findByUuid(uuid.uuid());
        };
    }

    @Override
    public Mono<Account> update(Account account) {
        return accountRepo.save(account);
    }

    @Override
    public Mono<Void> delete(UserFinder find) {
        return switch (find) {
            case UserFinder.ById id -> accountRepo.deleteById(id.id());
            case UserFinder.ByUsername username -> accountRepo.deleteByUsername(username.username());
            case UserFinder.ByEmail email -> accountRepo.deleteByEmail(email.email());
            case UserFinder.ByUuid uuid -> accountRepo.deleteByUuid(uuid.uuid());
        };
    }

    @Override
    public Mono<FullAccountDto> findFullAccount(UserFinder finder){
        return find(finder)
            .flatMap(account -> {
                if (account == null) {
                    return Mono.error(AccountError.AccountNotFoundError.build(Option.none()));
                }

                return roleService.findByAccount(finder)
                    .collectList()
                    .map(roles -> FullAccountDto.fromAccount(account, roles));
            });
    }

    @Override
    public Mono<Boolean> exists(UserFinder finder) {
        return switch(finder){
            case UserFinder.ById id -> accountRepo.existsById(id.id());
            case UserFinder.ByUsername username -> accountRepo.existsByUsername(username.username());
            case UserFinder.ByEmail email -> accountRepo.existsByEmail(email.email());
            case UserFinder.ByUuid uuid -> accountRepo.existsByUuid(uuid.uuid());
        };
    }






}
