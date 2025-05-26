package com.example.DZforT1.repository;

import com.example.DZforT1.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIdAndClientId(Long accountId, Long clientId);
}
