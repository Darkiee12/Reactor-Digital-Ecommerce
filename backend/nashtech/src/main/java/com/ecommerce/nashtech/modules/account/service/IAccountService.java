package com.ecommerce.nashtech.modules.account.service;

import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.dto.FullAccountDto;
import com.ecommerce.nashtech.modules.account.model.Account;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import reactor.core.publisher.Mono;

public interface IAccountService {
    @Transactional
    Mono<Account> create(CreateAccountDto dto);
    Mono<Account> find(UserFinder find);
    @Transactional
    Mono<Account> update(Account account);
    @Transactional
    Mono<Void> delete(UserFinder find);

    Mono<FullAccountDto> findFullAccount(UserFinder finder);

    public Mono<Boolean> exists(UserFinder finder);
}
