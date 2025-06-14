package com.example.DZforT1.service1.service.Impl;

import com.example.DZforT1.core.DTO.TransactionResultDTO;
import com.example.DZforT1.core.ENUM.AccountStatus;
import com.example.DZforT1.core.ENUM.TransactionStatus;
import com.example.DZforT1.service1.models.Account;
import com.example.DZforT1.service1.models.Transaction;
import com.example.DZforT1.service1.repository.AccountRepository;
import com.example.DZforT1.service1.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionResultService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @KafkaListener(topics = "t1_demo_transaction_result", groupId = "result-group")
    public void handleResult(String message) {
        try {
            TransactionResultDTO dto = objectMapper.readValue(message, TransactionResultDTO.class);
            log.info("Получен результат транзакции: {}", dto);

            Optional<Transaction> transactionOpt = transactionRepository.findByTransactionId(dto.transactionId());
            if (transactionOpt.isEmpty()) {
                log.warn("Транзакция не найдена: {}", dto.transactionId());
                return;
            }

            Transaction transaction = transactionOpt.get();
            Account account = transaction.getAccount();
            BigDecimal amount = transaction.getAmount();

            switch (dto.status()) {
                case ACCECPTED:
                    transaction.setStatus(TransactionStatus.ACCECPTED);
                    break;

                case BLOCKED:
                    transaction.setStatus(TransactionStatus.BLOCKED);
                    account.setFrozenAmount(account.getFrozenAmount().add(amount));
                    account.setStatus(AccountStatus.BLOCKED);
                    break;

                case REJECTED:
                    transaction.setStatus(TransactionStatus.REJECTED);
                    account.setBalance(account.getBalance().subtract(amount));
                    break;

                default:
                    log.warn("Неизвестный статус: {}", dto.status());
                    return;
            }

            transactionRepository.save(transaction);
            accountRepository.save(account);
            log.info("Статус транзакции и аккаунта обновлены");

        } catch (Exception ex) {
            log.error("Ошибка обработки результата транзакции", ex);
        }
    }
}
