package com.example.DZforT1.core.DTO;


import com.example.DZforT1.core.ENUM.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDTO(
    UUID accountId,
    AccountStatus status,
    BigDecimal balance,
    BigDecimal frozenAmount
) {}
