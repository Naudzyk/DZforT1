package com.example.DZforT1.core.DTO;

import java.time.LocalDateTime;

public record KafkaMetricDTO(
        String errorType,
        String method,
        Long duration,
        LocalDateTime timestamp

) {
}
