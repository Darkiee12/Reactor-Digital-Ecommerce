package com.ecommerce.nashtech.modules.user.internal.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.user.model.User;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    Mono<User> findById(long id);

    @Query("""
        SELECT u.* 
        FROM users AS u
        INNER JOIN accounts AS a
        ON u.id = a.id
        WHERE a.uuid = :uuid
    """)
    Mono<User> findByUuid(UUID uuid);

    @Query("""
        SELECT u.* 
        FROM users AS u
        INNER JOIN accounts AS a
        ON u.id = a.id 
        WHERE a.email = :email
    """)
    Mono<User> findByEmail(String email);

    @Query("""
        SELECT * 
        FROM users AS u
        INNER JOIN accounts AS a
        ON u.id = a.id
        WHERE a.username = :username
    """)
    Mono<User> findByUsername(String username);

    @Query("""
        SELECT EXISTS (
          SELECT 1
            FROM users AS u
            JOIN accounts a ON u.id = a.id
           WHERE a.username = :username
        )
    """)
    Mono<Boolean> existsByUsername(String username);

    @Query("""
        SELECT EXISTS (
          SELECT 1
            FROM users  u
            JOIN accounts a ON u.id = a.id
           WHERE a.email = :email
        )
    """)
    Mono<Boolean> existsByEmail(String email);

    @Query("""
        UPDATE users
        SET is_deleted = true
        WHERE id = :id
    """)
    Mono<Void> setDeletedById(long id);

    @Query("""
        UPDATE users
        SET is_deleted = true
        WHERE uuid = :uuid
    """)
    Mono<Void> setDeletedByUuid(UUID uuid);

    @Query("""
        UPDATE users
        SET is_deleted = true
        WHERE username = :username
    """)
    Mono<Void> setDeletedByUsername(String username);

    @Query("""
        UPDATE users
        SET is_deleted = true
        WHERE email = :email
    """)
    Mono<Void> setDeletedByEmail(String email);
}
