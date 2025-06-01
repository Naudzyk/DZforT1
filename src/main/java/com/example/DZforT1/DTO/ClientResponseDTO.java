package com.example.DZforT1.DTO;


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
