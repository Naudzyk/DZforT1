package com.example.DZforT1.core.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCreateDTO(
        UUID clientid,
        UUID accountId,
        BigDecimal amount,
        LocalDateTime time
) {
}
