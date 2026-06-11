# Project Features

## Core Features

### 1) Multi-Country Postal Code Validation
- Factory/strategy-based validators by country code.
- Supports US, MX, CA, ES, UK.
- Falls back to default validator when country is unsupported.

### 2) Role-Based Address Limits
- CUSTOMER: up to 5 addresses.
- EMPLOYEE: up to 1 address.
- Enforced in service layer before persistence.

### 3) Default Address Management
- First address can become default automatically.
- Only one default per user at a time.
- Supports user and admin default-setting endpoints.

### 4) Dual Controller Architecture
- `UserAddressController` for user-owned resources.
- `AddressAdminController` for admin-wide operations.
- Clear route-level authorization boundaries.

### 5) Redis-Backed Rate Limiting
- AOP-based enforcement using `@RateLimit`.
- Profiles for standard vs sensitive operations.
- Redis-backed distributed counters.

### 6) JWT Authentication and Authorization
- Bearer JWT validation via shared kernel filter.
- Role-aware access controls.
- Authentication principal used for user-scoped actions.

### 7) Soft Delete Pattern
- No hard delete by default.
- Records marked inactive for recoverability/auditing.

### 8) Docker Containerization
- Multi-stage build, non-root runtime, HTTPS health checks.
- Compose stack for local dependencies and observability.

### 9) OpenAPI Documentation
- Springdoc/Swagger UI integration.
- Endpoint-level request/response examples and auth scheme.

### 10) Flyway Database Migrations
- Versioned schema evolution.
- Repeatable environment setup.

### 11) CORS Configuration
- Development origins enabled.
- Credentials and method controls configured.

### 12) Observability Stack
- Actuator metrics + Prometheus registry.
- Loki log ingestion from Logback.
- Grafana dashboards with metrics/log correlation.
- Trace sampling configured at 100%.

### 13) GitHub Packages (Shared External Library)
- Consumes `io.github.alexisTrejo11:shared-kernel:2.0.0` from GitHub Packages Maven registry (`drugstore-platform`).
- Centralizes cross-cutting platform code: `ResponseWrapper`, domain exceptions, JWT DTOs, audit logging, pagination helpers (`libs_kernel.*`).
- Gradle resolves credentials from `.env`, environment variables, or `-P` properties before dependency download (`envOrDotEnv` in `build.gradle`).
- Docker image builds pass `GITHUB_ACTOR` / `GITHUB_TOKEN` as Compose build args (`.env` is not baked into the image).
- Source library is published from `libs/shared-kernel` via `maven-publish`; consumers pin an explicit artifact version in `build.gradle`.

See [GitHubPackages.md](GitHubPackages.md) for setup, publish workflow, and troubleshooting.
