# API schema

REST API for **store-service**. Authoritative interactive spec: **`/swagger-ui.html`** and **`/v3/api-docs`** when `SWAGGER_ENABLED=true`.

Twin of `docs/project/obsidian/APISchema.md`: that file carries **structured YAML** for tooling; this file is **human-readable** tables and notes.

---

## Global

| Item | Value |
|------|--------|
| **Base path** | `/api/v2/stores` |
| **Response envelope** | `libs_kernel.response.ResponseWrapper` (+ `PageResponse` for lists) |
| **Docs** | OpenAPI 3 via Springdoc |
| **Version prefix** | `v2` (no separate URL version header) |

### Security reality vs OpenAPI

- Controllers use `@SecurityRequirement(name = "bearerAuth")` for Swagger.
- **`SecurityConfig` allows unauthenticated GET** on `/api/v2/stores/**`.
- Mutations require JWT with roles **`ROLE_ADMIN`** or **`ROLE_MANAGER`** (Spring convention: authorities typically `ADMIN`/`MANAGER` in `hasRole` form — confirm `JwtAuthenticationFilter` prefix).

### Rate limiting (profiles)

Configured under `app.rate-limit` in **`application.yml`** (dev) and **`application-docker.yml`** (containers). Annotation profiles:

| Profile | Typical use in code | Docker YAML hint |
|---------|---------------------|------------------|
| `SENSITIVE` | `POST /stores`, `DELETE /stores/{id}` | Override via `RATE_LIMIT_SENSITIVE` |
| `STANDARD` | PUT/PATCH except two gaps below | `RATE_LIMIT_STANDARD` |
| `PUBLIC` | GET store APIs | `RATE_LIMIT_PUBLIC` |

**Missing annotations:** `PATCH /{id}/temporary-closure`, `PATCH /{id}/deactivate` → **no** `@RateLimit`.

---

## Endpoints

### Commands (authenticated: JWT + role)

| Method | Path | Summary | Rate limit |
|--------|------|---------|------------|
| POST | `/api/v2/stores` | Create store | SENSITIVE |
| PUT | `/api/v2/stores/{id}/location` | Update address + geo | STANDARD |
| PUT | `/api/v2/stores/{id}/schedule` | Update hours JSON schedule | STANDARD |
| PATCH | `/api/v2/stores/{id}/under-maintenance` | Set maintenance | STANDARD |
| PATCH | `/api/v2/stores/{id}/temporary-closure` | Temp close | **none** |
| PATCH | `/api/v2/stores/{id}/activate` | Activate | STANDARD |
| PATCH | `/api/v2/stores/{id}/deactivate` | Deactivate | **none** |
| DELETE | `/api/v2/stores/{id}` | Delete store | SENSITIVE |

**Request body (create):** See `CreateStoreRequest` — fields `code`, `name`, `status`, `contactInfo`, `address`, `schedule`, `geolocation` (all required with nested validation).

### Queries (JWT optional — permitAll)

| Method | Path | Summary | Rate limit |
|--------|------|---------|------------|
| GET | `/api/v2/stores/{id}` | By UUID | PUBLIC |
| GET | `/api/v2/stores/by-code/{code}` | By business code | PUBLIC |
| GET | `/api/v2/stores` | Search + filters (`SearchStoreRequest` query params) | PUBLIC |
| GET | `/api/v2/stores/status/{status}` | By `StoreStatus` enum + pagination | PUBLIC |

**Success shape (single):** `StoreResponse` — `id`, `code`, `name`, `status`, `phone`, `email`, `address`, `latitude`, `longitude`, `isOpen`, `createdAt`, `updatedAt`.

### Infrastructure / docs

| Method | Path | Notes |
|--------|------|--------|
| GET | `/actuator/health` | Healthcheck |
| GET | `/actuator/prometheus` | Metrics (scoped in docker profile) |
| GET | `/v3/api-docs` | OpenAPI JSON |
| GET | `/swagger-ui.html` | Swagger UI |

---

## Status enum (`StoreStatus`)

`ACTIVE`, `INACTIVE`, `UNDER_MAINTENANCE`, `TEMPORARILY_CLOSED`, `UNKNOWN`

---

## Example cURL (placeholder JWT)

```bash
# Create (requires ADMIN/MANAGER token)
curl -sS -X POST "http://localhost:8080/api/v2/stores" \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"code":"STR-001","name":"Central","status":"ACTIVE",...}'

# Public read (no Authorization header — allowed by SecurityConfig)
curl -sS "http://localhost:8080/api/v2/stores/by-code/STR-001"
```

---

## Highlighted risks

- **Danger:** Anonymous GET exposes whatever fields `StoreResponse` returns — revise before production if sensitive.
- **Danger:** Swagger “Try it out” disabled in docker profile — developers may enable insecurely on edge hosts.
- **Missing:** Rate limits not applied to two PATCH routes; add for abuse parity.
- **Important:** Separate **`JWT_SECRET`** vs **`JWT_SECRET_KEY`** between `application.yml` and `application-docker.yml` when switching profiles.
