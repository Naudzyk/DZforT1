package com.example.DZforT1.core.DTO;

import com.example.DZforT1.core.ENUM.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountUpdateDTO(
        Long id,
        UUID clientId,
        AccountType accountType,
        BigDecimal balance
) {
}
