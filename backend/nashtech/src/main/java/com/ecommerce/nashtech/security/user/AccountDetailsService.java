package com.ecommerce.nashtech.security.user;

import com.ecommerce.nashtech.modules.account.dto.FullAccountDto;
import com.ecommerce.nashtech.modules.account.internal.repository.AccountRepository;
import com.ecommerce.nashtech.modules.account.service.AccountService;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AccountDetailsService implements ReactiveUserDetailsService {
    AccountService accountService;
    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        return accountService.findFullAccount(UserFinder.ByUsername(username))
            .map(account -> (UserDetails) AccountDetails.build(account))
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with username: " + username)));
    }
}