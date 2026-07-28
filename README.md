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
| `config-data/` | Shared configuration assets for config server workflows. |
| `libs/` | Shared Java libraries (for example `shared-kernel`). |

Shared infrastructure (databases, Kafka, Prometheus/Loki/Grafana) is **not** in this monorepo; it lives in your homelab / cloud and is consumed via `.env` + external Docker networks.

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

- **Kafka** — Async integration between services (topics per domain). Brokers are provided by shared infra; set `KAFKA_BOOTSTRAP_SERVERS` in each service `.env`.
- **Spring Boot Admin** — Several services register as **admin clients** (`de.codecentric:spring-boot-admin-starter-client`); the **admin server** lives under `admin-service/` for a single operations entrypoint.
- **Config** — Services that use Spring Cloud Config consume shared settings from your config server / `config-data/` as documented per service.
- **Shared kernel** — `libs/shared-kernel` (and published coordinates where used) for cross-cutting types, audit helpers, etc.

## Documentation layout

Every service with `docs/project/` follows the **address-service** convention:

- **`docs/project/generated/*.md`** — Human-readable Markdown (no Obsidian YAML frontmatter).
- **`docs/project/source/*.md`** — Structured source docs (frontmatter + body) for tooling or Obsidian.

Start from a service README, then open **Project Overview** and **Project Architecture** for depth.

## Observability

Services expose **Actuator** (`health`, `info`, `prometheus` where enabled) and log to **stdout**. Shared **Promtail** ships container logs to Loki; **Prometheus** scrapes metrics. Observability lives outside this monorepo.

## Local development

Requirements (typical):

- **Java 23** (toolchain in Gradle).
- **Docker** + **Docker Compose** for running a service container against shared infra.
- External networks `infra_central_network` and `shared_app_network` (see [docs/docker-local-dev.md](docs/docker-local-dev.md)).

Per service (Gradle):

```bash
cd <service-name>
./gradlew bootRun
./gradlew test
```

With Docker (app-only compose at the service root):

```bash
cd <service-name>
cp .env.example .env
docker compose up -d --build
```

Full Docker notes: [docs/docker-local-dev.md](docs/docker-local-dev.md).

## Further reading

- [Address Service README](address-service/README.md) — reference layout for all service READMEs.
- [Docker local development](docs/docker-local-dev.md) — compose, networks, ports.

---

When you add or remove a service, update this root **README** and the [Service catalog](#service-catalog) table in the same change.
