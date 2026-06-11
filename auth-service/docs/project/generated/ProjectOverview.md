# Overview

Plain-Markdown twin of `docs/project/source/ProjectOverview.md` (same facts, no Obsidian YAML front matter).

---

## Problem statement

**Title:** Centralized authentication in a microservices architecture.

**Description:** In a distributed e-commerce platform, authentication must be centralized to avoid duplication across services. Requirements include JWT token management, refresh token rotation, two-factor authentication, OAuth2 social login, password reset flows, and publishing auth events to other services via Kafka.

**Problems addressed**

- No centralized auth service across microservices
- JWT access + refresh token management needed
- Two-factor authentication (TOTP) for enhanced security
- OAuth2 social login (Google, GitHub, etc.)
- Password reset flow with email tokens
- Kafka event publishing for user lifecycle events
- gRPC communication with user-service for user data

---

## Solution

**Title:** Comprehensive Auth Service with DDD architecture.

**Approach**

| Area | Implementation |
|------|----------------|
| **Orchestration** | `UseCasesOrquestrator` coordinates all authentication use cases (DDD) |
| **Tokens** | `TokenFactory` creates ACCESS (short-lived) and REFRESH (long-lived) JWTs with JJWT |
| **Events** | `UserEventProducer` publishes to Kafka: `user.created`, `user.updated`, `user.deleted`, `auth.password-changed`, `auth.account-activated`, `auth.two-factor-enabled/disabled`, and related topics |
| **Sessions** | `RedisSessionRepository` manages refresh token sessions with blacklisting |
| **Users** | `UserServiceGrpcClient` communicates with user-service for user CRUD via Protobuf |

---

## Key metrics

| Metric | Value |
|--------|--------|
| JWT access token TTL | Configurable (default ~15 min) |
| JWT refresh token TTL | Configurable (default ~7 days) |
| Session storage | Redis-backed with blacklist support |
| Kafka integration | 7+ event topics |
| REST API | 12+ endpoints across 4 controllers (15 documented in API schema) |
| OAuth2 | Custom `OAuth2UserService` |

### Operational metrics (from source)

| Label | Value | Notes |
|-------|--------|--------|
| Integration tests | `AuthEndpointsIntegrationTest` | Testcontainers Redis/Kafka + gRPC stub (Docker required) |
| API endpoints | 12+ | Across login, register, password, 2FA controllers |
| Kafka topics | 7+ | User lifecycle and auth events |
| Token types | 4 | ACCESS, REFRESH, ACTIVATION, TWO_FA |

---

## Links & media

| Link | URL |
|------|-----|
| Repository | [drugstore-platform/auth-service](https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service) |
| API docs (placeholder) | `https://api.ecommerce.com/auth-service/swagger-ui` |
| Docker Hub (placeholder) | `https://hub.docker.com/r/alexistrejo11/auth-service` |
| Demo | *Not configured* |

Cover image (placeholder): `https://placeholder-drugstore.com/images/auth-service-cover.png`

---

## Summary

Production-ready auth service with JWT, 2FA, OAuth2, Kafka, and gRPC. PostgreSQL is **not** embedded locally—user records live in **user-service** (accessed via gRPC). **Integration tests** (`integration-test` + `test` profiles) run against Testcontainers Redis/Kafka and an in-process `UserService`; use Docker locally/CI.

**Further improvements:** broader unit tests, Kubernetes manifests, Circuit Breaker on gRPC, Micrometer metrics, gRPC health in production probes.
