# Architecture

## Reverse proxy (Nginx)

This repository does **not** bundle an Nginx edge container. Terminate TLS at your cluster ingress or reuse the `nginx/` + compose pattern from sibling microservices.

## Notification template layer

Notification bodies are rendered from **classpath Thymeleaf templates** (`templates/notifications/`), not from MongoDB. See [Notification Templates](NotificationTemplates.md).

```text
Kafka Handler → NotificationOrchestrator (templateId + variables)
             → EmailNotificationService / SmsNotificationService
             → NotificationTemplateRenderer (Thymeleaf)
             → EmailSender / SmsSender
```

MongoDB persists **notification delivery records** (`NotificationRepository`), not template content.

