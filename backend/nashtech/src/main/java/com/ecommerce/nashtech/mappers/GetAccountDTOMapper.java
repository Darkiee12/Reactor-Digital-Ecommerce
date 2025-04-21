package com.ecommerce.nashtech.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.dto.AccountDto;
import com.ecommerce.nashtech.models.Account;

@Service
public class GetAccountDTOMapper implements Function<Account, AccountDto> {
    @Override
    public AccountDto apply(Account account) {
        return new AccountDto(account.getUuid(), account.getUsername(), account.getEmail(), account.getCreatedAt());
    }
    
}
