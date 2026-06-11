# Project Features

Plain-Markdown twin of **`docs/project/source/ProjectFeature.md`**.

## Authenticated user reads (`UserQueryManagerController`)

- **Endpoints:** `GET /api/v2/users/{id}`, `.../by-email/{email}`, `.../by-phone/{phone}`, `.../by-role/{role}`, `.../by-status/{status}`  
- **Auth:** Bearer JWT (**any authenticated** `/api/**` principal).  
- **Pagination:** `by-role` / `by-status` accept `libs_kernel.page.PageRequest` query params (**tests use `page=1`** — confirm zero vs one indexing in `PageRequest` implementation before API clients rely on semantics).  

## Manager / admin lifecycle (`UserManagerController`)

| Method | Path | Role gate |
|--------|------|-----------|
| POST | `/api/v2/users/manager/` | `ADMIN`, `MANAGER` |
| PATCH | `/api/v2/users/manager/{id}/ban` | same |
| PATCH | `/api/v2/users/manager/{id}/unban` | same |
| PATCH | `/api/v2/users/manager/{id}/activate/code/{activationCode}` | same |
| DELETE | `/api/v2/users/manager/{id}` | **destructive — verify domain semantics** |

- **Observation:** POST always maps `UserRole.CUSTOMER`; **staff-only creation of EMPLOYEE** roles is **not exposed** via this controller (potential **missing capability** vs full IAM needs).

## Profile (`ProfileController`)

- `GET /api/v2/users/profile/me` • `PATCH /api/v2/users/profile`  
- **PII-bearing** payloads — ensure audit logging + field-level masking in gateways if required by policy.  

## Kafka consumption (`UserEventConsumer`)

Topics from config keys: **`kafka.topics.user.created|updated|deleted`**. Manual commit after successful handler invocation.

### Missing / risky

- **Dead letter queue** absent.  
- **Retry policy** unspecified at listener level (`KafkaErrorHandler` exists — verify backoff wiring).  

## gRPC (`user_service.proto` + `UserGrpcServer`)

- **Contract completeness:** uniqueness checks, credential validation, enrollment RPCs declared.  
- **Runtime completeness:** **`UserGrpcServer` not registered with a listener** • treat as **`not-implemented`** for SLO/accountability.  

## Observability bundle (Compose)

Prometheus scrape + Grafana + Loki provisioning under `observability/`. Grafana ships **weak default password** (**rotate**).

## Dummy / future features (explicit placeholders)

| ID | Feature | Status |
|----|---------|--------|
| `sso-oidc` | Enterprise OIDC federation | placeholder — not wired |
| `outbox-pattern` | Transactional Kafka outbox for producers | placeholder — inbound only today |
| `geo-read-replicas` | Read replicas for global queries | cloud placeholder |
