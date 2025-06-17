package com.example.DZforT1.service1.repository;

import com.example.DZforT1.core.ENUM.TransactionStatus;
import com.example.DZforT1.service1.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByClientIdAndAccountIdAndTimestampAfter(UUID clientId, UUID accountId, LocalDateTime timestamp);
    Optional<Transaction> findByTransactionId(UUID transactionId);
    int countByClientIdAndStatus(UUID clientId, TransactionStatus status);
}
