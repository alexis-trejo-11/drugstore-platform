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
- [Docker and Full Local Stack](#docker-and-full-local-stack)
- [Testing](#testing)
- [Documentation Navigation](#documentation-navigation)

## Overview

- **Service name:** `notification-service`
- **Role in platform:** Deliver user-facing and system notifications triggered by auth, orders, and other services.
- **Main responsibility:** Consume or expose APIs for notification dispatch, template handling, and provider integration.
- **Protocol:** REST (and async integrations per implementation).
- **Persistence:** As defined in service configuration and migrations.

## Core Capabilities

- Notification dispatch pipelines.
- Integration with external providers (see infrastructure and feature docs).
- Documented API schema and architecture under `docs/project/`.

## Tech Stack

- Java 23
- Spring Boot 3.3.x (see `build.gradle` for exact starters)
- Spring ecosystem components as declared in Gradle
- Docker where a compose file is added for local stacks

## Project Structure

```text
notification-service/
├── src/
│   ├── main/
│   └── test/
├── docs/
│   └── project/
│       ├── *.md
│       └── obsidian/*.md
├── build.gradle
└── README.md
```

## API Surface

- See [API Schema](docs/project/APISchema.md) and OpenAPI configuration in code.

## Security and Business Rules

- Treat provider API keys as secrets via environment variables only.

## Observability

- Add or align Actuator, Prometheus, and Loki with other services when this service joins the shared observability pattern.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

Add or use a `docker-compose.yml` when you containerize this service; until then run with JVM only.

## Testing

- Tests under `src/test/`.

## Documentation Navigation

### Main Service Documentation

- [Project Metadata](docs/project/ProjectMetadata.md)
- [Project Overview](docs/project/ProjectOverview.md)
- [Project Infrastructure](docs/project/ProjectInfrastructure.md)
- [Project Features](docs/project/ProjectFeature.md)
- [Project Code Showcase](docs/project/ProjectCodeShowCase.md)
- [Project Architecture](docs/project/ProjectArchitecture.md)
- [API Schema](docs/project/APISchema.md)

### Structured Source Docs (Obsidian-style)

- [Project Metadata (Source)](docs/project/obsidian/ProjectMetadata.md)
- [Project Overview (Source)](docs/project/obsidian/ProjectOverview.md)
- [Project Infrastructure (Source)](docs/project/obsidian/ProjectInfrastructure.md)
- [Project Features (Source)](docs/project/obsidian/ProjectFeature.md)
- [Project Code Showcase (Source)](docs/project/obsidian/ProjectCodeShowCase.md)
- [Project Architecture (Source)](docs/project/obsidian/ProjectArchitecture.md)
- [API Schema (Source)](docs/project/obsidian/APISchema.md)

---

If this service changes its API contract, providers, or observability setup, update `docs/` and this `README.md` in the same PR.
