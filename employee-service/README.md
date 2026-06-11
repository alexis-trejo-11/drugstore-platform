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
- [Nginx Reverse Proxy and Load Balancer](#nginx-reverse-proxy-and-load-balancer)
- [Run Locally](#run-locally)
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
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
├── nginx/
│   ├── nginx.conf
│   └── ssl/
│       ├── .gitignore
│       └── generate-certs.sh
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

- REST API under `/api/**` (see Springdoc packages and `docs/project/generated/APISchema.md`).

## Security and Business Rules

- JWT and role checks per endpoint groups.
- Rate limiting configuration in `application.yml`.

## Observability

- Actuator + Prometheus.
- Logback Loki appender (non-test profiles).
- Optional local stack via `docker-compose.yml`.

## Nginx Reverse Proxy and Load Balancer

External traffic now enters through Nginx over HTTPS. The app remains internal on port `8081`.

### Architecture

```text
Client -> Nginx :443 (TLS termination) -> employee-service :8081 (internal HTTP)
Client -> Nginx :80  (redirect)        -> HTTPS
```

### Setup

```bash
cd employee-service
chmod +x nginx/ssl/generate-certs.sh
./nginx/ssl/generate-certs.sh   # required before nginx starts (creates nginx.crt / nginx.key)
```

If nginx fails with `PEM_read_bio_X509` errors, Docker may have created empty `nginx.crt`/`nginx.key` **directories**. Stop nginx, remove them, and re-run the script:

```bash
docker compose stop nginx
rm -rf nginx/ssl/nginx.crt nginx/ssl/nginx.key
./nginx/ssl/generate-certs.sh
```

### Scale

```bash
docker compose up -d --build --scale employee-service=3
```

Nginx uses Docker DNS + `least_conn` to distribute traffic across replicas.

## Run Locally

```bash
cp .env.example .env
# Edit .env: fill every REQUIRED variable (JWT, DB credentials, GitHub token, etc.)
./gradlew bootRun
./gradlew test
```

Spring Boot loads `./.env` via `spring.config.import` in `application.yml`. Gradle also reads `.env` for `bootRun` and GitHub Packages credentials (see `build.gradle`). Never commit `.env` (gitignored at repo root).

## Docker and Full Local Stack

```bash
cp .env.example .env
# Edit .env (GITHUB_ACTOR, GITHUB_TOKEN, JWT, DB credentials, etc.), then:
docker compose up -d --build
```

Docker **build** needs `GITHUB_ACTOR` and `GITHUB_TOKEN` in `.env` so Compose can pass them as `build.args` (see `dockerfile`). Local `./gradlew` reads the same `.env` file directly.

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
