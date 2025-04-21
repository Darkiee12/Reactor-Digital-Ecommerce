package com.ecommerce.nashtech.data;

import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.nashtech.models.Account;
import com.ecommerce.nashtech.models.Role;
import com.ecommerce.nashtech.repositories.AccountRepository;
import com.ecommerce.nashtech.repositories.RoleRepository;
import com.ecommerce.nashtech.security.config.Config;
import com.ecommerce.nashtech.services.RoleService;
import com.ecommerce.nashtech.utils.Settings;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitalizer implements ApplicationListener<ApplicationReadyEvent>{
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event){
        Set<String> defaultRoles = Set.of(Settings.Roles.DEFAUL_ROLE, Settings.Roles.ADMIN_ROLE, Settings.Roles.MODERATOR_ROLE);
        roleService.createDefaultRoleIfNotExits(defaultRoles);
        createDefaultAdminIfNotExists();
    }   


    @Value("${spring.security.user.name}")
    private String adminUsername;
    @Value("${spring.security.user.password}")
    private String adminPassword;
    private void createDefaultAdminIfNotExists(){
        Role adminRole = roleRepository.findByName("ADMIN").get();
        if(!accountRepository.existsByUsername(adminUsername)){
            Account account = new Account();
            account.setUsername(adminUsername);
            account.setPassword(Config.passwordEncoder.encode(adminPassword));
            account.setEmail(adminUsername+"@ecommerce.com");
            account.setRoles(Set.of(adminRole));
            accountRepository.save(account);
        }
    }
}
