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

            String key = dto.clientId() + "_" + dto.accountId();

            List<LocalDateTime> recent = transactionHistory.getOrDefault(key, new ArrayList<>());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime windowStart = now.minusMinutes(timeWindowMinutes);

            recent.removeIf(t -> t.isBefore(windowStart));

            if (recent.size() >= maxTransactions) {
                log.warn("Превышен лимит транзакций для клиента {}", dto.clientId());
                sendResult(dto, TransactionStatus.BLOCKED, "Превышен лимит транзакций");
                return;
            }

            if (dto.amount().compareTo(dto.accountBalance()) > 0) {
                log.warn("Недостаточно средств для транзакции {}", dto.transactionId());
                sendResult(dto, TransactionStatus.REJECTED, "Недостаточно средств");
                return;
            }

            sendResult(dto, TransactionStatus.ACCECPTED, "Транзакция одобрена");

        } catch (Exception ex) {
            log.error("Ошибка обработки транзакции: {}", ex.getMessage());
            sendResult(dto, TransactionStatus.REJECTED, ex.getMessage());
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
}
