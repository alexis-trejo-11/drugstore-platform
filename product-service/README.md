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
- Docker / Docker Compose (`docker/`)

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
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.full.yml
│   ├── docker-compose.app.yml
│   └── README.md
├── docs/
│   └── project/
│       ├── *.md
│       └── obsidian/*.md
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
- Prometheus + Loki + Grafana in `docker/docker-compose.full.yml`.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

All containerization lives under [`docker/`](docker/README.md). See that README for profiles (`local` / `prod`), compose files, and env setup.

```bash
cp .env.example .env
# Edit .env — set JWT_SECRET_KEY and GITHUB_TOKEN
chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

Two compose files are available:

| File | Contents |
|------|----------|
| `docker-compose.full.yml` | App + Nginx + PostgreSQL + Redis + monitoring |
| `docker-compose.app.yml` | App + Nginx only (external DB/Redis/Kafka) |

Two profiles: **`local`** and **`prod`** (set `COMPOSE_PROFILES` in root `.env`).

## Testing

- H2 test scope where configured.

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

If this service changes its API contract, event contracts, or observability setup, update `docs/` and this `README.md` in the same PR.
