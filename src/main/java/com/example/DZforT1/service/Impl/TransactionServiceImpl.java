package com.example.DZforT1.service.Impl;

import com.example.DZforT1.DTO.TransactionCreateDTO;
import com.example.DZforT1.DTO.TransactionResponseDTO;
import com.example.DZforT1.aop.LogDataSourceError;
import com.example.DZforT1.models.Account;
import com.example.DZforT1.models.Transaction;
import com.example.DZforT1.repository.AccountRepository;
import com.example.DZforT1.repository.TransactionRepository;
import com.example.DZforT1.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public TransactionResponseDTO getTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
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
        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(dto.amount());
        transaction.setTime(dto.time());

        Transaction saved = transactionRepository.save(transaction);

        return convertToDto(saved);
    }


    private TransactionResponseDTO convertToDto(Transaction transaction) {
        return new TransactionResponseDTO(
            transaction.getId(),
            transaction.getAccount().getId(),
            transaction.getAmount(),
            transaction.getTime()
        );
    }
}
