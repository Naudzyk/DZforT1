package com.example.DZforT1.service1.service.Impl;


import com.example.DZforT1.core.DTO.*;
import com.example.DZforT1.core.ENUM.AccountStatus;
import com.example.DZforT1.core.ENUM.ClientStatus;
import com.example.DZforT1.core.ENUM.TransactionStatus;
import com.example.DZforT1.service1.JwtService;
import com.example.DZforT1.service1.client.BlacklistCheckClient;
import com.example.DZforT1.service1.models.Account;
import com.example.DZforT1.service1.models.Client;
import com.example.DZforT1.service1.models.Transaction;
import com.example.DZforT1.service1.repository.AccountRepository;
import com.example.DZforT1.service1.repository.ClientRepository;
import com.example.DZforT1.service1.repository.TransactionRepository;
import com.example.DZforT1.service1.service.TransactionProcesingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private final BlacklistCheckClient blacklistCheckClient;
    private final JwtService jwtService;
    private final ClientRepository clientRepository;

    @Value("${app.kafka.accept-topic}")
    private String acceptTopic;

    @Value("${app.kafka.result-topic}")
    private String resultTopic;

    @Value("${app.transaction.max-rejected-transactions}")
    private int maxRejectedTransactions;

    @Value("${app.kafka.transaction-topic}")
    private String transactionTopic;

    @Transactional
    @KafkaListener(topics = "${app.kafka.transaction-topic}", groupId = "transaction-group")
    public void processTransaction(String message) {
        try {
            TransactionRequestDTO dto = objectMapper.readValue(message, TransactionRequestDTO.class);
            log.info("Получена транзакция: {}", dto);

            // Проверка, что accountId передан
            if (dto.accountId() == null) {
                log.warn("accountId равен null");
                sendToResultTopic(dto, TransactionStatus.REJECTED, "accountId равен null");
                return;
            }

            // Получение аккаунта
            Optional<Account> accountOpt = accountRepository.findByAccountId(dto.accountId());
            if (accountOpt.isEmpty()) {
                log.warn("Аккаунт не найден: {}", dto.accountId());
                sendToResultTopic(dto, TransactionStatus.REJECTED, "Аккаунт не найден");
                return;
            }

            Account account = accountOpt.get();
            Client client = account.getClient();

            // Проверка, что клиент существует
            if (client == null) {
                log.warn("Клиент не найден для аккаунта: {}", account.getAccountId());
                sendToResultTopic(dto, TransactionStatus.REJECTED, "Клиент не найден");
                return;
            }

            // Проверка, что клиент заблокирован
            if (client.getStatus() == ClientStatus.BLOCKED) {
                sendToResultTopic(dto, TransactionStatus.REJECTED, "Клиент заблокирован");
                return;
            }

            // Проверка статуса клиента
            if (client.getStatus() == ClientStatus.UNKNOWN) {
                String token = "Bearer " + jwtService.generateToken();
                BlacklistRequestDTO request = new BlacklistRequestDTO(dto.clientId(), dto.accountId());
                ResponseEntity<BlacklistResponseDTO> response = blacklistCheckClient.checkClient(token, request);

                if (response.getStatusCode().isError()) {
                    log.warn("Ошибка проверки черного списка: {}", response.getStatusCode());
                    sendToResultTopic(dto, TransactionStatus.REJECTED, "Не удалось проверить статус клиента");
                    return;
                }

                BlacklistResponseDTO blacklistResponse = response.getBody();
                if (blacklistResponse != null && blacklistResponse.isBlacklisted()) {
                    log.warn("Клиент {} в черном списке", dto.clientId());
                    blockClientAndAccount(dto, account);
                    sendToResultTopic(dto, TransactionStatus.REJECTED, "Клиент в черном списке");
                    return;
                }
            }

            // Создание транзакции
            Transaction transaction = new Transaction();
            transaction.setClientId(dto.clientId() != null ? dto.clientId() : account.getClientId());
            transaction.setAccountId(dto.accountId() != null ? dto.accountId() : account.getAccountId());
            transaction.setAmount(dto.amount());
            transaction.setTimestamp(dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now());
            transaction.setStatus(TransactionStatus.REQUESTED);
            transaction.setAccount(account);

            transaction = transactionRepository.save(transaction);
            account.setBalance(account.getBalance().add(dto.amount()));
            accountRepository.save(account);

            // Проверка, что баланс не стал отрицательным
            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                log.warn("Отрицательный баланс для транзакции {}", dto.transactionId());
                sendToResultTopic(dto, TransactionStatus.REJECTED, "Баланс аккаунта отрицательный");
                return;
            }

            // Отправка в accept-топик
            TransactionAcceptDTO acceptDTO = new TransactionAcceptDTO(
                transaction.getClientId(),
                transaction.getAccountId(),
                transaction.getTransactionId(),
                transaction.getTimestamp(),
                dto.amount(),
                account.getBalance()
            );

            String json = objectMapper.writeValueAsString(acceptDTO);
            kafkaTemplate.send(acceptTopic, json);

            // Проверка количества REJECTED транзакций
            UUID clientId = account.getClientId();
            int rejectedCount = transactionRepository.countByClientIdAndStatus(clientId, TransactionStatus.REJECTED);

            if (rejectedCount >= maxRejectedTransactions) {
                log.warn("Превышен лимит REJECTED транзакций для клиента: {}", clientId);
                account.setStatus(AccountStatus.ARRESTED);
                accountRepository.save(account);
            }

        } catch (Exception ex) {
            log.error("Ошибка обработки транзакции", ex);
            sendToResultTopicOnError(message, ex);
        }
    }

    @Transactional
    public void blockClientAndAccount(TransactionRequestDTO dto, Account account) {
        if (dto.clientId() == null || account == null) {
            log.warn("Не удалось заблокировать клиента: clientId или аккаунт равен null");
            return;
        }

        Optional<Client> clientOpt = clientRepository.findByClientId(dto.clientId());
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            client.setStatus(ClientStatus.BLOCKED);
            clientRepository.save(client);

            List<Account> accounts = client.getAccounts();
            if (accounts != null && !accounts.isEmpty()) {
                accounts.forEach(ac -> {
                    ac.setStatus(AccountStatus.BLOCKED);
                    accountRepository.save(ac);
                });
            }
        }
    }

    private void sendToResultTopic(TransactionRequestDTO dto, TransactionStatus status, String reason) {
        try {
            TransactionResultDTO result = new TransactionResultDTO(
                dto.transactionId() != null ? dto.transactionId() : UUID.randomUUID(),
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