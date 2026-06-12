# Inventory Service

Inventory Service is an internal microservice of the Drugstore Platform monorepo.  
It owns **inventory and stock** visibility: availability, reservations-friendly data model, caching, and integration with orders and catalog.

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

- **Service name:** `inventory-service`
- **Role in platform:** Source of truth for stock levels and inventory-facing queries.
- **Main responsibility:** Expose inventory APIs, apply rate limits, use Redis cache, persist to PostgreSQL.
- **Protocol:** REST on **8083** by default in `application.yml`.
- **Persistence:** PostgreSQL (Flyway optional per configuration).

## Core Capabilities

- Inventory read/update flows and validation.
- Redis-backed cache with TTL-oriented configuration.
- Custom rate limiting (see logging package in `application.yml`).
- Springdoc OpenAPI.
- RabbitMQ integration where enabled in build.

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Data JPA, Spring Data Redis, Spring Cache, Spring AMQP
- PostgreSQL, Flyway (optional by profile)
- Logstash encoder (legacy ELK-style logs) + **Loki** for unified observability
- Spring Boot Admin client
- Actuator + Micrometer + Prometheus
- Loki4j + Loki + Grafana
- Docker / Docker Compose

## Project Structure

```text
inventory-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.full.yml    # App + DB + Redis + monitoring
│   ├── docker-compose.app.yml     # App + Nginx only
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

- REST under `/api/**` per Springdoc configuration.

## Security and Business Rules

- Rate limiting filters and app-specific rules (see `docs/project/generated/ProjectFeature.md`).

## Observability

- Actuator endpoints including Prometheus.
- Loki log shipping via Logback.
- Compose stack for local Prometheus, Loki, Grafana.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

All containerization lives under `docker/`. See **[docker/README.md](docker/README.md)** for profiles, env files, and endpoints.

```bash
cd docker
cp .env.example .env && cp .env.local.example .env.local
./nginx/ssl/generate-certs.sh

# Full local stack (app + Postgres + Redis + monitoring)
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

If this service changes its API contract, domain rules, or observability setup, update `docs/` and this `README.md` in the same PR.
