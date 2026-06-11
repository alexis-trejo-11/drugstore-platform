# Project features

Plain-Markdown twin of `docs/project/source/ProjectFeature.md`.

---

## 1. JWT access + refresh token management

- **Status:** stable
- **What:** Short-lived access tokens (~15 min) and long-lived refresh tokens (~7 days). HMAC-SHA JWTs via JJWT 0.11.5; `TokenFactory` also creates numeric ACTIVATION and TWO_FA tokens.
- **Highlights:** Claims include `role`, `userId`, `email`, `name`, `phoneNumber`; refresh used to obtain new access tokens.
- **Code:** `adapter/output/security/tokens/factory/TokenFactory.java`

---

## 2. Two-factor authentication (TOTP)

- **Status:** stable
- **What:** Enable/disable 2FA, send validation codes, complete 2FA login flow.
- **Highlights:** 6-digit numeric codes, ~5 min expiration; `TwoFactorConfigController`; events `TwoFactorEnabledEvent` / `TwoFactorDisabledEvent` → Kafka.
- **Endpoints:** `POST /api/v1/auth/2fa/{userId}/enable|disable|send-code`, `POST /api/v2/auth/login/2fa`

---

## 3. OAuth2 social login

- **Status:** stable
- **What:** External providers (Google, GitHub, etc.) via Spring OAuth2 Client.
- **Highlights:** `CustomOAuth2UserService`, `OAuth2AuthenticationSuccessHandler`, `ExternalIDs` value object, `OAuth2Provider` enum.
- **Gap:** Provider count and callback URLs should be documented in OpenAPI.

---

## 4. Password management & reset flow

- **Status:** stable
- **What:** Forgot password, validate reset token, reset password, change password (authenticated).
- **Highlights:** BCrypt hashing; ACTIVATION-style tokens for email; `PasswordChangedEvent` → Kafka; user updates via gRPC to user-service.
- **Endpoints:** `/api/v2/auth/password/*`

---

## 5. Kafka event publishing

- **Status:** stable
- **What:** 8+ domain events with CompletableFuture publishing (~10s timeout).
- **Topics (examples):** `user-registered`, `user.created`, `user.updated`, `user.deleted`, `user-login`, `auth.password-changed`, `auth.account-activated`, `auth.two-factor-*`
- **Code:** `adapter/output/messaging/kafka/producer/UserEventProducer.java`

---

## 6. gRPC client for user-service

- **Status:** stable
- **What:** Protobuf/gRPC 1.60.0 for user CRUD; blocking stub; `UserGrpcMapper`.
- **Operations:** get by email/id, create, update, delete.
- **Gap:** Add Resilience4j Circuit Breaker for production fault tolerance.

---

## 7. DDD ports & adapters

- **Status:** stable
- **What:** Ports in `core/ports`, adapters in `adapter/output`, `UseCasesOrquestrator` implements Auth, Logout, Password, Register, TwoFaConfig use-case interfaces.
- **Patterns:** Command objects (`LoginCommand`, `SignupCommand`, …), result objects (`SessionPayload`, `SignUpResult`).

---

## 8. Redis session management

- **Status:** stable
- **What:** Refresh token sessions, blacklisting, rate-limit counters, non-JWT token storage (ACTIVATION, TWO_FA).
- **TTL:** Session TTL aligned with refresh token expiration (~7 days configurable).

---

## 9. Value objects pattern

- **Status:** stable
- **What:** `Email`, `Password`, `UserId`, `UserRole`, `Token`, `SessionId`, `PhoneNumber`, `OAuth2Provider`, `ExternalIDs` with validation at construction.

---

## 10. Multi-role user registration

- **Status:** stable
- **Endpoints:** `POST /api/v2/auth/register/customer|employee|admin`
- **Roles:** CUSTOMER, EMPLOYEE, ADMIN — activation required after register.

---

## 11. HTTP integration tests (Testcontainers)

- **Status:** stable
- **What:** `AuthEndpointsIntegrationTest` — real Redis + Kafka containers, in-process `InMemoryUserGrpcServer`, profiles `integration-test` + `test`.
- **Flows:** register → activate → login, refresh/logout, password reset, 2FA, authenticated routes.
- **Docker:** Required; `@Testcontainers(disabledWithoutDocker = true)` skips when unavailable.

---

## 12. GitHub Packages (shared-kernel)

- **Status:** stable
- **What:** Consumes `io.github.alexisTrejo11:shared-kernel:2.0.0` from GitHub Packages Maven (not vendored sources).
- **Shared APIs:** `ResponseWrapper`, JWT DTOs, rate-limit config, audit types (`libs_kernel.*`).
- **Build:** `GITHUB_ACTOR` / `GITHUB_TOKEN` in Gradle and Docker Compose build args.

---

## Summary table

| Feature | Status | Risk / note |
|---------|--------|-------------|
| JWT + refresh | Implemented | Consider refresh token rotation + reuse detection |
| 2FA | Implemented | v1 path prefix for 2FA config |
| OAuth2 | Implemented | Document provider setup |
| Kafka events | Implemented | Compose may omit Kafka for manual runs |
| gRPC user-service | Implemented | No circuit breaker yet |
| Integration tests | Implemented | Docker required |
| Unit tests | Partial | Expand beyond integration suite |
| K8s / CI | Missing | Manifests and pipeline TBD |

**Potential improvements:** Micrometer metrics, CI/CD (GitHub Actions), rate limit on all endpoints, Java 23 compatibility watchlist.
