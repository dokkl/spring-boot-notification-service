package com.anupam.notification.channel;

import com.anupam.notification.model.NotificationRequest;

/**
 * Strategy interface for notification delivery channels.
 * Each channel (Email, SMS, Push) implements this interface.
 */
public interface NotificationChannel {

    /**
     * Returns the channel type this handler supports.
     */
    NotificationRequest.Channel getChannel();

    /**
     * Sends the notification through this channel.
     * Throws RuntimeException on failure (triggers retry).
     */
    void send(NotificationRequest request);
}
