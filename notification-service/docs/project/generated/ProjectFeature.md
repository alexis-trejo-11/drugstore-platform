# Project Features

## Classpath Notification Templates

Notification bodies are **not** stored in MongoDB. They live as static files under `src/main/resources/templates/notifications/` and are rendered at send time with Thymeleaf.

See [Notification Templates](NotificationTemplates.md) for layout conventions, available templates, and how to add new ones.

### Highlights

- HTML email templates with shared header, footer, and minimal elegant styling (teal brand palette)
- Plain-text SMS templates via a dedicated Thymeleaf TEXT engine
- Handlers pass `templateId`; services resolve `templates/notifications/{channel}/{templateId}`
- Subject lines supplied by orchestrator/handlers; body rendered from template variables

### Flow

1. Kafka handler builds `templateVariables` and calls `NotificationOrchestrator.sendNotification(..., templateId, subject, ...)`.
2. `EmailNotificationService` / `SmsNotificationService` call `NotificationTemplateRenderer`.
3. Renderer loads `notifications/email/{templateId}.html` or `notifications/sms/{templateId}.txt`.
4. Provider (`EmailSender` / `SmsSender`) delivers the rendered message.

### Removed design

The previous `NotificationTemplate` entity and `NotificationTemplateRepository` (MongoDB) were removed in favor of classpath templates.
