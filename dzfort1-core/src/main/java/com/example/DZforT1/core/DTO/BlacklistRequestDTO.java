package com.example.DZforT1.core.DTO;

import java.util.UUID;

public record BlacklistRequestDTO(
        UUID clientId,
        UUID accountId
) {}
