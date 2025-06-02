package com.example.DZforT1.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaErrorConsumer {

    @KafkaListener(topics = "${app.kafka.metric-topic}", groupId = "metric-group")
    public void listen(String message) {
        System.out.println(message);
    }
}
