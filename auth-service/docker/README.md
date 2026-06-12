# Auth Service — Docker

All containerization for **auth-service** lives under this directory: the image definition, reverse proxy, observability configs, and Compose files.

Environment variables live in a **single `.env` file at the project root** (`auth-service/.env`). Copy from `.env.example` and run Compose from the root.

## Quick reference

| Goal | Compose file | Profile (`COMPOSE_PROFILES` in `.env`) |
|------|--------------|----------------------------------------|
| Full local stack (app + Redis + monitoring) | `docker-compose.full.yml` | `local` |
| App containers + cloud Redis/Kafka | `docker-compose.full.yml` | `prod` |
| App + Nginx only, Redis/Kafka on host | `docker-compose.app.yml` | `local` |
| App + Nginx only, cloud infrastructure | `docker-compose.app.yml` | `prod` |

```bash
cd auth-service
cp .env.example .env
# Edit .env — set JWT_SECRET_KEY, GITHUB_TOKEN, and Redis/Kafka/gRPC endpoints for your profile
chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh   # local/dev only

# Pick one (always pass --env-file .env from project root):
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
docker compose -f docker/docker-compose.app.yml  --env-file .env up -d --build
```

## Layout

```text
auth-service/
├── .env.example                    # All required + optional variables (copy to .env)
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.full.yml     # App + Nginx + Redis + monitoring
│   ├── docker-compose.app.yml      # App + Nginx only (no hardcoded app env vars)
│   ├── nginx/
│   └── observability/
```

## Compose files

| File | What it runs | When to use |
|------|----------------|-------------|
| `docker-compose.full.yml` | Nginx, auth-service, Redis, Prometheus, Loki, Grafana | Local development with everything in Docker |
| `docker-compose.app.yml` | Nginx + auth-service only | Deploy the app against external Redis/Kafka (host or cloud) |

`docker-compose.app.yml` loads all application settings from the root `.env` via `env_file` — no hardcoded environment variables on the app service.

`docker-compose.full.yml` only asserts **inter-container communication** defaults on `auth-service` (e.g. `REDIS_URL` pointing at `redis`, `KAFKA_BOOTSTRAP_SERVERS` pointing at the Kafka cluster) so the bundled local stack works out of the box. Override those in `.env` for production.

## Profiles

Set `COMPOSE_PROFILES=local` or `COMPOSE_PROFILES=prod` in your root `.env`.

| Profile | Infrastructure | Typical use |
|---------|----------------|-------------|
| **`local`** | Bundled containers (full stack) or host services via `host.docker.internal` (app only) | Development and integration testing |
| **`prod`** | External managed services (ElastiCache, MSK, etc.) | Staging/production-like runs |

### Full stack (`docker-compose.full.yml`)

- **`local`** — starts Redis, Prometheus, Loki, and Grafana alongside the app. Connects to the shared Kafka cluster on `drugstore-kafka-network`.
- **`prod`** — starts only Nginx and auth-service; set `REDIS_URL`, `KAFKA_BOOTSTRAP_SERVERS`, and gRPC endpoints in `.env`.

### App only (`docker-compose.app.yml`)

- **`local`** — set `REDIS_URL`, `KAFKA_BOOTSTRAP_SERVERS`, and gRPC host to `host.docker.internal` endpoints in `.env` when Redis/Kafka run on the host.
- **`prod`** — set cloud ElastiCache/MSK endpoints in `.env`.

## First-time setup

From **`auth-service/`** (project root):

```bash
cp .env.example .env
# Edit .env — JWT_SECRET_KEY and GITHUB_TOKEN are required for image build

chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh

# Full local stack only: start the Kafka cluster — creates drugstore-kafka-network
cd ../../infrastrucuture/kafka && docker compose up -d && cd -
```

## How to run

All commands assume your current directory is **`auth-service/`**.

### Full stack — local (recommended for development)

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

Scale app replicas behind Nginx:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --scale auth-service=3
```

### Full stack — prod (app containers + cloud infrastructure)

Set `COMPOSE_PROFILES=prod` and cloud `REDIS_URL` / `KAFKA_BOOTSTRAP_SERVERS` in `.env`, then:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

> If Compose fails because `drugstore-kafka-network` does not exist, create it once with `docker network create drugstore-kafka-network`, or use `docker-compose.app.yml` with `COMPOSE_PROFILES=prod` instead.

### App only — local (external Redis/Kafka on the host)

In `.env`:

```bash
COMPOSE_PROFILES=local
REDIS_URL=redis://host.docker.internal:6379
KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9093
GRPC_CLIENT_USER_SERVICE_HOST=host.docker.internal
GRPC_CLIENT_USER_SERVICE_PORT=9090
```

```bash
docker compose -f docker/docker-compose.app.yml --env-file .env up -d --build
```

### App only — prod (cloud ElastiCache / MSK / etc.)

Set `COMPOSE_PROFILES=prod` and cloud endpoints in `.env`, then:

```bash
docker compose -f docker/docker-compose.app.yml --env-file .env up -d --build
```

## Endpoints

| Service | URL (local full stack) |
|---------|-------------------------|
| API (HTTPS via Nginx) | `https://localhost/` |
| Actuator health (via Nginx) | `https://localhost/actuator/health` |
| Swagger UI (direct, local dev) | `http://localhost:8082/swagger-ui.html` |
| Prometheus UI | `http://localhost:9090` |
| Loki | `http://localhost:3100/ready` |
| Grafana | `http://localhost:3000` (admin / admin by default) |

External API traffic goes through Nginx on ports **80** and **443**. Port **8082** exposes the app directly on the host for dev tools (Swagger, etc.) without going through Nginx.

## Environment variables

See **`.env.example`** at the project root for the full list of required and optional variables.

| Variable | Local (full) | Prod |
|----------|--------------|------|
| `REDIS_URL` | `redis://redis:6379` | Cloud Redis URL |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka-1:9092,...` (Docker network) | Cloud Kafka bootstrap servers |
| `GRPC_CLIENT_USER_SERVICE_HOST` | `user-service` | Cloud / internal DNS |
| `SWAGGER_ENABLED` | `true` | `false` |
| `GITHUB_TOKEN` | Required for build | Required for build |
| `JWT_SECRET_KEY` | Required | Required |

## Common operations

```bash
# View logs
docker compose -f docker/docker-compose.full.yml --env-file .env logs -f auth-service

# Stop everything
docker compose -f docker/docker-compose.full.yml --env-file .env down

# Stop and remove volumes (destructive — wipes local Redis data)
docker compose -f docker/docker-compose.full.yml --env-file .env down -v

# Rebuild after code changes
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build auth-service
```

## Production notes

- Replace self-signed Nginx certs in `nginx/ssl/` with CA-signed or Let's Encrypt certificates.
- Do not commit `.env` with real secrets (ignored via monorepo `**/.env` rule).
- For Kubernetes/ECS, use `docker/Dockerfile` directly and inject env vars from your orchestrator's secret store.
- Managed monitoring (Grafana Cloud, Datadog, etc.) replaces the bundled Prometheus/Loki/Grafana stack in production.
- User persistence is delegated to **user-service** over gRPC — ensure `GRPC_CLIENT_USER_SERVICE_HOST` points to a reachable instance.

## Troubleshooting

**`GITHUB_TOKEN` build error** — Set `GITHUB_TOKEN` in the root `.env` (GitHub PAT with `read:packages`).

**Nginx fails to start (missing certs)** — Run `./docker/nginx/ssl/generate-certs.sh`. If Docker created empty `nginx.key`/`nginx.crt` directories, remove them and regenerate.

**App cannot reach Kafka in full local mode** — Start the Kafka stack first: `cd ../../infrastrucuture/kafka && docker compose up -d`. Verify `drugstore-kafka-network` exists: `docker network ls | grep drugstore-kafka`.

**App cannot reach Redis in app-only local mode** — Ensure Redis listens on the host (`6379`) and set `REDIS_URL=redis://host.docker.internal:6379` in `.env`.

**Profile prod still tries to connect to `redis`** — Set `COMPOSE_PROFILES=prod` and cloud `REDIS_URL` / `KAFKA_BOOTSTRAP_SERVERS` in `.env`.
