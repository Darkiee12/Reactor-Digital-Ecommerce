package com.ecommerce.nashtech.modules.account.service;

import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.dto.FullAccountDto;
import com.ecommerce.nashtech.modules.account.dto.UpdateAccountDto;
import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.modules.account.internal.patch.AccountPatcher;
import com.ecommerce.nashtech.modules.account.internal.repository.AccountRepository;
import com.ecommerce.nashtech.modules.account.model.Account;
import com.ecommerce.nashtech.shared.enums.UserFinder;
import com.ecommerce.nashtech.shared.types.Option;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AccountService implements IAccountService {
    AccountRepository accountRepo;
    AccountPatcher accountPatcher;
    RoleService roleService;
    TransactionalOperator txOperator;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    R2dbcEntityTemplate template;

    @Override
    public Mono<Account> find(UserFinder find) {
        var account = switch (find) {
        case UserFinder.ById id -> accountRepo.findById(id.id());
        case UserFinder.ByUsername username -> accountRepo.findByUsername(username.username());
        case UserFinder.ByEmail email -> accountRepo.findByEmail(email.email());
        case UserFinder.ByUuid uuid -> accountRepo.findByUuid(uuid.uuid());
        };
        return account.filter(u -> !u.isDeleted())
                .switchIfEmpty(Mono.error(AccountError.AccountNotFoundError.build(Option.none())));
    }

    @Override
    public Mono<Account> create(CreateAccountDto dto) {
        return accountRepo.existsByUsername(dto.username()).filter(exists -> !exists)
                .switchIfEmpty(Mono.error(AccountError.DuplicateUsernameError.build()))
                .then(accountRepo.existsByEmail(dto.email())).filter(exists -> !exists)
                .switchIfEmpty(Mono.error(AccountError.DuplicateEmailError.build())).then(Mono.fromSupplier(() -> {
                    Account a = new Account();
                    a.setUsername(dto.username());
                    a.setEmail(dto.email());
                    a.setPassword(passwordEncoder.encode(dto.password()));
                    a.setUuid(UUID.randomUUID());
                    return a;
                })).flatMap(account -> template.insert(Account.class).using(account)
                        .flatMap(saved -> roleService.assignDefaultRole(saved.getId()).thenReturn(saved)))
                .as(txOperator::transactional);
    }

    @Override
    public Mono<Account> update(UserFinder finder, UpdateAccountDto dto) {
        return find(finder).flatMap(existing -> {
            Mono<Void> emailCheck = dto == null || !dto.email().isBlank() || dto.email().equals(existing.getEmail()) ? Mono.empty()
                    : checkEmailUnique(dto.email());
            Mono<Void> usernameCheck = dto == null || !dto.email().isBlank() || dto.username().equals(existing.getUsername()) ? Mono.empty()
                    : checkUsernameUnique(dto.username());
            return Mono.when(emailCheck, usernameCheck).thenReturn(existing);
        }).flatMap(existing -> accountPatcher.patch(existing, dto).toMono()).flatMap(patchedAccount ->{
            return accountRepo.save(patchedAccount);
        })
                .as(txOperator::transactional);
    }

    @Override
    public Mono<Void> delete(UserFinder finder) {
        return find(finder).flatMap(account -> {
            String uuid = account.getUuid().toString();
            account.setDeleted(true);
            account.setUsername("deleted_" + uuid);
            account.setEmail("deleted_" + uuid + "@deleted.com");
            return accountRepo.save(account);
        }).as(txOperator::transactional).then();
    }

    @Override
    public Mono<FullAccountDto> findFullAccount(UserFinder finder) {
        return find(finder).flatMap(account -> roleService.findByAccount(finder).collectList()
                .map(roles -> FullAccountDto.fromAccount(account, roles)));
    }

    @Override
    public Mono<Boolean> exists(UserFinder finder) {
        return switch (finder) {
        case UserFinder.ById id -> accountRepo.existsById(id.id());
        case UserFinder.ByUsername username -> accountRepo.existsByUsername(username.username());
        case UserFinder.ByEmail email -> accountRepo.existsByEmail(email.email());
        case UserFinder.ByUuid uuid -> accountRepo.existsByUuid(uuid.uuid());
        };
    }

    private Mono<Void> checkEmailUnique(String email) {
        return accountRepo.existsByEmail(email).filter(Boolean::booleanValue)
                .flatMap(dup -> Mono.error(AccountError.DuplicateEmailError.build())).then();
    }

    private Mono<Void> checkUsernameUnique(String username) {
        return accountRepo.existsByUsername(username).filter(Boolean::booleanValue)
                .flatMap(dup -> Mono.error(AccountError.DuplicateUsernameError.build())).then();
    }
}
