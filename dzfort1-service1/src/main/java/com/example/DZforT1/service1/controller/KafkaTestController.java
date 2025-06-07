package com.example.DZforT1.service1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.metric-topic}")
    private String topic;

    @GetMapping("/send")
    public String sendTestMessage() {
        String payload = "{\"errorType\":\"TEST\",\"method\":\"testMethod\",\"timestamp\":\"2025-06-02T13:30:52\"}";
        kafkaTemplate.send(topic, payload);
        return "Message sent to Kafka";
    }
}
