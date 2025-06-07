package com.example.DZforT1.service1.service.Impl;

import com.example.DZforT1.core.DTO.TransactionCreateDTO;
import com.example.DZforT1.core.DTO.TransactionResponseDTO;
import com.example.DZforT1.service1.aop.CachedAOP.Cached;
import com.example.DZforT1.service1.aop.LogDataSourceAOP.LogDataSourceError;
import com.example.DZforT1.service1.aop.MetricAOP.Metric;
import com.example.DZforT1.service1.models.Account;
import com.example.DZforT1.service1.models.Transaction;
import com.example.DZforT1.service1.repository.AccountRepository;
import com.example.DZforT1.service1.repository.TransactionRepository;
import com.example.DZforT1.service1.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /**
     * Получить все транзакции
     */
    @Override
    @Transactional
    @Metric
    public List<TransactionResponseDTO> getTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Получить транзакцию по ID
     */
    @Override
    @Transactional
    @LogDataSourceError
    @Cached
    @Metric
    public TransactionResponseDTO getTransaction(UUID id) {
        Transaction transaction = transactionRepository.findByTransactionId(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));
        return convertToDto(transaction);
    }

    /**
     * Создать новую транзакцию
     */
    @Override
    @Transactional
    @LogDataSourceError
    public TransactionResponseDTO addTransaction(TransactionCreateDTO dto) {
        Account account = accountRepository.findByAccountId(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(dto.amount());
        transaction.setTimestamp(dto.time());

        Transaction saved = transactionRepository.save(transaction);

        return convertToDto(saved);
    }


    private TransactionResponseDTO convertToDto(Transaction transaction) {
        return new TransactionResponseDTO(
            transaction.getAccountId(),
            transaction.getAmount(),
            transaction.getTimestamp()
        );
    }
}
