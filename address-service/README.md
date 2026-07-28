# Address Service

Address Service is an internal microservice of the Drugstore Platform monorepo.  
It owns user address management with secure, validated, and observable APIs for both customer self-service and admin operations.

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
- **Service name:** `address-service`
- **Role in platform:** Address domain service in a microservices architecture.
- **Main responsibility:** Create, read, update, delete, and manage default addresses by user role.
- **Protocol:** REST over HTTPS.
- **Persistence:** PostgreSQL with Flyway migrations.

## Core Capabilities
- Multi-country postal code validation (US, MX, CA, ES, UK).
- Role-based address limits (CUSTOMER and EMPLOYEE constraints).
- Default address management (single default per user).
- User vs Admin API separation through dedicated controllers.
- Soft delete strategy for address records.
- JWT-authenticated endpoints with role-based authorization.
- Redis-backed rate limiting with different sensitivity profiles.

## Tech Stack
- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Security, Spring Data JPA
- PostgreSQL 15
- Redis 7
- Flyway
- Springdoc OpenAPI
- Actuator + Micrometer + Prometheus
- Loki4j + Loki + Grafana
- Docker / Docker Compose (app-only at service root)

## Project Structure
```text
address-service/
├── src/
│   ├── main/
│   │   ├── java/io/github/alexisTrejo11/drugstore/address/
│   │   │   ├── controller/        # User and admin REST controllers
│   │   │   ├── service/           # Business orchestration
│   │   │   ├── repository/        # JPA repositories
│   │   │   ├── entity/            # Persistence models
│   │   │   ├── config/            # Security, OpenAPI, rate-limit, etc.
│   │   │   └── utils/             # DTOs, mappers, validation helpers
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-docker.yml
│   │       ├── logback-spring.xml
│   │       └── db/migration/
│   └── test/
├── Dockerfile
├── docker-compose.yml         # App-only; shared infra outside monorepo
├── docs/
│   ├── observability-checklist.md
│   └── project/
│       ├── *.md                   # Human-readable docs
│       └── obsidian/*.md          # Structured source docs
├── build.gradle
└── README.md
```

## API Surface
- **User base path:** `/api/v2/user/addresses`
- **Admin base path:** `/api/v2/addresses/admin`
- Includes CRUD operations, default-address controls, and admin user-targeted management.
- OpenAPI UI is available in runtime through Swagger configuration.

## Security and Business Rules
- JWT Bearer authentication required for protected routes.
- Authorization rules separated by route groups (user vs admin).
- Input validation with Jakarta Validation + domain rules.
- Postal code format validation by country.
- Rate limiting with Redis (`STANDARD` and `SENSITIVE` profiles).
- Soft delete behavior: records are marked inactive, not hard deleted.

## Observability
- Actuator endpoints expose health, info, and Prometheus metrics.
- Common metrics tags configured for easier dashboard filtering.
- Logback forwards logs to Loki in non-test profiles.
- Tracing sampling configured for full visibility.
- Metrics/logs go to the shared observability stack outside this monorepo.

## Run Locally
Requirements:
- Java 23
- Docker + Docker Compose (recommended for full stack)

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
- Unit and integration tests are under `src/test/`.
- `application-test.yml` provides test profile configuration.
- Recommended: run tests before opening PRs in the monorepo.

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
If this service changes its API contract, domain rules, or observability setup, update `docs/` and this `README.md` in the same PR.
