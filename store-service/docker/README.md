# Store Service — Docker

All containerization for **store-service** lives under this directory: the image definition, reverse proxy, observability configs, and Compose files.

Environment variables live in a **single `.env` file at the project root** (`store-service/.env`). Copy from `.env.example` and run Compose from the root.

## Quick reference

| Goal | Compose file | Profile (`COMPOSE_PROFILES` in `.env`) |
|------|--------------|----------------------------------------|
| Full local stack (app + DB + Redis + monitoring) | `docker-compose.full.yml` | `local` |
| App containers + cloud DB/Redis/Kafka | `docker-compose.full.yml` | `prod` |
| App + Nginx only, DB/Redis/Kafka on host | `docker-compose.app.yml` | `local` |
| App + Nginx only, cloud infrastructure | `docker-compose.app.yml` | `prod` |

```bash
cd store-service
cp .env.example .env
# Edit .env — set JWT_SECRET_KEY and connection URLs for your profile
chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh   # local/dev only

# Pick one (always pass --env-file .env from project root):
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
docker compose -f docker/docker-compose.app.yml  --env-file .env up -d --build
```

## Layout

```text
store-service/
├── .env.example                    # All required + optional variables (copy to .env)
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.full.yml     # App + Nginx + DB + Redis + monitoring
│   ├── docker-compose.app.yml      # App + Nginx only (no hardcoded app env vars)
│   ├── nginx/
│   └── observability/
```

## Compose files

| File | What it runs | When to use |
|------|----------------|-------------|
| `docker-compose.full.yml` | Nginx, store-service, PostgreSQL, Redis, Prometheus, Loki, Grafana | Local development with everything in Docker |
| `docker-compose.app.yml` | Nginx + store-service only | Deploy the app against external DB/Redis/Kafka (host or cloud) |

`docker-compose.app.yml` loads all application settings from the root `.env` via `env_file` — no hardcoded environment variables on the app service.

`docker-compose.full.yml` only asserts **inter-container communication** defaults on `store-service` (e.g. `DATASOURCE_URL` pointing at `postgres`, `REDIS_URL` pointing at `redis`) so the bundled local stack works out of the box. Override those in `.env` for production.

## Profiles

Set `COMPOSE_PROFILES=local` or `COMPOSE_PROFILES=prod` in your root `.env`.

| Profile | Infrastructure | Typical use |
|---------|----------------|---------------|
| **`local`** | Bundled containers (full stack) or host services via `host.docker.internal` (app only) | Development and integration testing |
| **`prod`** | External managed services (RDS, ElastiCache, MSK, etc.) | Staging/production-like runs |

### Full stack (`docker-compose.full.yml`)

- **`local`** — starts PostgreSQL, Redis, Prometheus, Loki, and Grafana alongside the app. Connects to the shared Kafka cluster on `drugstore-kafka-network`.
- **`prod`** — starts only Nginx and store-service; set `DATASOURCE_URL`, `REDIS_URL`, and `SPRING_KAFKA_BOOTSTRAP_SERVERS` to your cloud endpoints in `.env`.

### App only (`docker-compose.app.yml`)

- **`local`** — set `DATASOURCE_URL`, `REDIS_URL`, and `SPRING_KAFKA_BOOTSTRAP_SERVERS` to `host.docker.internal` endpoints in `.env` when Postgres/Redis/Kafka run on the host.
- **`prod`** — set cloud RDS/ElastiCache/MSK endpoints in `.env`.

## First-time setup

From **`store-service/`** (project root):

```bash
cp .env.example .env
# Edit .env — JWT_SECRET_KEY is required

chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh

# (Full local stack only) Start the Kafka cluster — creates drugstore-kafka-network
cd ../infrastructure/kafka && docker compose up -d && cd -
```

## How to run

All commands assume your current directory is **`store-service/`**.

### Full stack — local (recommended for development)

Starts the complete stack: app, Nginx, PostgreSQL, Redis, and monitoring. Requires the Kafka cluster from `infrastructure/kafka`.

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

Scale app replicas behind Nginx:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --scale store-service=3
```

### Full stack — prod (app containers + cloud infrastructure)

Set `COMPOSE_PROFILES=prod` and cloud connection URLs in `.env`, then:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

> If Compose fails because `drugstore-kafka-network` does not exist, create it once with `docker network create drugstore-kafka-network` (the network is unused when Kafka endpoints are cloud-hosted), or use `docker-compose.app.yml` with `COMPOSE_PROFILES=prod` instead.

### App only — local (external DB/Redis/Kafka on the host)

In `.env`:

```bash
COMPOSE_PROFILES=local
DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/store_db
REDIS_URL=redis://host.docker.internal:6379
SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9093
```

```bash
docker compose -f docker/docker-compose.app.yml --env-file .env up -d --build
```

### App only — prod (cloud RDS / ElastiCache / MSK / etc.)

Set `COMPOSE_PROFILES=prod` and cloud endpoints in `.env`, then:

```bash
docker compose -f docker/docker-compose.app.yml --env-file .env up -d --build
```

## Endpoints

| Service | URL (local full stack) |
|---------|-------------------------|
| API (HTTPS via Nginx) | `https://localhost/api/**` |
| Actuator health | `https://localhost/actuator/health` |
| Prometheus metrics | `https://localhost/actuator/prometheus` |
| Swagger UI (direct, local dev) | `http://localhost:8087/swagger-ui.html` |
| Prometheus UI | `http://localhost:9090` |
| Loki | `http://localhost:3100/ready` |
| Grafana | `http://localhost:3000` (admin / admin by default) |

External API traffic goes through Nginx on ports **80** and **443**. Port **8087** exposes the app directly on the host for dev tools (Swagger, etc.) without going through Nginx.

## Environment variables

See **`.env.example`** at the project root for the full list of required and optional variables.

| Variable | Local (full) | Prod |
|----------|--------------|------|
| `DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/store_db` | Cloud Postgres JDBC URL |
| `REDIS_URL` | `redis://redis:6379` | Cloud Redis URL |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `kafka-1:9092,...` (Docker network) | Cloud Kafka bootstrap servers |
| `SWAGGER_ENABLED` | `true` | `false` |
| `JWT_SECRET_KEY` | Required | Required |

## Common operations

```bash
# View logs
docker compose -f docker/docker-compose.full.yml --env-file .env logs -f store-service

# Stop everything
docker compose -f docker/docker-compose.full.yml --env-file .env down

# Stop and remove volumes (destructive — wipes local DB data)
docker compose -f docker/docker-compose.full.yml --env-file .env down -v

# Rebuild after code changes
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build store-service
```

## Production notes

- Replace self-signed Nginx certs in `nginx/ssl/` with CA-signed or Let's Encrypt certificates.
- Do not commit `.env` with real secrets (ignored via monorepo `**/.env` rule).
- For Kubernetes/ECS, use `docker/Dockerfile` directly and inject env vars from your orchestrator's secret store.
- Managed monitoring (Grafana Cloud, Datadog, etc.) replaces the bundled Prometheus/Loki/Grafana stack in production.

## Troubleshooting

**Nginx fails to start (missing certs)** — Run `./docker/nginx/ssl/generate-certs.sh`. If Docker created empty `nginx.key`/`nginx.crt` directories, remove them and regenerate.

**App cannot reach Postgres in app-only local mode** — Ensure Postgres listens on the host and set `DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/store_db` in `.env`.

**App cannot reach Kafka in full local mode** — Start the Kafka stack first: `cd ../infrastructure/kafka && docker compose up -d`. Verify `drugstore-kafka-network` exists: `docker network ls | grep drugstore-kafka`.

**Profile prod still tries to connect to `postgres`** — Set `COMPOSE_PROFILES=prod` and cloud `DATASOURCE_URL` / `REDIS_URL` in `.env`.

**JWT errors on startup** — Set `JWT_SECRET_KEY` in `.env` with at least 32 characters.
