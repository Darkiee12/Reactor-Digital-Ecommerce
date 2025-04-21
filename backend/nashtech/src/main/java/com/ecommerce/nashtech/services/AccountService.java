package com.ecommerce.nashtech.services;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.nashtech.dto.AccountDto;
import com.ecommerce.nashtech.dto.CreateAccountDTO;
import com.ecommerce.nashtech.errors.AccountError;
import com.ecommerce.nashtech.mappers.GetAccountDTOMapper;
import com.ecommerce.nashtech.models.Account;
import com.ecommerce.nashtech.repositories.AccountRepository;
import com.ecommerce.nashtech.security.config.Config;
import com.ecommerce.nashtech.utils.Settings;
import com.ecommerce.nashtech.utils.rust.Option;
import com.ecommerce.nashtech.utils.rust.Result;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepositories;
    private final GetAccountDTOMapper getAccountDTOMapper;
    private final RoleService roleService;


    public boolean usernameExists(String username) {
        return accountRepositories.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return accountRepositories.existsByEmail(email);
    }

    public boolean accountExists(int id) {
        return accountRepositories.existsById(id);
    }

    @Transactional
    public Result<Account, AccountError> create(CreateAccountDTO dto) {
        if(accountRepositories.existsByEmail(dto.email())){
            return Result.err(AccountError.DuplicateEmailError.build());
        }
        if(accountRepositories.existsByUsername(dto.username())){
            return Result.err(AccountError.DuplicateUsernameError.build());
        }
        Account account = new Account();
        account.setUsername(dto.username());
        account.setEmail(dto.email());
        account.setPassword(Config.passwordEncoder.encode(dto.password()));
        account.setRoles(Set.of(roleService.findByName(Settings.Roles.DEFAUL_ROLE).unwrap()));
        return Result.ok(accountRepositories.save(account));
    }

    public Page<AccountDto> getAll(Pageable pageable) {
        var accounts = accountRepositories.findAll(pageable);
        return accounts.map(getAccountDTOMapper);
    }

    public Option<AccountDto> getById(int id) {
        return Option.fromOptional(accountRepositories.findById(id).map(getAccountDTOMapper));
    }

    public Option<AccountDto> getByUsername(String username) {
        return Option.fromOptional(accountRepositories.findByUsername(username).map(getAccountDTOMapper));
    }

    public Option<AccountDto> getByEmail(String email) {
        return Option.fromOptional(accountRepositories.findByEmail(email).map(getAccountDTOMapper));
    }

    public Option<AccountDto> getByUuid(UUID uuid) {
        return Option.fromOptional(accountRepositories.findByUuid(uuid).map(getAccountDTOMapper));
    }
}
