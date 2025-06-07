package com.example.DZforT1.core.DTO;


import java.util.List;
import java.util.UUID;

public record ClientResponseDTO(
    String firstName,
    String lastName,
    String middleName,
    List<AccountResponseDTO> accounts,
    UUID clientId
) {}
