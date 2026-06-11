# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Spring Boot)
- **Database:** PostgreSQL with Flyway (provide via env / external infra — not bundled in `docker-compose.yml`)
- **Cache:** Redis (configured in Spring — not bundled in compose by default)
- **Application Port:** `8080` inside compose (`SPRING_PROFILES_ACTIVE=docker`); host mapping `8085:8080` for plain HTTP dev access
- **Reverse Proxy:** Nginx 1.27 — TLS on `:443`, redirect `:80` → HTTPS, upstream `payment_backend` with `least_conn` → `payment-service:8080`
- **Observability (compose):** Prometheus, Loki, Grafana
- **Payment gateway:** Stripe (adapter status — see application code)

## Deployment Layers
### Reverse Proxy / Load Balancer Layer
- Nginx 1.27 at the Docker edge

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
Nginx 1.27 (`payment-nginx`): terminates TLS on `:443`, redirects `:80`, balances to `payment_backend`. Generate dev certs with `nginx/ssl/generate-certs.sh` before first `docker compose up`.

### prometheus / loki / grafana
Centralized metrics and logs for local stacks.
