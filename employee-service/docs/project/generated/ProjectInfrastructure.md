# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Eclipse Temurin, Spring Boot 3.3.2)
- **Database:** PostgreSQL 15 with Flyway migrations
- **Cache:** Redis (rate limiting + cache support)
- **Config:** Spring Cloud Config integration
- **Application Port:** `8081` (internal)
- **Reverse Proxy:** Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
- **Health Endpoint:** `/actuator/health`

## Deployment Layers
- Client Layer
- Reverse Proxy / Load Balancer Layer (Nginx)
- Application Layer (employee-service)
- Data Layer (PostgreSQL + Flyway)
- Cache Layer (Redis)

## Docker Services
### employee-service
Spring Boot app listening on `8081` inside the Docker network.

### nginx

### prometheus / loki / grafana
Observability stack bundled with compose.

## Notes
- Nginx terminates TLS and forwards to `http://employee-service:8081` inside Docker network.
