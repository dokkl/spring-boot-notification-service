package com.anupam.notification.channel;

import com.anupam.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SMS channel implementation (simulated).
 * In production, this would integrate with Twilio, AWS SNS, or similar.
 */
@Component
public class SmsChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(SmsChannel.class);

    @Override
    public NotificationRequest.Channel getChannel() {
        return NotificationRequest.Channel.SMS;
    }

    @Override
    public void send(NotificationRequest request) {
        // In production: call Twilio API
        // TwilioRestClient.create(accountSid, authToken)
        //     .messages()
        //     .create(new PhoneNumber(request.recipient()),
        //             new PhoneNumber(fromNumber),
        //             buildMessageBody(request));

        String messageBody = buildMessageBody(request);
        log.info("[SMS] Sending to {}: {}", request.recipient(), messageBody);
        log.info("[SMS] Delivered successfully to: {}", request.recipient());
    }

    private String buildMessageBody(NotificationRequest request) {
        if (request.variables() != null && request.variables().containsKey("code")) {
            return "Your verification code is: " + request.variables().get("code");
        }
        return "You have a new notification from our platform.";
    }
}
