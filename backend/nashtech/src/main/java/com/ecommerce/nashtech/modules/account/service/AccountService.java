package com.ecommerce.nashtech.modules.account.service;

import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.dto.FullAccountDto;
import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.modules.account.internal.repository.AccountRepository;
import com.ecommerce.nashtech.modules.account.model.Account;
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
    TransactionalOperator txOperator;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    R2dbcEntityTemplate template;

    @Override
    public Mono<Account> find(UserFinder find){
        var account = switch (find) {
            case UserFinder.ById id -> accountRepo.findById(id.id());
            case UserFinder.ByUsername username -> accountRepo.findByUsername(username.username());
            case UserFinder.ByEmail email -> accountRepo.findByEmail(email.email());
            case UserFinder.ByUuid uuid -> accountRepo.findByUuid(uuid.uuid());
        };
        return account.filter(u -> !u.isDeleted()).switchIfEmpty(Mono.error(AccountError.AccountNotFoundError.build(Option.none())));
    }
    
    @Override
    public Mono<Account> create(CreateAccountDto dto) {
        return accountRepo.existsByUsername(dto.username())
            .filter(usernameExists -> !usernameExists)
            .switchIfEmpty(Mono.error(AccountError.DuplicateUsernameError.build()))
            .then(accountRepo.existsByEmail(dto.email()))
            .filter(emailExists -> !emailExists)
            .switchIfEmpty(Mono.error(AccountError.DuplicateEmailError.build()))
            .then(Mono.fromSupplier(() -> {
                Account account = new Account();
                account.setUsername(dto.username());
                account.setEmail(dto.email());
                account.setPassword(passwordEncoder.encode(dto.password()));
                account.setUuid(UUID.randomUUID());
                return account;
            }))
            .flatMap(account -> template.insert(Account.class).using(account))
            .as(txOperator::transactional);
    }

    @Override
    public Mono<Account> update(Account account) {
        return accountRepo.findById(account.getId())
            .switchIfEmpty(Mono.error(AccountError.AccountNotFoundError.build(Option.none())))
            .then(accountRepo.findByUsername(account.getUsername()))
            .filter(existing -> existing.getId() == account.getId() || existing == null)
            .switchIfEmpty(Mono.error(AccountError.DuplicateUsernameError.build()))
            .then(accountRepo.findByEmail(account.getEmail()))
            .filter(existing -> existing.getId() == account.getId() || existing == null)
            .switchIfEmpty(Mono.error(AccountError.DuplicateEmailError.build()))
            .then(accountRepo.save(account))
            .as(txOperator::transactional);
    }

    @Override
    public Mono<Void> delete(UserFinder finder) {
        return find(finder)
            .flatMap(account -> {
                account.setDeleted(true);
                account.setUsername("deleted_" + account.getUuid());
                account.setEmail("deleted_" + account.getUuid() + "@deleted.com");
                return accountRepo.save(account)
                    .then();
            })
            .as(txOperator::transactional);
    }

    @Override
    public Mono<FullAccountDto> findFullAccount(UserFinder finder){
        return find(finder)
            .flatMap(account -> roleService
                .findByAccount(finder)
                .collectList()
                .map(roles -> FullAccountDto.fromAccount(account, roles))
            );
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
