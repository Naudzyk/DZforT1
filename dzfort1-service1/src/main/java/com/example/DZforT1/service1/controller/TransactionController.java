package com.example.DZforT1.service1.controller;

import com.example.DZforT1.core.DTO.TransactionCreateDTO;
import com.example.DZforT1.core.DTO.TransactionRequestDTO;
import com.example.DZforT1.core.DTO.TransactionResponseDTO;
import com.example.DZforT1.service1.service.TransactionProcesingService;
import com.example.DZforT1.service1.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.transaction-topic}")
    private String transactionTopic;

    /**
     * Отправляет транзакцию в Kafka для асинхронной обработки
     */
    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody TransactionRequestDTO dto) {
        try {
            // Преобразуем DTO в JSON и отправляем в Kafka
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(transactionTopic, json);

            return ResponseEntity.accepted().body("Транзакция принята на обработку");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Ошибка отправки транзакции: " + ex.getMessage());
        }
    }

    /**
     * Получает транзакцию по указанному идентификатору.
     *
     * @param id Идентификатор транзакции
     * @return ResponseEntity с найденной транзакцией в формате DTO и статусом 200 (OK),
     *         либо статусом 404 (Not Found) если транзакция не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    /**
     * Получает список всех существующих транзакций.
     *
     * @return ResponseEntity со списком транзакций в формате DTO и статусом 200 (OK),
     *         либо пустым списком если транзакции отсутствуют
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getTransactions());
    }
}