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
- [Nginx Reverse Proxy and Load Balancer](#nginx-reverse-proxy-and-load-balancer)
- [Run Locally](#run-locally)
- [Docker and Full Local Stack](#docker-and-full-local-stack)
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
- Loki4j + Loki + Grafana
- Nginx 1.27 (reverse proxy + load balancer)
- Docker / Docker Compose

## Project Structure

```text
auth-service/
├── src/
│   ├── main/
│   │   ├── java/                  # Controllers, security, Kafka, gRPC clients
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application.docker.yml
│   │       └── logback-spring.xml
│   └── test/
├── docker/                        # All Docker / Compose configuration
│   ├── Dockerfile
│   ├── docker-compose.full.yml    # App + Redis + monitoring
│   ├── docker-compose.app.yml     # App + Nginx only
│   ├── README.md                  # How to run each profile
│   ├── nginx/
│   └── observability/
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

- Actuator exposes health, info, and Prometheus metrics (see `application.docker.yml`).
- Logback **Loki4j** appender sends logs to Loki in non-test profiles.
- `docker/docker-compose.full.yml` includes Prometheus, Loki, and Grafana with provisioned datasources.

## Nginx Reverse Proxy and Load Balancer

All external traffic enters the stack through Nginx — the auth-service port is not published on the host except for dev tools on **8082**.

### Architecture

```
Client → Nginx :443 (TLS termination) → auth-service :8080 (internal HTTP)
Client → Nginx :80  (redirect)        → HTTPS
Client → host :8082 (dev only)        → auth-service :8080 (Swagger, etc.)
```

### Files

| File | Purpose |
|------|---------|
| `docker/nginx/nginx.conf` | Upstream `auth_backend`, TLS config, proxy rules |
| `docker/nginx/ssl/generate-certs.sh` | Generates self-signed `nginx.key` + `nginx.crt` for dev |
| `docker/nginx/ssl/.gitignore` | Prevents committing private key material |

### Generate certificates (first time)

```bash
cd auth-service/docker
chmod +x nginx/ssl/generate-certs.sh
./nginx/ssl/generate-certs.sh
```

### Scale horizontally

```bash
cd auth-service/docker
docker compose -f docker-compose.full.yml --profile local --env-file .env --env-file .env.local up -d --scale auth-service=3
```

Docker DNS resolves `auth-service` to all running replicas. Nginx distributes connections with `least_conn`.

### Key Nginx settings

| Setting | Value | Purpose |
|---------|-------|---------|
| `upstream auth_backend` | `server auth-service:8080` | Docker DNS expands to replicas |
| Load-balancing policy | `least_conn` | Routes to replica with fewest active connections |
| `proxy_ssl_verify` | `off` | Trusts internal self-signed cert on Docker network |
| `client_max_body_size` | `1m` | Auth payloads are small |
| HTTP → HTTPS redirect | `:80 → :443` | Force encrypted connections |

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

## Docker and Full Local Stack

All Docker configuration lives in **`docker/`**. See **[docker/README.md](docker/README.md)** for profiles, env files, and run commands.

Quick start (full local stack):

```bash
cd auth-service/docker
cp .env.example .env && cp .env.local.example .env.local
# Edit .env — set JWT_SECRET_KEY, GITHUB_ACTOR, GITHUB_TOKEN
./nginx/ssl/generate-certs.sh
docker compose -f docker-compose.full.yml --profile local --env-file .env --env-file .env.local up -d --build
```

Typical URLs:

- Service health (via Nginx): `https://localhost/actuator/health`
- Swagger UI (direct): `http://localhost:8082/swagger-ui.html`
- Prometheus UI: `http://localhost:9090`
- Loki: `http://localhost:3100`
- Grafana: `http://localhost:3000`

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
