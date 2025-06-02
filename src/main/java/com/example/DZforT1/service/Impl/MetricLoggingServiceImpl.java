package com.example.DZforT1.service.Impl;

import com.example.DZforT1.DTO.KafkaMetricDTO;
import com.example.DZforT1.models.TimeLimitExceedLog;
import com.example.DZforT1.service.MetricLoggingService;
import com.example.DZforT1.service.TimeLimitExcedLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class MetricLoggingServiceImpl implements MetricLoggingService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TimeLimitExcedLogService timeLimitExceedLogService;

    @Value("${app.kafka.metric-topic}")
    private String metricTopic;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void logMetric(String metricName, long duration) {
        KafkaMetricDTO dto = new KafkaMetricDTO("METRIC", metricName, duration, LocalDateTime.now());

        try {
            String json = new ObjectMapper().writeValueAsString(dto);
            Message<String> message = MessageBuilder.withPayload(json)
                    .setHeader("error-type", "METRIC")
                    .build();

            kafkaTemplate.send(metricTopic, message.getPayload());
        } catch (Exception e) {
                timeLimitExceedLogService.logExceedMethod(metricName, duration);
        }

    }
}
