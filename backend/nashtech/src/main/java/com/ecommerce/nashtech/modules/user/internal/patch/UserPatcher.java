package com.ecommerce.nashtech.modules.user.internal.patch;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.ecommerce.nashtech.modules.user.dto.UpdateUserDto;
import com.ecommerce.nashtech.modules.user.model.User;
import com.ecommerce.nashtech.shared.util.ReflectionPatcher;

@Service
public class UserPatcher extends ReflectionPatcher<User, UpdateUserDto> {
    public UserPatcher() {
        super(Set.of("id", "uuid", "createdAt", "updatedAt"));
    }
    
    
}
