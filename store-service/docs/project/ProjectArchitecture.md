# Architecture

Plain-Markdown counterpart to `docs/project/obsidian/ProjectArchitecture.md` (diagram model and YAML blocks live there; this file is readable in GitHub and editors).

---

## Layers

### 1. Driving adapters (inbound)

- **REST:** `StoreCommandController`, `StoreQueryController` under `/api/v2/stores`.
- **Concern:** Jakarta validation on request DTOs; `ResponseWrapper` for uniform JSON; Swagger annotations.

### 2. Application core

- **Use cases:** `StoreCommandUseCasesImpl`, `StoreQueryUseCasesImpl`.
- **Transactions:** `@Transactional` on commands.
- **Caching:** `@Cacheable` on hot reads; `@CacheEvict(allEntries = true)` on mutations for simplicity.

### 3. Domain

- **Aggregate:** `Store` with status enum (`ACTIVE`, `INACTIVE`, `UNDER_MAINTENANCE`, `TEMPORARILY_CLOSED`, `UNKNOWN`).
- **Specifications / criteria:** Search pipeline builds JPA queries from `StoreSearchCriteria` and `SearchStoresQuery`.

### 4. Driven adapters (outbound)

- **Persistence:** `StoreEntity`, `StoreEntityMapper`, `StoreRepositoryImpl` (JPA).
- **Integration:** `StoreEventPublisherImpl` — **currently logs only** (no Kafka send).

---

## Design patterns

- **Hexagonal architecture** — domain does not depend on Spring or JPA types.
- **Transactional script** with rich domain methods (`relocate`, `updateSchedule`, status operations).

---

## Scalability & reliability

- **Horizontal:** Multiple `store-service` replicas behind Nginx; shared Redis for cache coherence.
- **Database:** Single Postgres instance in compose; production would add replicas / connection pool tuning (`application-docker.yml` Hikari settings).
- **Not implemented:** Read replicas, saga outbox, idempotency keys for commands.

---

## Security strategies

- **JWT** in `JwtAuthenticationFilter` (libs_kernel).
- **Authorization:** `SecurityConfig` — **all GET** under `/api/v2/stores/**` are **permitAll**; **mutations** require `ADMIN` or `MANAGER`.
- **CSRF:** Disabled (typical for token APIs).
- **Rate limits:** `@RateLimit` with profiles; two PATCH endpoints lack annotations (see Feature doc).

**Danger / doc drift:** OpenAPI `@SecurityRequirement(bearerAuth)` on controllers does not match anonymous GET behavior.

---

## Caching

| Cache name | Typical key | TTL (default config) | Notes |
|------------|-------------|----------------------|--------|
| `stores` | id or code | 30 minutes | Per `CacheConfig` |
| `store_searches` | `query.toString()` | 30 minutes | Fragile key — changing `toString` invalidates behavior |
| `store_status` | status + page + size | 30 minutes | Paged listings |

**Danger:** `CacheConfig` enables Jackson **default typing** for cached values — treat Redis as a trusted zone or harden serialization.

---

## Data flow (request path)

1. **Client → Nginx → Spring** — TLS at edge; forward headers strategy `native` for correct scheme behind proxy.
2. **Filter chain** — JWT optional for reads; validation for writes.
3. **Use case** — hits Redis on cacheable methods; falls through to Postgres.
4. **Events (intended)** — `StoreStatusChangedEvent` should go to Kafka; **not wired**.

---

## Technology decisions (summary)

| Decision | Rationale | Trade-off |
|----------|-----------|-----------|
| JSONB `schedule_config` | Flexible hours / exceptions | Must validate in app, not only DB |
| Redis shared cache | Cross-replica consistency vs Caffeine | Network hop + serialization risk |
| Flyway | Repeatable schema | Need discipline for zero-downtime migrations |

---

## Deployment topology (logical)

```text
[Client] --HTTPS:443--> [Nginx] --HTTP--> [store-service :8080]
                                              |        |
                                         [Postgres] [Redis]
```

Observability sidecars/containers: **Prometheus**, **Loki**, **Grafana** on `drugstore-network`.
