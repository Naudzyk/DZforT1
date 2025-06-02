package com.example.DZforT1.service.Impl;

import com.example.DZforT1.DTO.KafkaErrorDTO;
import com.example.DZforT1.models.DataSourceErrorLog;
import com.example.DZforT1.service.DataSourceErrorLogService;
import com.example.DZforT1.service.ErrorLoggingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ErrorLoggingServiceImpl implements ErrorLoggingService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DataSourceErrorLogService dataSourceErrorLogService;

    @Value("${app.kafka.error-topic}")
    private String errorTopic;

    @Override
    public void logError(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String errorMessage = ex.getMessage();

        KafkaErrorDTO dto =  new KafkaErrorDTO("DATA_SOURCE", methodName, errorMessage, LocalDateTime.now());

        try{
            String json = new ObjectMapper().writeValueAsString(dto);
            kafkaTemplate.send(errorTopic, json);
        }catch (Exception kafkaException) {
            DataSourceErrorLog log = new DataSourceErrorLog();
                    log.setExceptionMessage(ex.getMessage());
                    log.setStackTrace(ex.getStackTrace().length > 0 ? Arrays.toString(ex.getStackTrace()) : "No stack trace");
                    log.setMethodSignature(joinPoint.getSignature().toShortString());
                    log.setTimestamp(LocalDateTime.now());
                    dataSourceErrorLogService.saveError(log);
        }

    }
}
