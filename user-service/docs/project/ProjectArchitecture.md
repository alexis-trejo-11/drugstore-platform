# Architecture

## Layered view (conceptual hexagon)

### Delivery — REST adapters

| Piece | Responsibility | Tech |
|-------|-----------------|------|
| `UserQueryManagerController` | Read models by id/email/phone/role/status | Spring MVC • Swagger annotations |
| `UserManagerController` | Create / activate / ban / unban / delete | CommandBus |
| `ProfileController` | Authenticated profile read & patch | `RateLimit` • `@RequestAttribute userId` |
| `SecurityConfig` | JWT filter chain • role matchers | Spring Security |

**Critical defect:** **`springdoc.packages-to-scan`** in YAML still references **`io.github.alexisTrejo11.drugstore.stores`**, so **generated OpenAPI is unreliable** for documenting this service until fixed.

### Application layer

- **CommandBus** dispatches **`CreateUserCommand`**, **`UpdateUserStatusCommand`**, **`DeleteUserCommand`**.  
- **QueryBus** dispatches lookups and paginated projections.  
- **ProfileUseCases** orchestrates profile aggregate updates.

### Domain layer

- Entities / VOs (**Email**, **PhoneNumber**, **UserId**, **FullName**) and enums (**UserRole**, **UserStatus**).  
- **Domain events** (`UserCreatedEvent`, `UserUpdateEvent`, `UserDeletedEvent`) for Kafka integration semantics.

### Infrastructure layer

| Concern | Implementation | Gap / danger |
|---------|----------------|--------------|
| Persistence | JPA adapters + PostgreSQL | Dev profile disables Flyway and uses **`ddl-auto: update`** → schema drift risk. |
| Messaging | `UserEventConsumer` + handlers | **No DLQ**; deserialization errors **throw**. |
| gRPC | `UserGrpcServer` extends generated base | **`GrpcServerConfig` empty** • **no `ServerBuilder.start()`** anywhere → **not reachable**. |
| Cache | Redis + Spring cache abstraction | TTL/coverage tune per workload. |

## Request flow

1. **Client → Nginx** (`:443`, dev self-signed certs).  
2. **Nginx → user-service** internal HTTP `:8080`.  
3. **JwtAuthenticationFilter** establishes security context; **authorizeHttpRequests** enforces matchers.  

## Event flow

1. **Kafka broker** emits JSON payloads for **`user.created`**, **`user.updated`**, **`user.deleted`** (topics configurable via `kafka.topics.user.*`).  
2. Listener **parses** with Jackson → **delegates handler** → **`acknowledgment()`** on success only.  

**Risk:** Poison message or handler bug → repeated delivery / stuck consumer until operator intervenes (**DLQ not implemented**, TODOs remain in sources).

## gRPC positioning

`user_service.proto` exposes validations and CRUD-style RPC suitable for **`auth-service` or onboarding workers**. Until a **Netty/Spring-grpc lifecycle** binds `UserGrpcServer`, treat protobuf as **contract-only**.

## Threat / security notes

- **Actuator** is broadly open in **`application.yml`** for dev — tighten with profile-specific exposure.  
- **gRPC**, if ever enabled with defaults, has **`GRPC_SECURITY_ENABLED:false`** in config commentary — assume **plaintext on the wire** unless you layer mTLS/ALTS.

## Architectural decisions (ADR-style, short)

| Decision | Reason | Risk |
|----------|--------|------|
| **Shared-kernel JWT** | Consistency with other backend services | **Issuer/secret contract** must match token producer. |
| **Command/Query buses** | Testability vs anemic mega-service class | Boilerplate registrations must stay disciplined. |
| **Kafka JSON + wildcard trusted packages** | Fast PoC ergonomics | **Security / compatibility** regressions vs Avro/protobuf on the wire |
