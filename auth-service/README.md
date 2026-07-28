# Auth Service

Auth Service is an internal microservice of the Drugstore Platform monorepo.  
It centralizes **authentication and authorization**: JWT access and refresh flows, OAuth2 social login, session handling with Redis, Kafka event publishing, and **gRPC** integration with the user service.

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

- **Service name:** `auth-service`
- **Role in platform:** Identity and token issuance for the rest of the ecosystem.
- **Main responsibility:** Authenticate users, issue and validate JWTs, coordinate with user-service, publish auth-related events.
- **Protocols:** REST (HTTP locally, HTTPS in Docker profile), Kafka producers, gRPC client to users.
- **Persistence:** Delegated to user-service and Redis for session-oriented state where applicable.

## Core Capabilities

- JWT access and refresh token handling.
- OAuth2 client (e.g. Google) integration.
- Kafka producers for user and auth lifecycle topics.
- gRPC client to user-service for user data operations.
- Spring Security–protected HTTP API surface.
- Rate limiting configuration (see `application.yml`).

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Security, Spring Data Redis, Spring Kafka, OAuth2 client
- gRPC + Protobuf
- JJWT
- Springdoc OpenAPI (where enabled by profile)
- Actuator + Micrometer + Prometheus
- Console logging (stdout); shared Promtail → Loki + Grafana
- Nginx 1.27 (reverse proxy + load balancer)
- Docker / Docker Compose (app-only at service root)

## Project Structure

```text
auth-service/
├── src/
│   ├── main/
│   │   ├── java/                  # Controllers, security, Kafka, gRPC clients
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-docker.yml
│   │       └── logback-spring.xml
│   └── test/
├── Dockerfile
├── docker-compose.yml         # App-only; shared infra outside monorepo
├── docs/
│   └── project/
│       ├── *.md                   # Human-readable docs
│       └── obsidian/*.md          # Structured source docs
├── build.gradle
└── README.md
```

## API Surface

- REST API for registration, login, token refresh, and related auth flows (exact paths in OpenAPI / code).
- OpenAPI UI when enabled via Spring profile and `springdoc` configuration.

## Security and Business Rules

- JWT-based authentication for protected resources.
- Secrets and keys supplied via environment or `.env` (never commit real secrets).
- OAuth2 client credentials from environment variables.

## Observability

- Actuator exposes health, info, and Prometheus metrics (see `application-docker.yml`).
- Logs go to stdout; Promtail (shared infra) ships them to Loki.
- Metrics/logs go to the shared observability stack outside this monorepo.


## Run Locally

Requirements:

- Java 23
- Docker + Docker Compose (recommended for Redis and full stack)

Application only:

```bash
./gradlew bootRun
```

Run tests:

```bash
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
- Run `./gradlew test` before merging changes that touch security or token logic.

## Documentation Navigation

Detailed docs are available under `docs/`:

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

If this service changes its API contract, security model, or observability setup, update `docs/` and this `README.md` in the same PR.
