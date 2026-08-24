package com.anupam.notification.controller;

import com.anupam.notification.model.NotificationRequest;
import com.anupam.notification.model.NotificationStatus;
import com.anupam.notification.producer.NotificationProducer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationProducer notificationProducer;

    public NotificationController(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> send(@Valid @RequestBody NotificationRequest request) {
        notificationProducer.send(request);

        Map<String, Object> response = Map.of(
                "status", NotificationStatus.QUEUED,
                "message", "Notification queued for delivery",
                "channel", request.channel(),
                "recipient", request.recipient()
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
