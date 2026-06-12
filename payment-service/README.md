# Payment Service

Payment Service is an internal microservice of the Drugstore Platform monorepo.  
It owns **payment capture and reconciliation**: Stripe integration, webhooks, persistence of payment records, and safe handling of secrets.

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

- **Service name:** `payment-service`
- **Role in platform:** Payment provider boundary for checkout and order completion.
- **Main responsibility:** Process payments, verify webhooks, persist payment state, expose REST API on **8085** by default.
- **Protocol:** REST.
- **Persistence:** PostgreSQL with Flyway.

## Core Capabilities

- Stripe API integration (`stripe.api-key`, `stripe.webhook-secret` from environment).
- Flyway-managed schema; Hibernate validates against migrations.
- Springdoc OpenAPI for HTTP documentation.
- Actuator with Prometheus registry for metrics.

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Data JPA, Spring Security, Spring Kafka (where used)
- PostgreSQL, Flyway
- Stripe Java SDK
- Springdoc OpenAPI
- Actuator + Micrometer + Prometheus
- Loki4j + Loki + Grafana
- Docker / Docker Compose

## Project Structure

```text
payment-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-docker.yml
│   │       └── logback-spring.xml
│   └── test/
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.full.yml
│   ├── docker-compose.app.yml
│   ├── nginx/
│   └── observability/
├── docs/
│   └── project/
│       ├── *.md
│       └── obsidian/*.md
├── build.gradle
└── README.md
```

## API Surface

- REST endpoints for payment intents, confirmations, or webhooks as implemented (see `docs/project/generated/APISchema.md`).

## Security and Business Rules

- Never commit Stripe keys or webhook secrets; use environment variables only.
- Validate webhook signatures before trusting payloads.

## Observability

- `/actuator/prometheus` enabled in `application.yml`.
- Logs to Loki via Logback in non-test profiles.
- Local Prometheus, Loki, Grafana via `docker/docker-compose.full.yml`.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

All Docker assets live under [`docker/`](docker/). See [`docker/README.md`](docker/README.md) for profiles (`local` / `prod`), compose files, and env setup.

```bash
cd docker
cp .env.example .env && cp .env.local.example .env.local
# Edit .env — set STRIPE_API_KEY, STRIPE_WEBHOOK_SECRET, DB_PASSWORD
./nginx/ssl/generate-certs.sh

docker compose -f docker-compose.full.yml --profile local --env-file .env --env-file .env.local up -d --build
```

## Testing

- Tests under `src/test/`.

## Documentation Navigation

### Main Service Documentation

- [Project Metadata](docs/project/generated/ProjectMetadata.md)
- [Project Overview](docs/project/generated/ProjectOverview.md)
- [Project Infrastructure](docs/project/generated/ProjectInfrastructure.md)
- [Project Features](docs/project/generated/ProjectFeature.md)
- [Project Code Showcase](docs/project/generated/ProjectCodeShowCase.md)
- [Project Architecture](docs/project/generated/ProjectArchitecture.md)
- [API Schema](docs/project/generated/APISchema.md)

### Structured Source Docs (Obsidian-style)

- [Project Metadata (Source)](docs/project/source/ProjectMetadata.md)
- [Project Overview (Source)](docs/project/source/ProjectOverview.md)
- [Project Infrastructure (Source)](docs/project/source/ProjectInfrastructure.md)
- [Project Features (Source)](docs/project/source/ProjectFeature.md)
- [Project Code Showcase (Source)](docs/project/source/ProjectCodeShowCase.md)
- [Project Architecture (Source)](docs/project/source/ProjectArchitecture.md)
- [API Schema (Source)](docs/project/source/APISchema.md)

---

If this service changes its API contract, provider integration, or observability setup, update `docs/` and this `README.md` in the same PR.
