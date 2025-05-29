package com.example.DZforT1.DTO;

import com.example.DZforT1.models.Account;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ClientResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String middleName,
    List<AccountResponseDTO> accounts,
    UUID clientId
) {}
