package com.example.DZforT1.core.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionAcceptDTO(
    UUID clientId,
    UUID accountId,
    UUID transactionId,
    LocalDateTime timestamp,
    BigDecimal amount,
    BigDecimal accountBalance
) {}
