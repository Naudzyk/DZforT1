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

    @Transactional
    @KafkaListener(topics = "${app.kafka.transaction-topic}", groupId = "transaction-group")
    public void processTransaction(String message) {
        try {
            TransactionRequestDTO dto = objectMapper.readValue(message, TransactionRequestDTO.class);
            log.info("Получена транзакция: {}", dto);

            Optional<Account> accountOpt = accountRepository.findByAccountId(dto.accountId());
            if (accountOpt.isEmpty()) {
                sendToResultTopic(dto, TransactionStatus.REJECTED, "Account not found");
                return;
            }

            Account account = accountOpt.get();
            Client client = account.getClient();

            if (client.getStatus() == ClientStatus.UNKNOWN) {
                log.info("Статус клиента неизвестен. Отправляем запрос в сервис 2...");
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

            Transaction transaction = new Transaction();
            transaction.setClientId(dto.clientId());
            transaction.setAccountId(dto.accountId());
            transaction.setAmount(dto.amount());
            transaction.setTimestamp(dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now());
            transaction.setStatus(TransactionStatus.REQUESTED);
            transaction.setAccount(account);

            transaction = transactionRepository.save(transaction);
            account.setBalance(account.getBalance().add(dto.amount()));
            accountRepository.save(account);

            TransactionAcceptDTO acceptDTO = new TransactionAcceptDTO(
                dto.clientId(),
                dto.accountId(),
                transaction.getTransactionId(),
                transaction.getTimestamp(),
                dto.amount(),
                account.getBalance()
            );

            String json = objectMapper.writeValueAsString(acceptDTO);
            kafkaTemplate.send(acceptTopic, json);

        } catch (Exception ex) {
            log.error("Ошибка обработки транзакции", ex);
            sendToResultTopicOnError(message, ex);
        }
    }
    @Transactional
    public void blockClientAndAccount(TransactionRequestDTO dto, Account account) {
       Optional<Client> clientOpt = clientRepository.findByClientId(dto.clientId());
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            client.setStatus(ClientStatus.BLOCKED);
            clientRepository.save(client);

            List<Account> accounts = client.getAccounts();
            for (Account ac : accounts) {
                if (accounts != null) {
                    ac.setStatus(AccountStatus.BLOCKED);
                    accountRepository.save(account);
                }
            }
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
