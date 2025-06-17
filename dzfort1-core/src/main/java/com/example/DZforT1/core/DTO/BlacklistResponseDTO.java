package com.example.DZforT1.core.DTO;

import java.util.UUID;

public record BlacklistResponseDTO(
        UUID clientId,
        UUID accountId,
        boolean isBlacklisted
) {
}
