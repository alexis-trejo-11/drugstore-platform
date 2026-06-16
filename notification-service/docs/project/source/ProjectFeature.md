---
# ProjectFeature[]
features:
  - id: "classpath-notification-templates"
    title: "Classpath Notification Templates (Thymeleaf)"
    description: "Email and SMS notification bodies are rendered from version-controlled Thymeleaf templates on the classpath — no database-backed template store."
    icon: "mail"
    category: "messaging"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - "HTML email templates with shared header, footer, and minimal elegant styling"
      - "Plain-text SMS templates via a dedicated Thymeleaf TEXT engine"
      - "Handlers pass templateId; services resolve templates/notifications/{channel}/{templateId}"
      - "Subject lines supplied by orchestrator/handlers; body rendered from template variables"
    techStack:
      - "Thymeleaf"
      - "Spring Boot"
      - "classpath:/templates/notifications/"
    
    metrics:
      - label: "Email templates"
        value: "3"
        trend: "stable"
        icon: "email"
      - label: "SMS templates"
        value: "1"
        trend: "stable"
        icon: "sms"

    codeSnippet:
      language: "java"
      filename: "NotificationTemplateRenderer.java"
      code: |
        String html = templateRenderer.renderEmail(
            notification.getTemplateId(),
            templateVariables,
            notification.getRecipient().getLanguageOrDefault());
---
# Project Features

## Classpath Notification Templates

Notification bodies are **not** stored in MongoDB. They live as static files under `src/main/resources/templates/notifications/` and are rendered at send time with Thymeleaf.

See [Notification Templates](NotificationTemplates.md) for layout conventions, available templates, and how to add new ones.

### Flow

1. Kafka handler builds `templateVariables` and calls `NotificationOrchestrator.sendNotification(..., templateId, subject, ...)`.
2. `EmailNotificationService` / `SmsNotificationService` call `NotificationTemplateRenderer`.
3. Renderer loads `notifications/email/{templateId}.html` or `notifications/sms/{templateId}.txt`.
4. Provider (`EmailSender` / `SmsSender`) delivers the rendered message.

### Removed design

The previous `NotificationTemplate` entity and `NotificationTemplateRepository` (MongoDB) were removed in favor of classpath templates.
