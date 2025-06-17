package com.example.DZforT1.service1.service.Impl;

import com.example.DZforT1.core.DTO.KafkaErrorDTO;
import com.example.DZforT1.service1.models.DataSourceErrorLog;
import com.example.DZforT1.service1.service.DataSourceErrorLogService;
import com.example.DZforT1.service1.service.ErrorLoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorLoggingServiceImpl implements ErrorLoggingService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DataSourceErrorLogService dataSourceErrorLogService;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.error-topic:t1_demo_metrics}")
    private String errorTopic;

    @Override
    public void logError(JoinPoint joinPoint, Exception ex) {
        String methodSignature = joinPoint.getSignature().toShortString();
        KafkaErrorDTO dto = new KafkaErrorDTO(
            "DATA_SOURCE",
            methodSignature,
            ex.getMessage(),
            LocalDateTime.now()
        );

        try {
            String json = objectMapper.writeValueAsString(dto);
            ProducerRecord<String, String> record = new ProducerRecord<>(errorTopic, json);
            record.headers().add("error-type", "DATA_SOURCE".getBytes(StandardCharsets.UTF_8));

            kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);
            log.debug("Successfully sent error to Kafka: {}", json);

        } catch (Exception kafkaEx) {
            log.error("Failed to send error to Kafka. Saving to DB. Reason: {}", kafkaEx.getMessage());

            DataSourceErrorLog errorLog = DataSourceErrorLog.builder()
                .exceptionMessage(ex.getMessage())
                .stackTrace(getTruncatedStackTrace(ex))
                .methodSignature(methodSignature)
                .timestamp(LocalDateTime.now())
                .build();

            dataSourceErrorLogService.saveError(errorLog);
        }
    }

    private String getTruncatedStackTrace(Exception ex) {
        if (ex.getStackTrace() == null || ex.getStackTrace().length == 0) {
            return "No stack trace";
        }
        return Arrays.stream(ex.getStackTrace())
                   .limit(10) // Ограничиваем глубину стека
                   .map(StackTraceElement::toString)
                   .collect(Collectors.joining("\n"));
    }
}
