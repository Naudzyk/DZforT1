package com.example.DZforT1.service2.service.Impl;

import com.example.DZforT1.core.DTO.TransactionAcceptDTO;
import com.example.DZforT1.core.DTO.TransactionResultDTO;
import com.example.DZforT1.core.ENUM.TransactionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAcceptServiceImpl {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.result-topic}")
    private String resultTopic;

    @Value("${app.transaction.max-transactions}")
    private int maxTransactions;

    @Value("${app.transaction.time-window-minutes}")
    private int timeWindowMinutes;

    private final Map<String, List<LocalDateTime>> transactionHistory = new ConcurrentHashMap<>();

    @KafkaListener(topics = "${app.kafka.accept-topic}", groupId = "accept-group")
    public void handleAccept(String message) {
    TransactionAcceptDTO dto = null;
    try {
        dto = objectMapper.readValue(message, TransactionAcceptDTO.class);
        log.info("Получена транзакция для проверки: {}", dto);

        // Проверка обязательных полей
        if (dto.clientId() == null || dto.accountId() == null || dto.timestamp() == null) {
            log.warn("Транзакция содержит null-поля: {}", dto);
            sendResultOnError(dto, new IllegalArgumentException("clientId, accountId или timestamp равен null"));
            return;
        }

        String key = dto.clientId() + "_" + dto.accountId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(timeWindowMinutes);

        // Получаем или создаем список транзакций для ключа
        List<LocalDateTime> recent = transactionHistory.computeIfAbsent(key, k -> new ArrayList<>());

        // Добавляем новую транзакцию
        recent.add(dto.timestamp());

        // Удаляем старые транзакции за пределами окна
        recent.removeIf(t -> t.isBefore(windowStart));

        // Проверяем лимит транзакций
        if (recent.size() > maxTransactions) {
            log.warn("Превышен лимит транзакций для клиента {} и аккаунта {}", dto.clientId(), dto.accountId());
            sendResult(dto, TransactionStatus.BLOCKED, "Превышен лимит транзакций");
            return;
        }

        // Проверяем баланс
        if (dto.amount().compareTo(dto.accountBalance()) > 0) {
            log.warn("Недостаточно средств для транзакции {}", dto.transactionId());
            sendResult(dto, TransactionStatus.REJECTED, "Недостаточно средств");
            return;
        }

        // Транзакция одобрена
        sendResult(dto, TransactionStatus.ACCECPTED, "Транзакция одобрена");

        } catch (Exception ex) {
            log.error("Ошибка обработки транзакции", ex);
            sendResultOnError(dto, ex);
        }
    }

    private void sendResult(TransactionAcceptDTO dto, TransactionStatus status, String reason) {
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
            log.info("Результат отправлен: {}", result.status());
        } catch (Exception ex) {
            log.error("Ошибка отправки результата: {}", ex.getMessage());
        }
    }

    private void sendResultOnError(TransactionAcceptDTO dto, Exception ex) {
        try {
            TransactionResultDTO result = new TransactionResultDTO(
                dto != null ? dto.transactionId() : UUID.randomUUID(),
                dto != null ? dto.accountId() : UUID.randomUUID(),
                dto != null ? dto.clientId() : UUID.randomUUID(),
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
