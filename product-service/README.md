# Product Service

Product Service is an internal microservice of the Drugstore Platform monorepo.  
It owns the **product catalog**: CRUD, search, caching, and **Kafka** publication of product lifecycle events for downstream consumers.

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

- **Service name:** `product-service`
- **Role in platform:** Canonical product data and event source for cart, order, and inventory flows.
- **Main responsibility:** Maintain products, expose HTTPS API on **8444** by default, publish Kafka events.
- **Protocol:** REST over HTTPS; Kafka producer.
- **Persistence:** PostgreSQL with Flyway.

## Core Capabilities

- Product CRUD and domain validation.
- Redis cache for hot reads.
- Kafka topics for product events (see `application.yml` `app.kafka.topics`).
- JWT-secured API (see `jwt` block in `application.yml`).
- Spring Boot Admin client.

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Security, Spring Data JPA, Spring Data Redis, Spring Cache, Spring Kafka
- PostgreSQL, Flyway
- Springdoc OpenAPI
- Actuator + Micrometer + Prometheus
- Loki4j + Loki + Grafana
- Docker / Docker Compose + `Dockerfile`

## Project Structure

```text
product-service/
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
├── Dockerfile
├── build.gradle
└── README.md
```

## API Surface

- REST and OpenAPI per Springdoc configuration in `application.yml`.

## Security and Business Rules

- JWT validation for protected routes.
- Rate limiting under `app.rate-limit`.

## Observability

- Actuator (broad exposure in dev — restrict in production).
- Prometheus + Loki + Grafana in `docker-compose.yml`.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

```bash
docker compose up -d --build
```

## Testing

- H2 test scope where configured.

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

If this service changes its API contract, event contracts, or observability setup, update `docs/` and this `README.md` in the same PR.
