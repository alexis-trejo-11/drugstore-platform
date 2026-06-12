# Employee Service

Employee Service is an internal microservice of the Drugstore Platform monorepo.  
It owns **employee** domain data and APIs: profiles, assignments, and integration with platform security and messaging.

## Table of Contents

- [Overview](#overview)
- [Core Capabilities](#core-capabilities)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [API Surface](#api-surface)
- [Security and Business Rules](#security-and-business-rules)
- [Observability](#observability)
- [Docker and Full Local Stack](#docker-and-full-local-stack)
- [Testing](#testing)
- [Documentation Navigation](#documentation-navigation)

## Overview

- **Service name:** `employee-service`
- **Role in platform:** Employee master data and operations exposed to admin and internal consumers.
- **Main responsibility:** CRUD and query APIs for employees, Flyway-managed schema, Redis cache, Kafka where used.
- **Protocol:** REST (HTTP in default dev profile; align with your deployment TLS strategy).
- **Persistence:** PostgreSQL with Flyway.

## Core Capabilities

- Employee persistence and validation.
- Spring Security–protected APIs.
- Redis-backed cache configuration.
- Kafka integration for downstream events (see infrastructure docs).
- Springdoc OpenAPI for API exploration.

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Security, Spring Data JPA, Spring Data Redis, Spring Kafka
- PostgreSQL, Flyway
- Spring Cloud Config (bootstrap) when enabled
- Spring Boot Admin client
- Actuator + Micrometer + Prometheus
- Loki4j + Loki + Grafana
- Nginx 1.27 (reverse proxy + load balancer)
- Docker / Docker Compose

## Project Structure

```text
employee-service/
├── .env.example                   # All env vars (copy to .env at project root)
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-docker.yml
│   │       └── logback-spring.xml
│   └── test/
├── docker/                        # All Docker assets (see docker/README.md)
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

- REST API under `/api/**` (see Springdoc packages and `docs/project/generated/APISchema.md`).

## Security and Business Rules

- JWT and role checks per endpoint groups.
- Rate limiting configuration in `application.yml`.

## Observability

- Actuator + Prometheus.
- Logback Loki appender (non-test profiles).
- Optional local stack via `docker/docker-compose.full.yml` (see [docker/README.md](docker/README.md)).

## Nginx Reverse Proxy and Load Balancer

External traffic enters through Nginx over HTTPS. The app remains internal on port `8080`.

### Architecture

```text
Client -> Nginx :443 (TLS termination) -> employee-service :8080 (internal HTTP)
Client -> Nginx :80  (redirect)        -> HTTPS
```

See [docker/README.md](docker/README.md) for TLS setup, scaling, and compose profiles.

## Run Locally

Requirements:
- Java 23
- Docker + Docker Compose (recommended for full stack)

Application only:

```bash
cp .env.example .env
# Edit .env — set JWT_SECRET_KEY, DB_USER, DB_PASSWORD, and connection URLs for local JVM
./gradlew bootRun
```

Run tests:

```bash
./gradlew test
```

Spring Boot loads `./.env` via `spring.config.import` in `application.yml`. Gradle also reads `.env` for `bootRun` and GitHub Packages credentials (see `build.gradle`). Never commit `.env` (gitignored at repo root).

## Docker

All containerization lives under **`docker/`**. See **[docker/README.md](docker/README.md)** for compose files, profiles, and run commands.

Quick start (full local stack):

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
| `docker-compose.app.yml` | App + Nginx only (external DB/Redis) |

Two profiles: **`local`** (bundled or host infrastructure) and **`prod`** (cloud RDS, ElastiCache, etc.).

Main health endpoint (via Nginx): `https://localhost/actuator/health`.

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
