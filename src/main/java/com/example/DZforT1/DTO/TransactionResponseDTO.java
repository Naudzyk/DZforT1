package com.example.DZforT1.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id, // Уникальный ID транзакции
        Long accountId, // Ссылка на Account.id
        BigDecimal amount, // Сумма транзакции
        LocalDateTime timestamp // Время транзакции
) {}
