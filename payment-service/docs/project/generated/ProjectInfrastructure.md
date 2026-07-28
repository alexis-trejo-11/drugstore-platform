# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Spring Boot)
- **Database:** PostgreSQL with Flyway (provide via env / external infra — not bundled in `docker-compose.yml`)
- **Cache:** Redis (configured in Spring — not bundled in compose by default)
- **Application Port:** `8080` inside compose (`SPRING_PROFILES_ACTIVE=docker`); host mapping `8085:8080` for plain HTTP dev access
- **Reverse Proxy:** Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
- **Observability (compose):** Prometheus, Loki, Grafana
- **Payment gateway:** Stripe (adapter status — see application code)

## Deployment Layers
### Reverse Proxy / Load Balancer Layer
- Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).

### Application Layer
- Payment Service

### Data / Cache / Messaging
- PostgreSQL, Redis, Kafka as configured in Spring profiles (compose focuses on app + edge + observability)

### Observability Layer
- Prometheus, Loki, Grafana from `docker-compose.yml`

## Docker Services
### payment-service
Runs HTTP on port `8080` in the overlay network.

### nginx

### prometheus / loki / grafana
Centralized metrics and logs for local stacks.
