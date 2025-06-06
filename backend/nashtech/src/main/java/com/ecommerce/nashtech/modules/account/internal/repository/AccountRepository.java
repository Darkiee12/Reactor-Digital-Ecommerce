package com.ecommerce.nashtech.modules.account.internal.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.account.model.Account;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, Long> {

    Mono<Account> findById(long id);
    Mono<Account> findByUsername(String username);
    Mono<Account> findByUuid(UUID uuid);
    Mono<Account> findByEmail(String email);

    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<Boolean> existsById(long id);

    @Query("""
        UPDATE accounts
        SET is_deleted = true
        WHERE id = :id
    """)
    Mono<Void> setDeletedById(long id);

    @Query("""
        UPDATE accounts
        SET is_deleted = true
        WHERE uuid = :uuid
    """)
    Mono<Void> setDeletedByUuid(UUID uuid);

    @Query("""
        UPDATE accounts
        SET is_deleted = true
        WHERE username = :username
    """)
    Mono<Void> setDeletedByUsername(String username);

    @Query("""
        UPDATE accounts
        SET is_deleted = true
        WHERE email = :email
    """)
    Mono<Void> setDeletedByEmail(String email);
    
}
