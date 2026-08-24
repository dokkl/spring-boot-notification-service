package com.anupam.notification.producer;

import com.anupam.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;
    private final String topic;

    public NotificationProducer(KafkaTemplate<String, NotificationRequest> kafkaTemplate,
                                @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(NotificationRequest request) {
        String key = request.channel().name() + "-" + request.recipient();
        log.info("Publishing notification to Kafka: channel={}, recipient={}, priority={}",
                request.channel(), request.recipient(), request.priority());

        kafkaTemplate.send(topic, key, request)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish notification: {}", ex.getMessage());
                    } else {
                        log.info("Notification published: offset={}",
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
