package com.anupam.notification.consumer;

import com.anupam.notification.channel.NotificationChannel;
import com.anupam.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final Map<NotificationRequest.Channel, NotificationChannel> channelHandlers;

    public NotificationConsumer(List<NotificationChannel> channels) {
        this.channelHandlers = channels.stream()
                .collect(Collectors.toMap(NotificationChannel::getChannel, Function.identity()));
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "notification-group")
    public void consume(NotificationRequest request) {
        log.info("Received notification: channel={}, recipient={}, template={}",
                request.channel(), request.recipient(), request.templateName());

        NotificationChannel handler = channelHandlers.get(request.channel());
        if (handler == null) {
            log.error("No handler found for channel: {}", request.channel());
            throw new IllegalArgumentException("Unsupported channel: " + request.channel());
        }

        handler.send(request);
        log.info("Notification sent successfully: channel={}, recipient={}",
                request.channel(), request.recipient());
    }

    @DltHandler
    public void handleDlt(NotificationRequest request) {
        log.error("Notification moved to DLT after retries exhausted: channel={}, recipient={}",
                request.channel(), request.recipient());
        // In production: persist to DB, alert ops team, etc.
    }
}
