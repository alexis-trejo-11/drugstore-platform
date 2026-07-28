# Docker local development

Each microservice ships an **app-only** Compose file at `{service}/docker-compose.yml` plus a `{service}/Dockerfile`. Shared infrastructure (Postgres, Redis, MongoDB, Kafka, Prometheus/Loki/Grafana) lives **outside** this monorepo — point `.env` at those endpoints and join the external Docker networks below.

## Prerequisites

1. Shared infra already running (homelab / cloud).
2. External Docker networks exist on the host:
   - `infra_central_network`
   - `shared_app_network`
3. Copy env: `cp .env.example .env` in the service directory and set ports, DB/Redis/Kafka URLs, `GITHUB_TOKEN` (when the Dockerfile needs GitHub Packages), etc.

## Run

From the **service root** (e.g. `auth-service/`):

```bash
cp .env.example .env
# edit .env

docker compose up -d --build
docker compose logs -f api
docker compose down
```

## Service ports

| Service | Env variable | Default port |
|---------|--------------|--------------|
| address-service | `ADDRESS_SERVICE_PORT` | 8069 |
| user-service | `USER_SERVICE_PORT` | 8080 |
| employee-service | `EMPLOYEE_SERVICE_PORT` | 8081 |
| auth-service | `AUTH_SERVICE_PORT` | 8082 |
| inventory-service | `INVENTORY_SERVICE_PORT` | 8084 |
| payment-service | `PAYMENT_SERVICE_PORT` | 8085 |
| order-service | `ORDER_SERVICE_PORT` | 8086 |
| store-service | `STORE_SERVICE_PORT` | 8087 |
| product-service | `PRODUCT_SERVICE_PORT` | 8088 |
| cart-service | `CART_SERVICE_PORT` | 8089 |
| admin-service | `ADMIN_SERVICE_PORT` | 8090 |
| notification-service | `NOTIFICATION_SERVICE_PORT` | 8093 |

`admin-service` has no Dockerfile/compose in this repo (Gradle / `bootRun` only).

## Layout (per service)

```text
{service}/
├── .env / .env.example
├── Dockerfile
├── docker-compose.yml   # app container only
└── src/
```

## Observability

Apps log to **stdout** only (no file volume, no Loki4j push). Shared **Promtail** collects container logs → Loki. Metrics: Actuator `/actuator/prometheus` scraped by shared Prometheus.


## Gradle (no Docker)

```bash
./gradlew bootRun   # reads .env from service root
./gradlew test
```
