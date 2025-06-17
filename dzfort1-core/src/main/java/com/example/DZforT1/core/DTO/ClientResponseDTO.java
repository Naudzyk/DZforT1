package com.example.DZforT1.core.DTO;


import com.example.DZforT1.core.ENUM.ClientStatus;

import java.util.List;
import java.util.UUID;

public record ClientResponseDTO(
    String firstName,
    String lastName,
    String middleName,
    ClientStatus status,
    List<AccountResponseDTO> accounts,
    UUID clientId
) {}
