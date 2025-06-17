package com.example.DZforT1.core.DTO;

import com.example.DZforT1.core.ENUM.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResultDTO(
    UUID transactionId,
    UUID accountId,
    UUID clientId,
    TransactionStatus status,
    LocalDateTime timestamp
) {}
