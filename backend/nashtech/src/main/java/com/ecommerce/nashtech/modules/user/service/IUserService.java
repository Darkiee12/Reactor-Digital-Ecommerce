package com.ecommerce.nashtech.modules.user.service;

import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.model.User;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import reactor.core.publisher.Mono;

public interface IUserService {
    
    Mono<User> create(CreateUserDto dto);
    Mono<User> find(UserFinder finder);
    Mono<User> update(CreateUserDto dto);
    Mono<Void> delete(UserFinder finder);
    
}
