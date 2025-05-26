package com.example.DZforT1.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCreateDTO(
        Long accountId,
        BigDecimal amount,
        LocalDateTime time
) {
}
