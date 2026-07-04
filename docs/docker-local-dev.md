# Docker local development

Each microservice has a single Compose file at `{service}/docker/docker-compose.yml` with optional **profiles**. Environment variables live in `{service}/.env` (copy from `.env.example`).

## Profiles

| Profile | Starts |
|---------|--------|
| *(none)* | Spring Boot app only |
| `nginx` | TLS reverse proxy (`:80` / `:443`) |
| `infra` | Bundled data stores (Postgres, Redis, or MongoDB — per service) |
| `observability` | Prometheus, Loki, Grafana |

Combine profiles as needed. See the header comment in each service's `docker/docker-compose.yml` for copy-paste commands.

## Common workflows

Run from the **service root** (e.g. `address-service/`):

```bash
cp .env.example .env
# edit .env — JWT, GITHUB_TOKEN, DB/Redis endpoints, SERVICE_PORT, etc.

# App only — cloud RDS / Upstash / host DB (your .env endpoints)
docker compose -f docker/docker-compose.yml --env-file .env up -d --build

# App + bundled local Postgres & Redis (or MongoDB for notification-service)
docker compose -f docker/docker-compose.yml --env-file .env --profile infra up -d --build

# Full local stack (app + infra + monitoring)
docker compose -f docker/docker-compose.yml --env-file .env --profile infra --profile observability up -d --build

# App behind Nginx (generate certs first: ./docker/nginx/ssl/generate-certs.sh)
docker compose -f docker/docker-compose.yml --env-file .env --profile nginx up -d --build

# Everything
docker compose -f docker/docker-compose.yml --env-file .env \
  --profile nginx --profile infra --profile observability up -d --build
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

## Layout (per service)

```text
{service}/
├── .env / .env.example
└── docker/
    ├── docker-compose.yml   # single file, profiles: nginx | infra | observability
    ├── Dockerfile
    ├── nginx/
    └── observability/
```

## Kafka

Services that use Kafka join the external `drugstore-kafka-network`. Start Kafka first:

```bash
cd infrastructure/kafka
docker compose up -d
```

If Kafka is cloud-hosted, set `KAFKA_BOOTSTRAP_SERVERS` in `.env` and run without the Kafka network dependency.

## Useful commands

```bash
docker compose -f docker/docker-compose.yml --env-file .env logs -f <service-name>
docker compose -f docker/docker-compose.yml --env-file .env down
docker compose -f docker/docker-compose.yml --env-file .env down -v   # remove volumes
```

## Gradle (no Docker)

```bash
./gradlew bootRun   # reads .env from service root
```
