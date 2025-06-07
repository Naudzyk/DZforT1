package com.example.DZforT1.service1.controller;

import com.example.DZforT1.core.DTO.TransactionCreateDTO;
import com.example.DZforT1.core.DTO.TransactionResponseDTO;
import com.example.DZforT1.service1.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Создает новую транзакцию на основе полученных данных.
     *
     * @param dto DTO-объект с данными для создания транзакции
     * @return ResponseEntity с созданной транзакцией в формате DTO и статусом 201 (Created)
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionCreateDTO dto) {
        TransactionResponseDTO created = transactionService.addTransaction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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