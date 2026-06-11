# Drugstore Platform

Drugstore Platform is a **multi-service Spring Boot monorepo** for e-commerce and pharmacy-style domain flows: identity, catalog, carts, orders, payments, stores, staff, inventory, notifications, and addresses. Services communicate over **REST**, **Kafka**, and **gRPC** where needed, with **PostgreSQL**, **Redis**, and **Flyway** for persistence and schema evolution.

## Table of Contents

- [What is in this repository](#what-is-in-this-repository)
- [Service catalog](#service-catalog)
- [Cross-cutting platform components](#cross-cutting-platform-components)
- [Documentation layout](#documentation-layout)
- [Observability](#observability)
- [Local development](#local-development)
- [Further reading](#further-reading)

## What is in this repository

| Area | Purpose |
|------|---------|
| `*-service/` | Domain microservices (each owns its API, data, and docs). |
| `admin-service/` | Spring Boot Admin server — [Admin Service README](admin-service/README.md). |
| `kafka-infrastrucuture/` | Kafka-oriented local or reference setup ([readme](kafka-infrastrucuture/readme.md)). |
| `config-data/` | Shared configuration assets for config server workflows. |
| `libs/` | Shared Java libraries (for example `shared-kernel`). |

## Service catalog

Each row links to that service’s **root README** (same pattern as `address-service`: overview, stack, run instructions, and links to `docs/project/generated/*.md`).

| Service | README | Role (high level) |
|---------|--------|-------------------|
| Address | [address-service](address-service/README.md) | User and admin address CRUD, validation, defaults. |
| Auth | [auth-service](auth-service/README.md) | JWT, sessions, OAuth2, Kafka auth events, gRPC to users. |
| Cart | [cart-service](cart-service/README.md) | Shopping cart persistence, cache, Kafka integration. |
| Employee | [employee-service](employee-service/README.md) | Employee domain and HR-related APIs. |
| Inventory | [inventory-service](inventory-service/README.md) | Stock and inventory rules, cache, messaging. |
| Notification | [notification-service](notification-service/README.md) | Outbound notifications and delivery pipelines. |
| Order | [order-service](order-service/README.md) | Order lifecycle, persistence, integrations. |
| Payment | [payment-service](payment-service/README.md) | Payments (e.g. Stripe), webhooks, persistence. |
| Product | [product-service](product-service/README.md) | Product catalog, Kafka product events. |
| Store | [store-service](store-service/README.md) | Store master data and related APIs. |
| User | [user-service](user-service/README.md) | User profiles, Flyway, Kafka, gRPC server. |
| Admin | [admin-service](admin-service/README.md) | Spring Boot Admin server for registered clients. |

## Cross-cutting platform components

- **Kafka** — Async integration between services (topics per domain; see `kafka-infrastrucuture/` and each service’s docs under `docs/project/generated/ProjectInfrastructure.md`).
- **Spring Boot Admin** — Several services register as **admin clients** (`de.codecentric:spring-boot-admin-starter-client`); the **admin server** lives under `admin-service/` for a single operations entrypoint.
- **Config** — Services that use Spring Cloud Config consume shared settings from your config server / `config-data/` as documented per service.
- **Shared kernel** — `libs/shared-kernel` (and published coordinates where used) for cross-cutting types, audit helpers, etc.

## Documentation layout

Every service with `docs/project/` follows the **address-service** convention:

- **`docs/project/generated/*.md`** — Human-readable Markdown (no Obsidian YAML frontmatter).
- **`docs/project/source/*.md`** — Structured source docs (frontmatter + body) for tooling or Obsidian.

Start from a service README, then open **Project Overview** and **Project Architecture** for depth.

## Observability

Services that ship a local stack expose **Prometheus** (metrics scrape), **Loki** (log ingestion), and **Grafana** (dashboards + datasources), aligned with `address-service`:

- Actuator: `health`, `info`, `prometheus` where enabled.
- Logback **Loki4j** appender pushing to `http://loki:3100` in non-test profiles (per service `logback-spring.xml` and `docker-compose.yml`).

Ports are **per compose file** (often `9090`, `3100`, `3000` on localhost); check each service’s compose file before running several stacks at once.

## Local development

Requirements (typical):

- **Java 23** (toolchain in Gradle).
- **Docker** + **Docker Compose** for databases, Redis, Kafka, and observability stacks.

Per service:

```bash
cd <service-name>
./gradlew bootRun
./gradlew test
```

With Docker (when `docker-compose.yml` exists):

```bash
cd <service-name>
docker compose up -d --build
```

## Further reading

- [Address Service README](address-service/README.md) — reference layout for all service READMEs.
- [Kafka infrastructure](kafka-infrastrucuture/readme.md) — messaging setup notes.

---

When you add or remove a service, update this root **README** and the [Service catalog](#service-catalog) table in the same change.
