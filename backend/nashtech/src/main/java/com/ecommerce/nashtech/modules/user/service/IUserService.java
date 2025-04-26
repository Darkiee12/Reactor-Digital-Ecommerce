package com.ecommerce.nashtech.modules.user.service;
import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.dto.FullUserDto;
import com.ecommerce.nashtech.modules.user.dto.UpdateUserDto;
import com.ecommerce.nashtech.modules.user.model.User;
import com.ecommerce.nashtech.shared.enums.UserFinder;

import reactor.core.publisher.Mono;

public interface IUserService {
    
    Mono<User> create(CreateUserDto dto);
    /**
     * Creates a new user without performing full validation.
     * <p>
     * This method skips standard user validation (e.g., phone number, address),
     * but still enforces unique constraints such as username, email, or UUID.
     * It is intended for internal or trusted contexts where data integrity is externally guaranteed.
     *
     * @param dto the data transfer object containing user creation info
     * @return a {@link Mono} emitting the created {@link User}, or an error if a unique constraint is violated
     */
    Mono<User> unsafeCreate(CreateUserDto dto);
    Mono<User> find(UserFinder finder);
    Mono<FullUserDto> findFullUser(UserFinder finder);
    Mono<User> update(UserFinder finder, UpdateUserDto dto);
    Mono<Void> delete(UserFinder finder);
    
}
