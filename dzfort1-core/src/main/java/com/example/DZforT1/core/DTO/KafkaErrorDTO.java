package com.example.DZforT1.core.DTO;

import java.time.LocalDateTime;

public record KafkaErrorDTO(
        String errorType,
        String methodSignature,
        String message,
        LocalDateTime timestamp
) {
}
