---
projectId: "store-service"
featured: false
name: "Store Service"
language: "Java"
category: "backend"
framework: "Spring Boot"
version: "2.0.0"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/store-service"
liveDemoUrl: null
description: "Hexagonal Spring Boot microservice for drugstore store catalog: CRUD-style commands, search and pagination, Redis caching, JWT security, Flyway/PostgreSQL, optional Kafka hook (stub), Docker Compose with Nginx, Prometheus, Loki, and Grafana."
techStack:
  - "Java 23"
  - "Spring Boot 3.5.14"
  - "Spring Security + JWT"
  - "PostgreSQL 15 + Flyway"
  - "Redis 7 (cache)"
  - "Micrometer Prometheus"
  - "Loki logback appender"
  - "Springdoc OpenAPI 2.8.x"
  - "Docker / Nginx 1.27"
status: "develop"
createdAt: "2026-01-01T00:00:00.000Z"
updatedAt: "2026-05-06T00:00:00.000Z"
---

# Project metadata

| Field | Value |
|--------|--------|
| **Gradle coordinates** | `io.github.alexisTrejo11.drugstore.microservices` : `2.0.0` |
| **JDK** | 23 (toolchain) |
| **Main class** | `io.github.alexisTrejo11.drugstore.stores.StoreServiceApplication` |
| **Config profiles** | `dev` (default in `application.yml`), `docker` (compose) |

Structured mirror of `docs/project/source/ProjectMetadata.md` front matter for non-Obsidian pipelines.

## Highlighted notes (important / missing / dangerous)

- **Danger — wrong service in default YAML:** `src/main/resources/application.yml` still uses **`spring.application.name: product-service`**, wrong datasource env key **`product_DB`**, and product-service logging paths. Local `dev` runs can point at the wrong database unless you override env vars or switch to `docker` profile.
- **Danger — secrets:** Example passwords may appear in YAML; use env-only secrets in shared or production environments.
- **Missing — Kafka:** `SPRING_KAFKA_BOOTSTRAP_SERVERS` is required by Compose, but `StoreEventPublisherImpl` does not publish to a topic.
- **Important — JWT env naming:** Docker profile uses **`JWT_SECRET_KEY`** (see `application-docker.yml`); local `application.yml` references **`JWT_SECRET`** / **`ISSUER`** — keep `.env` aligned with the active profile.
