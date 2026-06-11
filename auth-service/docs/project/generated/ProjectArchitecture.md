# Project Architecture

Plain-Markdown twin of `docs/project/source/ProjectArchitecture.md`.

---

## Layered architecture (DDD ports & adapters)

### Controllers (Inbound Adapters)
- Handles REST requests with rate limiting and input validation.
- DTO mapping via Command pattern (Request → Command).
- Main components: `LoginController`, `RegisterController`, `PasswordAuthController`, `LogoutController`, `TwoFactorConfigController`.

### Application Layer (Use Cases)
- DDD Application Services orchestrating domain logic.
- Main component: `UseCasesOrquestrator` (implements 4 port interfaces).
- Use cases: Login, Register, Password, Logout, TwoFaConfig, Token, ActivateAccount.

### Domain Layer
- Core business logic: `User` entity, Value Objects (Email, Password, UserId, Token, etc.).
- Domain Events: UserRegisteredEvent, UserLoginEvent, PasswordChangedEvent, etc.
- JWTSessions model.

### Output Adapters (Infrastructure)
- `TokenManager` + `TokenFactory` — JWT and numeric token management.
- `RedisSessionRepository` / `RedisTokenRepository` — persistence.
- `UserEventProducer` / `NotificationEventProducer` — Kafka publishing.
- `UserServiceGrpcClient` — gRPC communication.
- `OAuth2AuthenticationSuccessHandler` — social login.

### Configuration Layer
- Security, rate limiting, Kafka, gRPC, CORS, global exception handling.

## Design Patterns
- Orchestrator Pattern (UseCasesOrquestrator)
- Command Pattern (LoginCommand, SignupCommand, etc.)
- Value Object Pattern (Email, Password, UserId, etc.)
- Factory Pattern (TokenFactory for 4 token types)
- Domain Events (8+ Kafka event types)
- Adapter Pattern (Ports & Adapters architecture)

## Scalability Strategies
- Nginx `least_conn` load balancing — scale with `docker compose up --scale auth-service=N`
- Stateless JWT access tokens for horizontal scaling
- Redis for distributed session storage across instances
- Kafka for asynchronous event publishing
- gRPC with Protobuf for high-performance user-service communication

## Security Strategies
- Nginx TLS termination at the edge; internal Docker traffic uses HTTPS with `proxy_ssl_verify off`
- JWT Access + Refresh Tokens (15min access / 7 day refresh)
- Two-Factor Authentication (TOTP — 6-digit codes, 5min expiration)
- OAuth2 Social Login (Google, GitHub, etc.)
- BCrypt password encoding
- Redis rate limiting (SENSITIVE profile: 10 req/min)
- Token blacklisting (Redis session revocation)

## Cache Strategies
- Redis session cache — refresh token sessions with 7-day TTL.
- Redis rate limit counters with 1-minute TTL for token bucket algorithm.

## Architecture Highlights
- DDD Bounded Context — clean domain model with ports/adapters
- Multi-role support (CUSTOMER, EMPLOYEE, ADMIN)
- Four token types via TokenFactory
- Event-driven inter-service communication (8+ Kafka topics)

## Request flow

1. Client → **Nginx** `:443` (TLS terminated, `least_conn` to a replica)
2. Nginx → **auth-service** `:8443` (internal Docker network)
3. **Rate limit** check (Redis, SENSITIVE: 10/min)
4. **UseCasesOrquestrator** → use case with Command object
5. **gRPC** user lookup via user-service (when needed)
6. **TokenFactory** / **TokenManager** generate ACCESS + REFRESH
7. **RedisSessionRepository** stores refresh session
8. **UserEventProducer** publishes domain event (e.g. `UserLoginEvent`)
9. **ResponseWrapper** with tokens → client via Nginx

## Event flow (Kafka)

| Step | Event | Consumer impact |
|------|--------|-----------------|
| 1 | `UserRegisteredEvent` | Downstream services create user profile |
| 2 | `UserCreatedEvent` | e.g. notification-service welcome email |
| 3 | `PasswordChangedEvent` | Audit / security logging |
| 4 | `TwoFactorEnabledEvent` / `TwoFactorDisabledEvent` | Security policy hooks |

## Architecture diagram (logical)

```text
[Client] --HTTPS:443--> [Nginx] --HTTPS--> [Auth Service]
                              |                |
                              |                +--RESP--> [Redis 7]
                              |                +--Kafka--> [Kafka]
                              |                +--gRPC--> [User Service]
                              |                +--HTTP--> [Actuator]
```

## Application Bootstrap
- `AuthServiceApplication` uses `@ComponentScan` for `io.github.alexisTrejo11.drugstore.accounts` plus `libs_kernel.security.jwt` and `libs_kernel.config.rate_limit` so all controllers, use cases, and adapters load correctly.

## Integration Testing (Automated)
- `AuthEndpointsIntegrationTest` (`src/test/java/.../integration/`) runs `@SpringBootTest` with **real Redis and Kafka** via **Testcontainers**, and a **Netty in-process gRPC server** (`InMemoryUserGrpcServer`) that implements `UserService` with BCrypt-backed credential checks—no mocked login path for the main flows.
- Spring profiles **`integration-test`** + **`test`**: `application-integration-test.yml` supplies JWT and relaxed rate limits; **`test`** keeps Logback from attaching the Loki appender (`logback-spring.xml` uses `!test`).
- `@Testcontainers(disabledWithoutDocker = true)` skips the suite when Docker is unavailable (local runs need Docker to execute these tests).

## Key Technical Decisions
- DDD Ports & Adapters for testable, swappable infrastructure.
- JWT + Redis sessions for scalable reads with revocable sessions.
- gRPC for low-latency, type-safe user-service communication.
- Kafka for loosely coupled async event publishing.
- TokenFactory for consistent multi-type token management.
- Nginx for TLS termination and zero-config horizontal scaling.
