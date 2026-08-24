package com.anupam.notification.channel;

import com.anupam.notification.model.NotificationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class EmailChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(EmailChannel.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailChannel(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public NotificationRequest.Channel getChannel() {
        return NotificationRequest.Channel.EMAIL;
    }

    @Override
    public void send(NotificationRequest request) {
        try {
            String htmlContent = renderTemplate(request.templateName(), request.variables());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(request.recipient());
            helper.setSubject(resolveSubject(request.templateName()));
            helper.setText(htmlContent, true);
            helper.setFrom("notifications@example.com");

            mailSender.send(message);
            log.info("Email sent to: {}", request.recipient());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", request.recipient(), e.getMessage());
            throw new RuntimeException("Email delivery failed", e);
        }
    }

    private String renderTemplate(String templateName, Map<String, String> variables) {
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        return templateEngine.process(templateName, context);
    }

    private String resolveSubject(String templateName) {
        return switch (templateName) {
            case "welcome" -> "Welcome to Our Platform!";
            case "otp" -> "Your Verification Code";
            case "password-reset" -> "Password Reset Request";
            default -> "Notification";
        };
    }
}
