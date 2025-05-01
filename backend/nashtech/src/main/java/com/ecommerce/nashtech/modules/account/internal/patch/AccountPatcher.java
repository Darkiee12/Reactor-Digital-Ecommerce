package com.ecommerce.nashtech.modules.account.internal.patch;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.account.dto.UpdateAccountDto;
import com.ecommerce.nashtech.modules.account.model.Account;
import com.ecommerce.nashtech.shared.util.ReflectionPatcher;

@Service
public class AccountPatcher extends ReflectionPatcher<Account, UpdateAccountDto>{
    public AccountPatcher() {
        super(Set.of("id", "uuid", "createdAt", "updatedAt"));
    }
}
