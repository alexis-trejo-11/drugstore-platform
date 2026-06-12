# User Service

User Service is an internal microservice of the Drugstore Platform monorepo.  
It owns **user identity and profile** persistence: accounts, attributes consumed by auth-service over **gRPC**, and **Kafka** events for downstream systems.

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

- **Service name:** `user-service`
- **Role in platform:** System of record for user data used by auth and other services.
- **Main responsibility:** User CRUD, Flyway migrations, Redis cache, Kafka consumers/producers, gRPC server for internal calls.
- **Protocols:** REST over HTTPS (port **8080**), gRPC, Kafka.
- **Persistence:** PostgreSQL via `DATASOURCE_URL` (`user_db` by default).

## Core Capabilities

- User lifecycle APIs and persistence.
- gRPC service for high-performance internal queries and updates.
- Kafka integration for user events and related topics.
- Spring Cloud Config bootstrap when enabled.
- Token and 2FA configuration blocks in `application.yml` (see docs for semantics).

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Security, Spring Data JPA, Spring Data Redis, Spring Kafka
- gRPC + Protobuf
- PostgreSQL, Flyway
- Springdoc OpenAPI
- Actuator + Micrometer + Prometheus
- Loki4j + Loki + Grafana
- Docker / Docker Compose + `Dockerfile`

## Project Structure

```text
user-service/
├── .env.example                   # Environment template (copy to .env)
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

- REST and Swagger paths driven by `springdoc` and profile-specific settings in `application.yml`.
- gRPC port defaults from `grpc.server.port`.

## Security and Business Rules

- JWT configuration shared patterns with auth-service consumers.
- Secrets only via environment (DB, Redis, Kafka, JWT).

## Observability

- Actuator includes Prometheus in default exposure list (customizable via `ACTUATOR_ENDPOINTS_INCLUDE`).
- Loki via Logback; compose includes Postgres, Redis, Flyway migrate job, Prometheus, Loki, Grafana.

## Run Locally

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

All Docker assets live under **`docker/`**. See **[docker/README.md](docker/README.md)** for compose files, profiles (`local` / `prod`), and environment setup.

```bash
cp .env.example .env
# Edit .env — set JWT_SECRET_KEY, GITHUB_TOKEN, and connection URLs
chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh

docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

Typical URLs:

- Health (via Nginx): `https://localhost/actuator/health`
- Grafana: `http://localhost:3000`

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

If this service changes its API contract, gRPC contracts, event schemas, or observability setup, update `docs/` and this `README.md` in the same PR.
