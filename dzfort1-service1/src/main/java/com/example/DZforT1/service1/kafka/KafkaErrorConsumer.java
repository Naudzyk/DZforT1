package com.example.DZforT1.service1.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaErrorConsumer {

    @KafkaListener(topics = "${app.kafka.metric-topic}", groupId = "metric-group")
    public void listen(String message) {
        System.out.println(message);
    }
}
