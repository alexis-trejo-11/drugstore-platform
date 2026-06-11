# Order Service

Order Service is an internal microservice of the Drugstore Platform monorepo.  
It owns the **order** aggregate: placement, state transitions, persistence, and coordination with payment, inventory, and catalog services.

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

- **Service name:** `order-service`
- **Role in platform:** Order lifecycle and orchestration boundary.
- **Main responsibility:** Create and manage orders, enforce business rules, expose HTTPS API, integrate with Redis and PostgreSQL.
- **Protocol:** REST over HTTPS (default port **8446** in `application.yml`).
- **Persistence:** PostgreSQL with Flyway.

## Core Capabilities

- Order CRUD and workflow-related operations.
- Redis-backed caching configuration.
- Spring Security on HTTP endpoints.
- Spring Boot Admin client registration.
- JSON logging pipeline (Logstash encoder) plus **Loki** for centralized logs.

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Security, Spring Data JPA, Spring Data Redis, Spring Cache
- PostgreSQL, Flyway
- Springdoc OpenAPI
- Logstash encoder + Janino + Loki4j
- Actuator + Micrometer + Prometheus
- Loki + Grafana + Prometheus (Docker)
- Docker / Docker Compose

## Project Structure

```text
order-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
├── observability/
│   ├── prometheus/
│   └── grafana/provisioning/datasources/
├── docs/
│   └── project/
│       ├── *.md
│       └── obsidian/*.md
├── docker-compose.yml
├── build.gradle
└── README.md
```

## API Surface

- REST under `/api/**` (Springdoc `microservice.order_service` package scan in `application.yml`).

## Security and Business Rules

- JWT-authenticated routes where configured.
- Rate limiting profiles under `app.rate-limit`.

## Observability

- Broad Actuator exposure in dev; tighten for production.
- Prometheus scrape over HTTPS with skip-verify in local `prometheus.yml`.
- Loki via Logback appender.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

Compose references `order-service:latest` — build the image first or adjust the compose service to `build:`.

```bash
docker compose up -d --build
```

Typical scrape target: `https://order-service:8446/actuator/prometheus`.

## Testing

- H2 for tests; suites under `src/test/`.

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

If this service changes its API contract, domain rules, or observability setup, update `docs/` and this `README.md` in the same PR.
