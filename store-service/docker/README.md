# Store Service — Docker

All containerization for **store-service** lives under this directory: the image definition, reverse proxy, observability configs, and Compose files.

## Quick reference

| Goal | Compose file | Profile | Env files |
|------|--------------|---------|-----------|
| Full local stack (app + DB + Redis + monitoring) | `docker-compose.full.yml` | `local` | `.env` + `.env.local` |
| App containers + cloud DB/Redis/Kafka/monitoring | `docker-compose.full.yml` | `prod` | `.env` + `.env.prod` |
| App + Nginx only, DB/Redis/Kafka on host | `docker-compose.app.yml` | `local` | `.env` + `.env.local` |
| App + Nginx only, cloud infrastructure | `docker-compose.app.yml` | `prod` | `.env` + `.env.prod` |

```bash
cd store-service/docker
cp .env.example .env && cp .env.local.example .env.local   # or .env.prod.example
# Edit .env — set JWT_SECRET_KEY (≥32 characters)
./nginx/ssl/generate-certs.sh   # local/dev only

# Pick one:
docker compose -f docker-compose.full.yml --profile local --env-file .env --env-file .env.local up -d --build
docker compose -f docker-compose.full.yml --profile prod  --env-file .env --env-file .env.prod  up -d --build
docker compose -f docker-compose.app.yml  --profile local --env-file .env --env-file .env.local up -d --build
docker compose -f docker-compose.app.yml  --profile prod  --env-file .env --env-file .env.prod  up -d --build
```

## Layout

```text
docker/
├── Dockerfile                      # Multi-stage Spring Boot image
├── docker-compose.full.yml         # App + Nginx + DB + Redis + monitoring
├── docker-compose.app.yml          # App + Nginx only
├── .env.example                    # Shared secrets (JWT)
├── .env.local.example              # Local profile overrides
├── .env.prod.example               # Production profile (cloud endpoints)
├── nginx/
│   ├── nginx.conf                  # TLS termination + load balancing
│   └── ssl/
│       └── generate-certs.sh       # Dev self-signed certificate
└── observability/
    ├── prometheus/
    ├── loki/
    └── grafana/
```

## Compose files

| File | What it runs | When to use |
|------|----------------|-------------|
| `docker-compose.full.yml` | Nginx, store-service, PostgreSQL, Redis, Prometheus, Loki, Grafana | Local development with everything in Docker |
| `docker-compose.app.yml` | Nginx + store-service only | Deploy the app against external DB/Redis/Kafka (host or cloud) |

## Profiles

Both compose files support two **profiles** that control where infrastructure comes from:

| Profile | Infrastructure | Typical use |
|---------|----------------|-------------|
| **`local`** | Bundled containers (full stack) or host services via `host.docker.internal` (app only) | Development and integration testing |
| **`prod`** | External managed services (RDS, ElastiCache, MSK, cloud monitoring) | Staging/production-like runs |

### Full stack (`docker-compose.full.yml`)

- **`--profile local`** — starts PostgreSQL, Redis, Prometheus, Loki, and Grafana alongside the app. Connects to the shared Kafka cluster on `drugstore-kafka-network`.
- **`--profile prod`** — starts only Nginx and store-service; set `POSTGRES_HOST`, `REDIS_HOST`, `SPRING_KAFKA_BOOTSTRAP_SERVERS`, etc. to your cloud endpoints in `.env.prod`.

> **Note:** All services require an active profile (`local` or `prod`), e.g. `--profile local`.

### App only (`docker-compose.app.yml`)

- **`--profile local`** or **`--profile prod`** — same containers (Nginx + app); the profile selects which env file you pass. Use `.env.local` for host/local deps or `.env.prod` for cloud deps.

## First-time setup

From the **`store-service/docker/`** directory:

```bash
# 1. Environment files
cp .env.example .env
cp .env.local.example .env.local   # or .env.prod.example for cloud

# 2. Edit .env — set JWT_SECRET_KEY (at least 32 characters for HS256)

# 3. Generate Nginx TLS certs (local/dev only)
chmod +x nginx/ssl/generate-certs.sh
./nginx/ssl/generate-certs.sh

# 4. (Full local stack only) Start the Kafka cluster — creates drugstore-kafka-network
cd ../../infrastructure/kafka && docker compose up -d && cd -
```

## How to run each profile

All commands below assume your current directory is **`store-service/docker/`**.

### Full stack — local (recommended for development)

Starts the complete stack: app, Nginx, PostgreSQL, Redis, and monitoring. Requires the Kafka cluster from `infrastructure/kafka`.

```bash
docker compose \
  -f docker-compose.full.yml \
  --profile local \
  --env-file .env \
  --env-file .env.local \
  up -d --build
```

Scale app replicas behind Nginx:

```bash
docker compose \
  -f docker-compose.full.yml \
  --profile local \
  --env-file .env \
  --env-file .env.local \
  up -d --scale store-service=3
```

### Full stack — prod (app containers + cloud infrastructure)

Starts only Nginx and store-service. Database, cache, Kafka, and monitoring are **not** started; configure cloud endpoints in `.env.prod`.

```bash
docker compose \
  -f docker-compose.full.yml \
  --profile prod \
  --env-file .env \
  --env-file .env.prod \
  up -d --build
```

> If Compose fails because `drugstore-kafka-network` does not exist, create it once with `docker network create drugstore-kafka-network` (the network is unused when Kafka endpoints are cloud-hosted), or use `docker-compose.app.yml --profile prod` instead.

### App only — local (external DB/Redis/Kafka on the host)

Use when Postgres/Redis/Kafka run on your machine (or from another compose project) and you only containerize the service.

1. Copy `.env.local.example` to `.env.local`.
2. Uncomment and set the `host.docker.internal` overrides in `.env.local`:

```bash
POSTGRES_HOST=host.docker.internal
REDIS_HOST=host.docker.internal
SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9093
```

3. Start:

```bash
docker compose \
  -f docker-compose.app.yml \
  --profile local \
  --env-file .env \
  --env-file .env.local \
  up -d --build
```

### App only — prod (cloud RDS / ElastiCache / MSK / etc.)

```bash
docker compose \
  -f docker-compose.app.yml \
  --profile prod \
  --env-file .env \
  --env-file .env.prod \
  up -d --build
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
| Grafana | `http://localhost:3000` (admin / change-me by default) |

External API traffic goes through Nginx on ports **80** and **443**. Port **8087** exposes the app directly on the host for dev tools (Swagger, etc.) without going through Nginx.

## Environment variables

| Variable | Local (full) | Prod |
|----------|--------------|------|
| `POSTGRES_HOST` | `postgres` | Cloud Postgres hostname |
| `REDIS_HOST` | `redis` | Cloud Redis hostname |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `kafka-1:9092,...` (Docker network) | Cloud Kafka bootstrap servers |
| `SWAGGER_ENABLED` | `true` | `false` |
| `JWT_SECRET_KEY` | Required | Required |

See `.env.example`, `.env.local.example`, and `.env.prod.example` for the full list.

## Common operations

```bash
# View logs
docker compose -f docker-compose.full.yml --profile local logs -f store-service

# Stop everything
docker compose -f docker-compose.full.yml --profile local down

# Stop and remove volumes (destructive — wipes local DB data)
docker compose -f docker-compose.full.yml --profile local down -v

# Rebuild after code changes
docker compose -f docker-compose.full.yml --profile local up -d --build store-service
```

## Production notes

- Replace self-signed Nginx certs in `nginx/ssl/` with CA-signed or Let's Encrypt certificates.
- Do not commit `.env`, `.env.local`, or `.env.prod` with real secrets.
- For Kubernetes/ECS, use `docker/Dockerfile` directly and inject env vars from your orchestrator's secret store.
- Managed monitoring (Grafana Cloud, Datadog, etc.) replaces the bundled Prometheus/Loki/Grafana stack in production.

## Troubleshooting

**Nginx fails to start (missing certs)** — Run `./nginx/ssl/generate-certs.sh`. If Docker created empty `nginx.key`/`nginx.crt` directories, remove them and regenerate.

**App cannot reach Postgres in app-only local mode** — Ensure Postgres listens on the host (`5432`) and use `POSTGRES_HOST=host.docker.internal`.

**App cannot reach Kafka in full local mode** — Start the Kafka stack first: `cd ../../infrastructure/kafka && docker compose up -d`. Verify `drugstore-kafka-network` exists: `docker network ls | grep drugstore-kafka`.

**Profile prod still tries to connect to `postgres`** — Use `--env-file .env.prod` so `POSTGRES_HOST` and `REDIS_HOST` point to cloud hosts, not Docker service names.

**JWT errors on startup** — Set `JWT_SECRET_KEY` in `.env` with at least 32 characters.
