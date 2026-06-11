# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Eclipse Temurin, Spring Boot 3.3.2)
- **Database:** PostgreSQL 15 with Flyway migrations
- **Cache:** Redis (rate limiting + cache support)
- **Config:** Spring Cloud Config integration
- **Application Port:** `8081` (internal)
- **Reverse Proxy:** Nginx 1.27 (`:443` TLS entrypoint, `:80` redirect, `least_conn` balancing)
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
Nginx 1.27 reverse proxy. Terminates TLS on `:443` with self-signed certs (dev). Redirects `:80` to HTTPS. Upstream `employee_backend` load-balances with `least_conn` to `employee-service:8081`. Run `nginx/ssl/generate-certs.sh` before first `docker compose up`.

### prometheus / loki / grafana
Observability stack bundled with compose.

## Notes
- Nginx terminates TLS and forwards to `http://employee-service:8081` inside Docker network.
- Generate local certs with `nginx/ssl/generate-certs.sh` before first compose run.
