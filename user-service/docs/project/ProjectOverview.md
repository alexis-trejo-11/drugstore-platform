# Overview

Central **user-service** microservice for the drugstore platform: **JWT REST APIs** for querying users, **manager lifecycle** mutations, **profile** read/write, **Kafka** consumption on `user.*` topics, and a **protobuf gRPC definition** (`user_service.proto`) with a **`UserGrpcServer` implementation that is not yet bound to a listening server**.

## Problem

- Operators and other services require a canonical user record (identity, phone, role, status) and lifecycle operations (**ban/unban/activate/delete**).
- Downstream aggregates react to domain events (**created / updated / deleted**).

## Solution

| Area | Approach |
|------|-----------|
| API | Hexagonal layering; **CommandBus / QueryBus** for writes and reads |
| Security | Stateless **JWT** via `libs_kernel` filter; **`/manager/**`** requires **ADMIN or MANAGER** |
| Persistence | PostgreSQL + Spring Data JPA |
| Speed / abuse | Redis; **rate limit** profiles (`app.rate-limit` + annotations) |
| Messaging | `@KafkaListener` + manual **acknowledgment** |
| Edge (local) | **Nginx** TLS in Docker Compose → `user-service:8080` |

## Highlighted risks (danger / missing)

| Severity | Item | Detail |
|---------|------|--------|
| **High** | `application-docker.yml` | Content still references **store-service** (wrong `spring.application.name`, DB defaults, springdoc packages, Prometheus tags). Correct before trusting Docker deployments. |
| **High** | Springdoc scan | **`packages-to-scan`** points at `...drugstore.**stores**` instead of **`...users`** → OpenAPI/UI may omit user endpoints. |
| **Medium** | gRPC runtime | **`UserGrpcServer`** exists but no **`ServerBuilder` / spring-grpc** lifecycle → **no RPC listener** despite `GRPC_PORT`. |
| **Medium** | Kafka failures | Consumer **throws** without **DLQ**; unacked offsets → **potential tight retry**. |
| **Medium** | Dev defaults | **`jwt.secret` placeholder**, **`flyway.disabled` + `ddl-auto: update`**, actuator **`expose: *`** in `application.yml` — not production posture. |

## Placeholder catalogue (fills empty template slots)

| Item | Dummy / placeholder |
|------|---------------------|
| GitHub repo | `https://github.com/PLACEHOLDER/drugstore-platform` |
| Public demo | `(none)` |
| Cover image URL | Use any placeholder CDN image for Obsidian canvases |

## Operational metrics (placeholders until measured)

- **Artifact version:** `2.0.0` (Gradle).
- **Java:** `23`.
- Target SLO strings (dummy): *p95 read < 150 ms*, *availability 99.9%* — not wired to real dashboards in-repo.
