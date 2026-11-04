package com.anupam.notification.channel;

import com.anupam.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Push notification channel backed by ntfy (https://ntfy.sh).
 * The request's {@code recipient} is the ntfy topic the device has subscribed to.
 * <p>
 * ntfy.sh topics are public: anyone who knows the name can read or publish.
 * Use hard-to-guess topic names and never send sensitive data. For production,
 * replace with Firebase Cloud Messaging (FCM) using device tokens.
 */
@Component
public class PushChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(PushChannel.class);

    private final RestClient restClient;

    public PushChannel(RestClient.Builder builder,
                       @Value("${app.push.ntfy.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public NotificationRequest.Channel getChannel() {
        return NotificationRequest.Channel.PUSH;
    }

    @Override
    public void send(NotificationRequest request) {
        // JSON publishing keeps UTF-8 (Korean) titles intact, unlike header-based publishing.
        Map<String, Object> payload = Map.of(
                "topic", request.recipient(),
                "title", resolveTitle(request.templateName()),
                "message", resolveBody(request),
                "priority", toNtfyPriority(request.priority())
        );

        log.info("[PUSH] Sending to ntfy topic {}: template={}", request.recipient(), request.templateName());

        // Non-2xx responses throw RestClientResponseException, which triggers Kafka retry/DLT.
        restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("[PUSH] Delivered successfully to topic: {}", request.recipient());
    }

    private String resolveTitle(String templateName) {
        return switch (templateName) {
            case "welcome" -> "Welcome to Our Platform!";
            case "otp" -> "Your Verification Code";
            case "password-reset" -> "Password Reset Request";
            default -> "Notification";
        };
    }

    private String resolveBody(NotificationRequest request) {
        Map<String, String> vars = request.variables();
        if (vars != null && vars.containsKey("message")) {
            return vars.get("message");
        }
        if (vars != null && vars.containsKey("code")) {
            return "Your verification code is: " + vars.get("code");
        }
        if (vars != null && vars.containsKey("name")) {
            return "Hello, " + vars.get("name") + "!";
        }
        return "You have a new notification from our platform.";
    }

    /** ntfy priority: 1 (min) .. 5 (max), 3 is default. */
    private int toNtfyPriority(NotificationRequest.Priority priority) {
        if (priority == null) {
            return 3;
        }
        return switch (priority) {
            case HIGH -> 4;
            case MEDIUM -> 3;
            case LOW -> 2;
        };
    }
}
