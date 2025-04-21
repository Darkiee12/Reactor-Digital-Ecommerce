package com.ecommerce.nashtech.repositories;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.ecommerce.nashtech.models.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUuid(UUID uuid);
    Optional<Account> findByUsernameAndEmail(String username, String email);
    @NonNull
    Page<Account> findAll(@NonNull Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(int id);
}
