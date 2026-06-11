# Cart Service

Cart Service is an internal microservice of the Drugstore Platform monorepo.  
It owns **shopping cart** persistence and behavior: line items, pricing context, cache, and integration with catalog and checkout flows via REST and Kafka.

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

- **Service name:** `cart-service`
- **Role in platform:** Session-scoped or user-scoped cart storage and orchestration.
- **Main responsibility:** Maintain carts, apply domain rules, expose HTTPS API, consume product-related events where configured.
- **Protocol:** REST over HTTPS (default), optional gRPC server for inter-service calls.
- **Persistence:** PostgreSQL with Flyway; Redis for cache.

## Core Capabilities

- Cart CRUD and validation.
- Redis-backed caching.
- Kafka consumer/producer integration for product or domain events.
- JWT-secured HTTP API (aligned with platform auth).
- Rate limiting profiles (see configuration).

## Tech Stack

- Java 23
- Spring Boot 3.3.2
- Spring Web, Spring Security, Spring Data JPA, Spring Data Redis, Spring Cache, Spring Kafka
- PostgreSQL, Flyway
- gRPC (Protobuf)
- Springdoc OpenAPI
- Spring Boot Admin client
- Actuator + Micrometer + Prometheus
- Loki4j + Loki + Grafana
- Nginx 1.27 (reverse proxy + load balancer)
- Docker / Docker Compose

## Project Structure

```text
cart-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-docker.yml
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
├── dockerfile
├── build.gradle
└── README.md
```

## API Surface

- REST API under versioned paths (see OpenAPI and `docs/project/generated/APISchema.md`).
- Swagger UI when enabled for the active profile.

## Security and Business Rules

- JWT authentication for protected routes.
- Input validation and cart ownership rules enforced in the application layer.

## Observability

- Actuator + Prometheus metrics.
- Logback pushes to Loki outside test profile.
- Local stack via `docker-compose.yml` (Prometheus, Loki, Grafana).

## Nginx Reverse Proxy and Load Balancer

External traffic enters through Nginx; `cart-service` runs on internal HTTPS `:8443` only.

### Architecture

```text
Client -> Nginx :443 (TLS termination) -> cart-service :8443 (internal HTTPS)
Client -> Nginx :80  (redirect)        -> HTTPS
```

### Setup

```bash
cd cart-service
chmod +x nginx/ssl/generate-certs.sh
./nginx/ssl/generate-certs.sh
```

### Scale

```bash
docker compose up -d --build --scale cart-service=3
```

Nginx uses `least_conn` and Docker DNS (`cart-service`) to distribute traffic across replicas.

## Run Locally

Requirements:

- Java 23
- PostgreSQL and Redis (or Docker Compose)

```bash
./gradlew bootRun
./gradlew test
```

## Docker and Full Local Stack

From `cart-service/`:

```bash
docker compose up -d --build
```

Typical endpoints:

- Health (via Nginx): `https://localhost/actuator/health`
- Prometheus / Grafana / Loki: `9090`, `3000`, `3100` on localhost when not conflicting with other stacks.

## Testing

- Tests under `src/test/` including H2-backed tests where configured.

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
