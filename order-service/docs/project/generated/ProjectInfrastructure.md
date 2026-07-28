# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Eclipse Temurin, Spring Boot 3.x)
- **Database:** PostgreSQL (configured via Spring; not defined in `docker-compose.yml` — provide externally or extend compose)
- **Cache:** Redis (configured via Spring; not defined in `docker-compose.yml` — provide externally or extend compose)
- **Application Port:** `8080` HTTP inside the compose network (`SPRING_PROFILES_ACTIVE=docker`). Published as host `8086:8080` for direct dev access.
- **Reverse Proxy:** Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
- **Observability (compose):** Prometheus, Loki, Grafana
- **Health Endpoint:** `/actuator/health`

## Deployment Layers
### Client Layer
- Frontend and API consumers

### Reverse Proxy / Load Balancer Layer
- Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).

### Application Layer
- Order Service (Spring Boot)

### Data Layer
- PostgreSQL (external or extended compose)

### Cache Layer
- Redis (external or extended compose)

### Observability Layer
- Prometheus, Loki, Grafana (from `docker-compose.yml`)

## Docker Services
### order-service
Container image `order-service:latest`. HTTP only on the internal network; HTTPS for clients is via Nginx.

### nginx

### prometheus / loki / grafana
Metrics/logs via Actuator + shared observability stack outside this monorepo.
