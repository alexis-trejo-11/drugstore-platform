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
├── .env.example
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
cp .env.example .env   # set JWT_SECRET_KEY, DATASOURCE_URL, REDIS_URL for local Postgres/Redis
./gradlew bootRun
./gradlew test
```

## Docker

All containerization lives under **`docker/`**. See **[docker/README.md](docker/README.md)** for compose files, profiles, and run commands.

Quick start (full local stack):

```bash
cp .env.example .env
# Edit .env — set JWT_SECRET_KEY and connection URLs
chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

Two compose files: `docker-compose.full.yml` (app + infra + monitoring), `docker-compose.app.yml` (app + Nginx only).

Two profiles: **`local`** (bundled or host infrastructure) and **`prod`** (cloud RDS, ElastiCache, MSK, etc.).

## Testing

- H2 for tests where configured.

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
