# Project features

Mirrors `docs/project/source/ProjectFeature.md` in prose form for standard Markdown viewers.

---

## 1. REST API v2 — store lifecycle

- **What:** Create store (nested DTO graph), update location and schedule, patch operational status, delete.
- **Roles:** `ADMIN` or `MANAGER` for mutating HTTP methods (see `SecurityConfig`).
- **Rate limits:** Most commands use `SENSITIVE` or `STANDARD` profiles; **`PATCH /{id}/temporary-closure`** and **`PATCH /{id}/deactivate`** have **no** `@RateLimit` — **gap**.
- **Important:** OpenAPI tags indicate admin operations; still verify gateway-level auth in production.

---

## 2. Query, search, pagination

- **Endpoints:** `GET /{id}`, `GET /by-code/{code}`, `GET /` (search), `GET /status/{status}`.
- **Security:** **Anonymous allowed** for all GETs under `/api/v2/stores/**`.
- **Pagination:** `libs_kernel.page.PageRequest` with defaults in `StoreQueryController` for status listing.
- **Danger:** If response payloads ever include **PII**, public GETs become a **data leak** — review field set and add auth or BFF filtering if needed.

---

## 3. Redis caching

- **Regions:** `stores`, `store_searches`, `store_status`; TTL **30 minutes** in `CacheConfig`.
- **Invalidation:** Command use cases **`@CacheEvict(..., allEntries = true)`** — simple but can spike Redis on bulk admin jobs.
- **Danger:** Jackson **default typing** in cache serializer — security review required.

---

## 4. JWT + Spring Security

- **Stateless** session; **CSRF off**; JWT filter inserted before username/password filter.
- **Public:** Swagger and actuator subsets (`health`, `info`, `prometheus` in docker profile).
- **Mismatch:** Controller-level `@SecurityRequirement(bearerAuth)` **overstates** requirement for reads.

---

## 5. Observability (Compose)

- **Metrics:** Micrometer Prometheus registry; scrape via Prometheus container.
- **Logs:** stdout → shared Promtail → Loki.
- **Dashboards:** Grafana with admin password from env in compose (**change**).

---

## 6. Persistence & migrations

- **Flyway** script `V1__create_store_table.sql` defines `stores` with JSONB schedule and indexes (code, status, geo, GIN on schedule).
- **Docker:** `ddl-auto: validate` — schema must match entities.

---

## 7. Integration events (stub)

- **Port:** `StoreEventPublisher`.
- **Implementation:** Logs only — **does not publish** to Kafka.
- **Missing:** Retry, dead-letter, outbox, topic naming conventions.
- **Operational oddity:** Compose still **requires** `SPRING_KAFKA_BOOTSTRAP_SERVERS`.

---

### Summary table

| Feature | Status | Risk / note |
|---------|--------|-------------|
| REST commands | Implemented | Rate limit gaps on 2 PATCH routes |
| REST queries | Implemented | Anonymous GET — data exposure risk |
| Redis cache | Implemented | Serialization / default typing |
| JWT RBAC | Implemented | Swagger vs runtime mismatch |
| Kafka events | Not implemented | Env still required |
| TLS edge | Implemented (compose) | Dev certs only |
