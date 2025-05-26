package com.example.DZforT1.DTO;

import com.example.DZforT1.models.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountCreateDTO(
        Long clientId,
        AccountType accountType,
        BigDecimal balance

) {}
