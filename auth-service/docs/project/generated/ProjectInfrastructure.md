# Infrastructure

Plain-Markdown twin of `docs/project/source/ProjectInfrastructure.md`.

---

## Infrastructure metrics

| Label | Value | Description |
|-------|--------|-------------|
| Java | 23 | Eclipse Temurin JDK, Spring Boot 3.3.2 |
| Redis | 7-alpine | Sessions, rate limiting, opaque tokens |
| Kafka | *External / Testcontainers* | Auth domain events (not always in Compose) |
| gRPC | 1.60.0 | Protobuf RPC to user-service |
| Container image | `eclipse-temurin:23-jre-alpine` | Multi-stage build + shared-kernel |
| HTTPS (internal) | 8443 | `keystore.p12`; not exposed to host |
| Reverse proxy | Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
| Health | `/actuator/health` | 30s interval in Docker HEALTHCHECK |
| Integration tests | Testcontainers | Redis + Kafka containers; in-process gRPC UserService |

---

## Deployment layers

### Client layer

- Frontend (React/Angular/Vue) consuming auth API over HTTPS
- Mobile apps (future)

### Reverse proxy / load balancer

- **Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).

### Application layer

- Auth Service (Spring Boot 3.3.2, DDD)
- JWT Token Manager (`TokenFactory`, `TokenManager`)
- OAuth2 (`CustomOAuth2UserService`)

### Communication layer

- gRPC → user-service (Protobuf 3.25.1)
- Kafka producer (8+ event types)

### Storage layer

- **Redis 7:** Refresh sessions, rate limits, ACTIVATION/TWO_FA tokens
- **PostgreSQL:** Owned by user-service (no direct DB in auth-service)

---

## Docker Compose services (typical)

| Service | Role |
|---------|------|
| **auth-service** | Spring Boot app; port 8443 internal only |
| **nginx** | Edge TLS + load balancing |
| **redis** | Session + rate limit backing store |
| **prometheus** | Scrapes `/actuator/prometheus` |
| **loki** | Centralized logs (Logback appender) |
| **grafana** | Dashboards (provisioned datasources) |

**Not in default Compose:** Kafka broker, real user-service JVM — add for full manual E2E beyond integration tests.

---

## Cloud placeholders (production)

| Service | Purpose |
|---------|---------|
| AWS ElastiCache | Managed Redis |
| AWS MSK | Managed Kafka |
| AWS ECS/EKS | Container orchestration |
| AWS Certificate Manager | TLS certs (replace dev self-signed Nginx certs) |
| AWS CloudWatch | Logs and metrics |

---

## Docker build highlights (auth-service)

Multi-stage build pattern:

1. **Builder:** JDK 23 Alpine — compile shared-kernel (or resolve from GitHub Packages), Gradle `bootJar`
2. **Runtime:** JRE 23 Alpine — non-root `spring` user, `keystore.p12`, entrypoint with `SPRING_PROFILES_ACTIVE=docker`
3. **Healthcheck:** `wget` to `https://localhost:8443/actuator/health` (no cert verify in container)

**Redis service (compose excerpt):**

```yaml
image: redis:7-alpine
command: redis-server --appendonly yes
ports:
  - "6378:6379"
healthcheck:
  test: ["CMD", "redis-cli", "ping"]
```

**Nginx:** Mounts `nginx/nginx.conf` and SSL certs; `depends_on` auth-service healthy; host ports `80`/`443` only.

---

## Automated integration tests

| Item | Detail |
|------|--------|
| Tooling | JUnit 5, Spring Boot Test, Testcontainers |
| Images | `redis:7-alpine`, Confluent Kafka 7.6.x |
| Profiles | `integration-test` + `test` |
| gRPC | In-process `InMemoryUserGrpcServer` (BCrypt-aligned with tests) |
| Docker | Required; suite skipped if Docker unavailable |

---

## Operational notes

- All **external** traffic enters through **Nginx :443**; auth-service **8443** stays on the Docker network.
- For production: managed Kafka, ACM (or equivalent) for TLS, gRPC health checks, Kubernetes manifests still TBD.
- User persistence is **only** via user-service over gRPC — no local PostgreSQL in this service.
