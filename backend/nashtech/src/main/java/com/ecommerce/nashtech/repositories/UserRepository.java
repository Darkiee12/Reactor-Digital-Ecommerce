package com.ecommerce.nashtech.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByAccountUuid(UUID uuid);

    Optional<User> findByAccountUsername(String username);

    
}
