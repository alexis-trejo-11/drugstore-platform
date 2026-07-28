# Notification Service

Notification Service is an internal microservice of the Drugstore Platform monorepo.  
It handles **outbound notifications**: email, SMS, or other channels as implemented, driven by platform events and configuration.

## Table of Contents

- [Overview](#overview)
- [Core Capabilities](#core-capabilities)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [API Surface](#api-surface)
- [Security and Business Rules](#security-and-business-rules)
- [Observability](#observability)
- [Run Locally](#run-locally)
- [Docker](#docker)
- [Testing](#testing)
- [Documentation Navigation](#documentation-navigation)

## Overview

- **Service name:** `notification-service`
- **Role in platform:** Deliver user-facing and system notifications triggered by auth, orders, and other services.
- **Main responsibility:** Consume or expose APIs for notification dispatch, template handling, and provider integration.
- **Protocol:** REST (and async integrations per implementation).
- **Persistence:** As defined in service configuration and migrations.

## Core Capabilities

- Notification dispatch pipelines (Kafka → orchestrator → email/SMS providers).
- **Classpath Thymeleaf templates** for HTML email and plain-text SMS bodies (see [Notification Templates](docs/project/generated/NotificationTemplates.md)).
- Integration with external providers (see infrastructure and feature docs).
- Documented API schema and architecture under `docs/project/`.

## Tech Stack

- Java 23
- Spring Boot 3.3.x (see `build.gradle` for exact starters)
- Spring ecosystem components as declared in Gradle
- **Thymeleaf** for classpath notification templates
- Docker where a compose file is added for local stacks

## Project Structure

```text
notification-service/
├── .env.example            # All env vars (copy to .env at root)
├── Dockerfile
├── docker-compose.yml         # App-only; shared infra outside monorepo
├── src/
│   ├── main/
│   │   └── resources/templates/notifications/   # Thymeleaf email (.html) & SMS (.txt) templates
│   └── test/
├── docs/
│   └── project/
│       ├── *.md
│       └── obsidian/*.md
├── build.gradle
└── README.md
```

## Notification Templates

Email and SMS bodies are rendered from **Thymeleaf templates** on the classpath (`src/main/resources/templates/notifications/`). There is no database-backed template store.

| Channel | Path pattern | Example |
|---------|--------------|---------|
| Email | `email/{templateId}.html` | `email-verification-template.html` |
| SMS | `sms/{templateId}.txt` | `two-factor-code-sms-template.txt` |

Handlers pass `templateId` to `NotificationOrchestrator`; `NotificationTemplateRenderer` resolves and renders the file at send time.

Full reference: [Notification Templates](docs/project/generated/NotificationTemplates.md).

## API Surface

- See [API Schema](docs/project/generated/APISchema.md) and OpenAPI configuration in code.

## Security and Business Rules

- Treat provider API keys as secrets via environment variables only.

## Observability

- Add or align Actuator, Prometheus, and Loki with other services when this service joins the shared observability pattern.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker

App-only Compose at the service root. Shared Postgres/Redis/Kafka/observability live outside this monorepo — set endpoints in `.env` and join `infra_central_network` + `shared_app_network`.

See **[docs/docker-local-dev.md](../docs/docker-local-dev.md)** for networks, ports, and prerequisites.

```bash
cp .env.example .env
# Edit .env — JWT, GITHUB_TOKEN, DB/Redis/Kafka endpoints, SERVICE_PORT
docker compose up -d --build
```


## Testing

- Tests under `src/test/`.

## Documentation Navigation

### Main Service Documentation

- [Project Metadata](docs/project/generated/ProjectMetadata.md)
- [Project Overview](docs/project/generated/ProjectOverview.md)
- [Project Infrastructure](docs/project/generated/ProjectInfrastructure.md)
- [Project Features](docs/project/generated/ProjectFeature.md)
- [Notification Templates](docs/project/generated/NotificationTemplates.md)
- [Project Code Showcase](docs/project/generated/ProjectCodeShowCase.md)
- [Project Architecture](docs/project/generated/ProjectArchitecture.md)
- [API Schema](docs/project/generated/APISchema.md)

### Structured Source Docs (Obsidian-style)

- [Project Metadata (Source)](docs/project/source/ProjectMetadata.md)
- [Project Overview (Source)](docs/project/source/ProjectOverview.md)
- [Project Infrastructure (Source)](docs/project/source/ProjectInfrastructure.md)
- [Project Features (Source)](docs/project/source/ProjectFeature.md)
- [Notification Templates (Source)](docs/project/source/NotificationTemplates.md)
- [Project Code Showcase (Source)](docs/project/source/ProjectCodeShowCase.md)
- [Project Architecture (Source)](docs/project/source/ProjectArchitecture.md)
- [API Schema (Source)](docs/project/source/APISchema.md)

---

If this service changes its API contract, providers, or observability setup, update `docs/` and this `README.md` in the same PR.
