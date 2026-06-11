# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Eclipse Temurin, Spring Boot 3.x)
- **Database:** PostgreSQL (configured via Spring; not defined in `docker-compose.yml` — provide externally or extend compose)
- **Cache:** Redis (configured via Spring; not defined in `docker-compose.yml` — provide externally or extend compose)
- **Application Port:** `8080` HTTP inside the compose network (`SPRING_PROFILES_ACTIVE=docker`). Published as host `8086:8080` for direct dev access.
- **Reverse Proxy:** Nginx 1.27 — TLS on host `:443`, HTTP→HTTPS redirect on `:80`, `least_conn` upstream `order_backend` → `order-service:8080`
- **Observability (compose):** Prometheus, Loki, Grafana
- **Health Endpoint:** `/actuator/health`

## Deployment Layers
### Client Layer
- Frontend and API consumers

### Reverse Proxy / Load Balancer Layer
- Nginx 1.27 (TLS termination, horizontal scale via `docker compose up --scale order-service=N`)

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
Nginx 1.27 reverse proxy (`order-nginx`). Host `:443` terminates TLS (self-signed in dev); `:80` redirects to HTTPS. Proxies to upstream `order_backend` with `least_conn`. Run `nginx/ssl/generate-certs.sh` before first `docker compose up`.

### prometheus / loki / grafana
Metrics, log aggregation, and dashboards wired under `observability/`.
