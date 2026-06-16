# Notification Templates

Classpath-based notification templates rendered with **Thymeleaf**. Templates are versioned in Git and deployed with the service — there is no database template store.

## Directory layout

```text
src/main/resources/templates/notifications/
├── email/
│   ├── fragments/
│   │   └── base.html              # Shared head, header, footer, CSS
│   ├── email-verification-template.html
│   ├── welcome-email-template.html
│   └── two-factor-code-email-template.html
└── sms/
    └── two-factor-code-sms-template.txt
```

## Resolution rules

| Channel | Classpath path | Engine |
|---------|----------------|--------|
| Email | `templates/notifications/email/{templateId}.html` | Spring Boot `TemplateEngine` (HTML) |
| SMS | `templates/notifications/sms/{templateId}.txt` | `smsTemplateEngine` bean (TEXT) |

The `templateId` is the same string passed from Kafka handlers to `NotificationOrchestrator.sendNotification(...)`.

## Available templates

| templateId | Channel | Handler | Subject (from handler) |
|------------|---------|---------|------------------------|
| `email-verification-template` | EMAIL | `EmailVerificationEventHandler` | Verify your email address |
| `welcome-email-template` | EMAIL | `WelcomeEmailEventHandler` | Welcome to Drugstore! |
| `two-factor-code-email-template` | EMAIL | `TwoFactorCodeEventHandler` | Your verification code |
| `two-factor-code-sms-template` | SMS | `TwoFactorCodeEventHandler` | — |

## Template variables

Handlers build a `Map<String, String>` of variables. Common examples:

**Email verification:** `firstName`, `lastName`, `email`, `verificationToken`, `verificationCode`, `verificationUrl`, `expiresAt`, `isResend`

**Welcome:** `firstName`, `lastName`, `fullName`, `email`, `accountType`, `dashboardUrl`, `profileUrl`, `supportUrl`, `registeredAt`, `currentYear`

**Two-factor (email & SMS):** `firstName`, `code`, `purpose`, `expiresAt`, `ipAddress`, `deviceName`

`currentYear` is also injected automatically by `NotificationTemplateRenderer` when missing.

## Email layout

All HTML emails share `fragments/base.html`:

- **Header:** teal gradient bar with Drugstore branding
- **Content:** per-template body
- **Footer:** copyright and automated-message notice

Use `th:text` for user-supplied values (auto-escaped). Use `th:href` for links.

## Adding a new template

1. Create `templates/notifications/email/{templateId}.html` (or `.txt` for SMS).
2. Reuse fragments from `notifications/email/fragments/base` for consistent styling.
3. Pass the same `templateId` from your Kafka handler.
4. Document variables in this file.

## Configuration

```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    cache: true   # set THYMELEAF_CACHE=false in dev for hot reload
```

SMS uses a separate `smsTemplateEngine` bean (`ThymeleafTemplateConfig`) with `TemplateMode.TEXT`.

## Implementation classes

- `NotificationTemplateRenderer` — renders email/SMS templates from classpath
- `ThymeleafTemplateConfig` — TEXT-mode engine for SMS
- `EmailNotificationService` / `SmsNotificationService` — call renderer using `notification.getTemplateId()`

## Fallback behavior

If a template file is missing, services log a warning and fall back to `notification.getContent()` or a minimal default message.
