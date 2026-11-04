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
  
  
# push  
curl -X POST http://localhost:8080/api/notifications \                                                                                                                                                      
    -H "Content-Type: application/json" \                                                                                                                                                                     
    -d '{"recipient":"hoon_push_test","channel":"PUSH","templateName":"welcome","variables":{"message":"원하는 내용"},"priority":"HIGH"}'
```

## Email delivery (Naver SMTP)

By default email is sent through Naver SMTP (`smtp.naver.com:465`, SSL). Provide credentials via environment variables:

```bash
export NAVER_MAIL_USERNAME=yourid@naver.com
export NAVER_MAIL_PASSWORD='your-password-or-app-password'
mvn spring-boot:run
```

- In Naver Mail → 환경설정 → POP3/IMAP 설정, enable **IMAP/SMTP 사용**.
- If 2-step verification is on, use an **application password** (네이버 내정보 → 보안설정 → 애플리케이션 비밀번호).
- The sender (`From`) defaults to `NAVER_MAIL_USERNAME`; Naver rejects a different sender address.

## Mailhog UI (local testing)

To capture mail locally instead of sending it, run with the `mailhog` profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mailhog
```

View captured emails at: http://localhost:8025

## Blog Post

Detailed system design walkthrough:  
[System Design — Notification Service with Spring Boot](https://anupamsinha.github.io/posts/system-design-notification-service-spring-boot/)
