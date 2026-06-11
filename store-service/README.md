# Store Service

Store Service is an internal microservice of the Drugstore Platform monorepo.  
It owns **store** master data: locations, branding or operational attributes, and relationships to catalog and orders as defined in your domain.

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

- **Service name:** `store-service`
- **Role in platform:** Store entity and APIs for multi-store commerce.
- **Main responsibility:** Persist stores in PostgreSQL, cache with Redis, expose HTTPS API on **8443** by default.
- **Protocol:** REST over HTTPS.
- **Persistence:** PostgreSQL with Flyway.

## Core Capabilities

- Store CRUD and queries.
- Redis-backed cache.
- JWT-secured HTTP API.
- Spring Boot Admin client.
- Logstash-style JSON logs plus Loki forwarding.

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
store-service/
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

- REST under `/api/**` per Springdoc `paths-to-match` in `application.yml`.

## Security and Business Rules

- JWT configuration in `application.yml`.
- Rate limiting under `app.rate-limit`.

## Observability

- Actuator (wide exposure in dev).
- Prometheus scrape over HTTPS in local `prometheus.yml`.
- Loki appender in Logback.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

Compose uses `store-service:latest` — build the image first or add a `build:` directive.

```bash
docker compose up -d --build
```

## Testing

- H2 for tests where configured.

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

If this service changes its API contract, domain rules, or observability setup, update `docs/` and this `README.md` in the same PR.
