package com.ecommerce.nashtech.services;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.models.Role;
import com.ecommerce.nashtech.repositories.RoleRepository;
import com.ecommerce.nashtech.utils.Settings;
import com.ecommerce.nashtech.utils.rust.Option;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    
    public void createDefaultRoleIfNotExits(Set<String> roles){
        roles.stream()
            .filter(role -> roleRepository.findByName(role).isEmpty())
            .map(Role::new).forEach(roleRepository::save);
    }

    public Option<Role> findByName(String name){
        return Option.fromOptional(roleRepository.findByName(name));
    }

}
