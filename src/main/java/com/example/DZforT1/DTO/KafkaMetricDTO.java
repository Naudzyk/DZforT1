package com.example.DZforT1.DTO;

import java.time.LocalDateTime;

public record KafkaMetricDTO(
        String errorType,
        String method,
        long duration,
        LocalDateTime timestamp

) {
}
