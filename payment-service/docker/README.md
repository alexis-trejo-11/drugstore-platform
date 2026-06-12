# Payment Service — Docker

All containerization for **payment-service** lives under this directory: the image definition, reverse proxy, observability configs, and Compose files.

Environment variables live in a **single `.env` file at the project root** (`payment-service/.env`). Copy from `.env.example` and run Compose from the root.

## Quick reference

| Goal | Compose file | Profile (`COMPOSE_PROFILES` in `.env`) |
|------|--------------|----------------------------------------|
| Full local stack (app + DB + Redis + monitoring) | `docker-compose.full.yml` | `local` |
| App containers + cloud DB/Redis | `docker-compose.full.yml` | `prod` |
| App + Nginx only, DB/Redis on host | `docker-compose.app.yml` | `local` |
| App + Nginx only, cloud infrastructure | `docker-compose.app.yml` | `prod` |

```bash
cd payment-service
cp .env.example .env
# Edit .env — set STRIPE_API_KEY, STRIPE_WEBHOOK_SECRET, and DB/Redis hosts for your profile
chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh   # local/dev only

# Pick one (always pass --env-file .env from project root):
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
docker compose -f docker/docker-compose.app.yml  --env-file .env up -d --build
```

## Layout

```text
payment-service/
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
| `docker-compose.full.yml` | Nginx, payment-service, PostgreSQL, Redis, Prometheus, Loki, Grafana | Local development with everything in Docker |
| `docker-compose.app.yml` | Nginx + payment-service only | Deploy the app against external DB/Redis (host or cloud) |

`docker-compose.app.yml` loads all application settings from the root `.env` via `env_file` — no hardcoded environment variables on the app service.

`docker-compose.full.yml` only asserts **inter-container communication** defaults on `payment-service` (e.g. `DATASOURCE_URL` pointing at `postgres`, `REDIS_URL` pointing at `redis`) so the bundled local stack works out of the box. Override those in `.env` for production.

## Profiles

Set `COMPOSE_PROFILES=local` or `COMPOSE_PROFILES=prod` in your root `.env`.

| Profile | Infrastructure | Typical use |
|---------|----------------|-------------|
| **`local`** | Bundled containers (full stack) or host services via `host.docker.internal` (app only) | Development and integration testing |
| **`prod`** | External managed services (RDS, ElastiCache, etc.) | Staging/production-like runs |

### Full stack (`docker-compose.full.yml`)

- **`local`** — starts PostgreSQL, Redis, Prometheus, Loki, and Grafana alongside the app.
- **`prod`** — starts only Nginx and payment-service; set `DATASOURCE_URL` and `REDIS_URL` to your cloud endpoints in `.env`.

### App only (`docker-compose.app.yml`)

- **`local`** — set `DATASOURCE_URL` and `REDIS_URL` to `host.docker.internal` endpoints in `.env` when Postgres/Redis run on the host.
- **`prod`** — set cloud RDS/ElastiCache endpoints in `.env`.

## First-time setup

From **`payment-service/`** (project root):

```bash
cp .env.example .env
# Edit .env — STRIPE_API_KEY and STRIPE_WEBHOOK_SECRET are required

chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh
```

## How to run

All commands assume your current directory is **`payment-service/`**.

### Full stack — local (recommended for development)

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

Scale app replicas behind Nginx:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --scale payment-service=3
```

### Full stack — prod (app containers + cloud infrastructure)

Set `COMPOSE_PROFILES=prod` and cloud `DATASOURCE_URL` / `REDIS_URL` in `.env`, then:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

### App only — local (external DB/Redis on the host)

In `.env`:

```bash
COMPOSE_PROFILES=local
DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/drugstore_payments
REDIS_URL=redis://host.docker.internal:6379
```

```bash
docker compose -f docker/docker-compose.app.yml --env-file .env up -d --build
```

### App only — prod (cloud RDS / ElastiCache / etc.)

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
| Swagger UI (direct, local dev) | `http://localhost:8085/swagger-ui.html` |
| Prometheus UI | `http://localhost:9090` |
| Loki | `http://localhost:3100/ready` |
| Grafana | `http://localhost:3000` (admin / change-me by default) |

External API traffic goes through Nginx on ports **80** and **443**. Port **8085** exposes the app directly on the host for dev tools (Swagger, etc.) without going through Nginx.

## Environment variables

See **`.env.example`** at the project root for the full list of required and optional variables.

| Variable | Local (full) | Prod |
|----------|--------------|------|
| `DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/drugstore_payments` | Cloud Postgres JDBC URL |
| `REDIS_URL` | `redis://redis:6379` | Cloud Redis URL |
| `STRIPE_API_KEY` | Required (`sk_test_...`) | Required (`sk_live_...`) |
| `STRIPE_WEBHOOK_SECRET` | Required | Required |
| `SWAGGER_ENABLED` | `true` | `false` |

## Common operations

```bash
# View logs
docker compose -f docker/docker-compose.full.yml --env-file .env logs -f payment-service

# Stop everything
docker compose -f docker/docker-compose.full.yml --env-file .env down

# Stop and remove volumes (destructive — wipes local DB data)
docker compose -f docker/docker-compose.full.yml --env-file .env down -v

# Rebuild after code changes
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build payment-service
```

## Production notes

- Replace self-signed Nginx certs in `nginx/ssl/` with CA-signed or Let's Encrypt certificates.
- Do not commit `.env` with real secrets (ignored via monorepo `**/.env` rule).
- For Kubernetes/ECS, use `docker/Dockerfile` directly and inject env vars from your orchestrator's secret store.
- Managed monitoring (Grafana Cloud, Datadog, etc.) replaces the bundled Prometheus/Loki/Grafana stack in production.

## Troubleshooting

**Nginx fails to start (missing certs)** — Run `./docker/nginx/ssl/generate-certs.sh`. If Docker created empty `nginx.key`/`nginx.crt` directories, remove them and regenerate.

**App cannot reach Postgres in app-only local mode** — Ensure Postgres listens on the host (`5432`) and set `DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/drugstore_payments` in `.env`.

**Profile prod still tries to connect to `postgres`** — Set `COMPOSE_PROFILES=prod` and cloud `DATASOURCE_URL` / `REDIS_URL` in `.env`.

**Stripe errors on startup** — Set `STRIPE_API_KEY` and `STRIPE_WEBHOOK_SECRET` in `.env`.
