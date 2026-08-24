# Notification Service — Spring Boot + Kafka

A multi-channel notification service built with Spring Boot 3.5, Java 21, and Apache Kafka. Supports Email (JavaMail + Thymeleaf), SMS (simulated), and Push (simulated) channels with priority-based routing.

## Features

- **Multi-Channel** — Email, SMS, and Push notifications through a unified API
- **Async Processing** — Kafka-based event-driven architecture
- **Template Engine** — Thymeleaf templates for rich HTML emails
- **Strategy Pattern** — Channel abstraction for easy extensibility
- **Priority Support** — HIGH/MEDIUM/LOW priority routing

## Tech Stack

| Layer         | Technology           |
|---------------|----------------------|
| Framework     | Spring Boot 3.5.0    |
| Language      | Java 21              |
| Messaging     | Apache Kafka (KRaft) |
| Email         | JavaMail + Thymeleaf |
| SMTP Testing  | Mailhog              |
| Build         | Maven                |

## Running Locally

```bash
# Start Kafka + Mailhog
docker compose up -d

# Run the application
./mvnw spring-boot:run
```

## API Usage

```bash
# Send email notification
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "user@example.com",
    "channel": "EMAIL",
    "templateName": "welcome",
    "variables": {"name": "Anupam", "activationLink": "https://example.com/activate"},
    "priority": "HIGH"
  }'

# Send SMS notification
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "+1234567890",
    "channel": "SMS",
    "templateName": "otp",
    "variables": {"code": "123456"},
    "priority": "HIGH"
  }'
```

## Mailhog UI

View sent emails at: http://localhost:8025

## Blog Post

Detailed system design walkthrough:  
[System Design — Notification Service with Spring Boot](https://anupamsinha.github.io/posts/system-design-notification-service-spring-boot/)
