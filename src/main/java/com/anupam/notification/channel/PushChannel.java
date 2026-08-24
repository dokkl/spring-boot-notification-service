package com.anupam.notification.channel;

import com.anupam.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Push notification channel implementation (simulated).
 * In production, this would integrate with Firebase Cloud Messaging (FCM) or APNs.
 */
@Component
public class PushChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(PushChannel.class);

    @Override
    public NotificationRequest.Channel getChannel() {
        return NotificationRequest.Channel.PUSH;
    }

    @Override
    public void send(NotificationRequest request) {
        // In production: call FCM API
        // FirebaseMessaging.getInstance().send(
        //     Message.builder()
        //         .setToken(request.recipient())  // device token
        //         .setNotification(Notification.builder()
        //             .setTitle(resolveTitle(request))
        //             .setBody(resolveBody(request))
        //             .build())
        //         .build());

        log.info("[PUSH] Sending to device token {}: template={}",
                request.recipient(), request.templateName());
        log.info("[PUSH] Delivered successfully to: {}", request.recipient());
    }
}
