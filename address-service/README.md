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
- [Nginx Reverse Proxy and Load Balancer](#nginx-reverse-proxy-and-load-balancer)
- [Run Locally](#run-locally)
- [Docker and Full Local Stack](#docker-and-full-local-stack)
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
- **Nginx 1.27** (reverse proxy + load balancer)
- Docker / Docker Compose

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
├── nginx/
│   ├── nginx.conf                 # Reverse proxy + upstream load balancer config
│   └── ssl/
│       ├── generate-certs.sh      # Dev self-signed cert generator
│       ├── nginx.crt              # Gitignored — generated locally
│       └── nginx.key              # Gitignored — generated locally
├── observability/
│   ├── prometheus/
│   └── grafana/provisioning/datasources/
├── docs/
│   ├── observability-checklist.md
│   └── project/
│       ├── *.md                   # Human-readable docs
│       └── obsidian/*.md          # Structured source docs
├── Dockerfile
├── docker-compose.yml
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
- Grafana and Prometheus are provisioned through Docker Compose.

## Nginx Reverse Proxy and Load Balancer

All Docker traffic reaches the service through **Nginx** — the Spring Boot port `8443` is no longer bound to the host.

### Architecture

```
Client
  │
  ├─ HTTP  :80  ──► Nginx ──► 301 redirect to HTTPS
  │
  └─ HTTPS :443 ──► Nginx (TLS termination)
                      │   least_conn load balancing
                      ├──► address-service replica 1 :8443
                      ├──► address-service replica 2 :8443
                      └──► address-service replica N :8443
```

Nginx uses Docker's internal DNS (`127.0.0.11`) to resolve `address-service` — all replicas share that hostname and Docker round-robins between them. The `least_conn` directive sends each new request to the replica with the fewest active connections.

### Files

| Path | Purpose |
|------|---------|
| `nginx/nginx.conf` | Worker config, upstream block, HTTP→HTTPS redirect, HTTPS proxy server. |
| `nginx/ssl/nginx.crt` | TLS certificate for Nginx (self-signed dev cert, gitignored). |
| `nginx/ssl/nginx.key` | Private key (gitignored). |
| `nginx/ssl/generate-certs.sh` | One-shot script to generate the self-signed cert. |
| `nginx/ssl/.gitignore` | Prevents committing key material. |

### Generate the dev TLS certificate (first-time setup)

```bash
cd nginx/ssl
chmod +x generate-certs.sh
./generate-certs.sh
```

This creates `nginx.key` and `nginx.crt` (gitignored). The files are mounted read-only into the Nginx container.

### Scale the service (load balancing)

```bash
# Start with 3 application replicas behind Nginx
docker compose up -d --scale address-service=3

# Check running containers
docker compose ps
```

Nginx distributes requests across all `address-service` containers automatically through Docker DNS. No config change needed — just scale up or down.

### Key Nginx settings

| Setting | Value | Why |
|---------|-------|-----|
| `least_conn` | upstream directive | Routes to replica with fewest active connections. |
| `proxy_ssl_verify off` | per location | Backend uses a self-signed keystore; verification is bypassed inside the private Docker network. |
| `keepalive 32` | upstream | Reuses connections to backends for lower latency. |
| `proxy_set_header X-Forwarded-For` | every location | Backend can log real client IPs. |
| HTTP→HTTPS redirect | port 80 server block | Enforces TLS for all external clients. |

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

## Docker and Full Local Stack

From `address-service/`:

```bash
# 1. Generate Nginx TLS cert (first time only)
cd nginx/ssl && ./generate-certs.sh && cd ../..

# 2. Build and start the full stack (single replica)
docker compose up -d --build

# 3. Or start with N replicas for load balancing
docker compose up -d --build --scale address-service=3
```

Key endpoints (all external traffic via Nginx):

| Endpoint | URL |
|----------|-----|
| API (via Nginx HTTPS) | `https://localhost/api/v2/user/addresses` |
| Actuator health (via Nginx) | `https://localhost/actuator/health` |
| Prometheus metrics (via Nginx) | `https://localhost/actuator/prometheus` |
| Prometheus UI | `http://localhost:9090` |
| Loki ready | `http://localhost:3100/ready` |
| Grafana UI | `http://localhost:3000` |

> **Note:** `address-service:8443` is no longer exposed to the host. All API access goes through Nginx on port `443`.

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
