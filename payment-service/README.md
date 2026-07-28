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
- [Docker](#docker)
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
- Console logging (stdout); shared Promtail → Loki + Grafana
- Docker / Docker Compose (app-only at service root)

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
├── Dockerfile
├── docker-compose.yml         # App-only; shared infra outside monorepo
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
- Actuator Prometheus endpoint; shared Loki/Grafana outside this monorepo.

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
- `application-test.yml` provides test profile configuration.

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
