package com.example.DZforT1.service1.service.Impl;


import com.example.DZforT1.core.DTO.TransactionAcceptDTO;
import com.example.DZforT1.core.DTO.TransactionRequestDTO;
import com.example.DZforT1.core.DTO.TransactionResultDTO;
import com.example.DZforT1.core.ENUM.AccountStatus;
import com.example.DZforT1.core.ENUM.TransactionStatus;
import com.example.DZforT1.service1.models.Account;
import com.example.DZforT1.service1.models.Transaction;
import com.example.DZforT1.service1.repository.AccountRepository;
import com.example.DZforT1.service1.repository.TransactionRepository;
import com.example.DZforT1.service1.service.TransactionProcesingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProcessingServiceImpl implements TransactionProcesingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.accept-topic}")
    private String acceptTopic;

    @Value("${app.kafka.result-topic}")
    private String resultTopic;

    @Transactional
    @KafkaListener(topics = "${app.kafka.transaction-topic}", groupId = "transaction-group")
    public void processTransaction(String message) {
        try {
            TransactionRequestDTO dto = objectMapper.readValue(message, TransactionRequestDTO.class);
            log.info("Получена транзакция: {}", dto);

            Optional<Account> accountOpt = accountRepository.findByAccountId(dto.accountId());

            if (accountOpt.isEmpty()) {
                log.warn("Аккаунт не найден: {}", dto.accountId());
                sendToResultTopic(dto, TransactionStatus.REJECTED, "Account not found");
                return;
            }

            Account account = accountOpt.get();
            UUID clientId = dto.clientId() != null ? dto.clientId() : account.getClientId();
            if (account.getStatus() != AccountStatus.OPEN) {
                log.warn("Аккаунт заблокирован: {}", account.getAccountId());
                sendToResultTopic(dto, TransactionStatus.BLOCKED, "Account is not OPEN");
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setClientId(clientId);
            transaction.setAccountId(dto.accountId());
            transaction.setAmount(dto.amount());
            transaction.setTimestamp(dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now());
            transaction.setStatus(TransactionStatus.REQUESTED);

            transaction = transactionRepository.save(transaction);
            log.info("Сохранена транзакция: {}", transaction.getTransactionId());

            account.setBalance(account.getBalance().add(dto.amount()));
            accountRepository.save(account);
            log.info("Баланс аккаунта обновлён: {}", account.getAccountId());

            TransactionAcceptDTO acceptDTO = new TransactionAcceptDTO(
                dto.clientId(),
                dto.accountId(),
                transaction.getTransactionId(),
                dto.timestamp(),
                dto.amount(),
                account.getBalance()
            );

            String acceptJson = objectMapper.writeValueAsString(acceptDTO);
            kafkaTemplate.send(acceptTopic, acceptJson);
            log.info("Отправлено в accept-топик: {}", acceptDTO.transactionId());

        } catch (Exception ex) {
            log.error("Ошибка обработки транзакции: {}", ex.getMessage(), ex);
            sendToResultTopicOnError(message, ex);
        }
    }


    private void sendToResultTopic(TransactionRequestDTO dto, TransactionStatus status,String reason) {
        try {
            TransactionResultDTO result = new TransactionResultDTO(
                dto.transactionId(),
                dto.accountId(),
                dto.clientId(),
                status,
                LocalDateTime.now()
            );

            String json = objectMapper.writeValueAsString(result);
            kafkaTemplate.send(resultTopic, json);
            log.info("Отправлен результат транзакции: {}", status);
        } catch (Exception ex) {
            log.error("Ошибка отправки в result-топик: {}", ex.getMessage());
        }
    }

    private void sendToResultTopicOnError(String message, Exception ex) {
        try {
            TransactionRequestDTO dto = objectMapper.readValue(message, TransactionRequestDTO.class);
            sendToResultTopic(dto, TransactionStatus.REJECTED, "Ошибка парсинга транзакции");
        } catch (Exception innerEx) {
            log.warn("Не удалось извлечь DTO из сообщения: {}", message);
            sendGenericErrorResult(message, ex);
        }
    }

    private void sendGenericErrorResult(String message, Exception ex) {
        try {
            TransactionResultDTO result = new TransactionResultDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionStatus.REJECTED,
                LocalDateTime.now()
            );

            String json = objectMapper.writeValueAsString(result);
            kafkaTemplate.send(resultTopic, json);
            log.warn("Отправлен generic результат из-за ошибки: {}", ex.getMessage());
        } catch (Exception innerEx) {
            log.error("Ошибка отправки generic результата: {}", innerEx.getMessage());
        }
    }
}
