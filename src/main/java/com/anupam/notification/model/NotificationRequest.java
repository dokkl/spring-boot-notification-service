package com.anupam.notification.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record NotificationRequest(
        @NotBlank(message = "Recipient must not be blank")
        String recipient,

        @NotNull(message = "Channel must not be null")
        Channel channel,

        @NotBlank(message = "Template name must not be blank")
        String templateName,

        Map<String, String> variables,

        Priority priority
) {
    public enum Channel {
        EMAIL, SMS, PUSH
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}
