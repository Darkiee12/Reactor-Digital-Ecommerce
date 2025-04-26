package com.ecommerce.nashtech.modules.account.service;

import com.ecommerce.nashtech.modules.account.dto.CreateAccountDto;
import com.ecommerce.nashtech.modules.account.dto.FullAccountDto;
import com.ecommerce.nashtech.modules.account.dto.UpdateAccountDto;
import com.ecommerce.nashtech.modules.account.model.Account;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import reactor.core.publisher.Mono;

public interface IAccountService {
    Mono<Account> create(CreateAccountDto dto);
    Mono<Account> find(UserFinder find);
    Mono<Account> update(UserFinder finder, UpdateAccountDto dto);
    Mono<Void> delete(UserFinder find);

    Mono<FullAccountDto> findFullAccount(UserFinder finder);

    public Mono<Boolean> exists(UserFinder finder);
}
