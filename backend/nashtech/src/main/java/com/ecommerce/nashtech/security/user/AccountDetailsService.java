package com.ecommerce.nashtech.security.user;
import com.ecommerce.nashtech.models.Account;
import com.ecommerce.nashtech.repositories.AccountRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;
    @Override
    public AccountDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username).orElseGet(() -> {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        });
        return AccountDetails.build(account);
    }
}