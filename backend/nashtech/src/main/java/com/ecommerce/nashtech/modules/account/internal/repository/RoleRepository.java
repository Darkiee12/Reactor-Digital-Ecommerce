package com.ecommerce.nashtech.modules.account.internal.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.ecommerce.nashtech.modules.account.model.Role;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository extends R2dbcRepository<Role, Long> {
    
    Mono<Role> findById(long id);
    Mono<Void> deleteById(long id);
    Mono<Boolean> existsById(long id);

    @Query("""
        SELECT r.* 
        FROM roles AS r
        INNER JOIN account_role AS ar
        ON rs.id = ar.role_id
        WHERE ar.account_id = :accountId      
    """)
    Flux<Role> findByAccountId(long accountId);

    @Query("""
        SELECT r.* 
        FROM roles AS r
        INNER JOIN account_role AS ar ON r.id = ar.role_id
        INNER JOIN accounts AS a ON ar.account_id = a.id
        WHERE a.uuid = :uuid  
    """)
    Flux<Role> findByAccountUuid(UUID uuid);
    

    @Query("""
        SELECT r.* 
        FROM roles AS r
        INNER JOIN account_role AS ar ON r.id = ar.role_id
        INNER JOIN accounts AS a ON ar.account_id = a.id
        WHERE a.username = :username;  
    """)
    Flux<Role> findByAccountUsername(String username);

    @Query("""
        SELECT r.* 
        FROM roles AS r
        INNER JOIN account_role AS ar ON r.id = ar.role_id
        INNER JOIN accounts AS a ON ar.account_id = a.id
        WHERE a.email = :email;
    """)
    Flux<Role> findByAccountEmail(String email);

    @Modifying
    @Query("""
        INSERT INTO account_role (account_id, role_id, assigned_at)
        VALUES (:accountId, :roleId, :assignedAt)
        ON CONFLICT (account_id, role_id) DO NOTHING
    """)
    Mono<Void> updateRole(long roleId, long accountId, long assignedAt);  
    
    @Query("""
       INSERT INTO roles (id, name)
       VALUES (:id, :name)
       ON CONFLICT (id) DO NOTHING
       RETURNING *     
    """)
    Mono<Role> insertRole(long id, String name);

}

