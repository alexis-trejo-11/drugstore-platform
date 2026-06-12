# Notification Service — Docker

All containerization for **notification-service** lives under this directory: the image definition, reverse proxy, observability configs, and Compose files.

Environment variables live in a **single `.env` file at the project root** (`notification-service/.env`). Copy from `.env.example` and run Compose from the root.

## Quick reference

| Goal | Compose file | Profile (`COMPOSE_PROFILES` in `.env`) |
|------|--------------|----------------------------------------|
| Full local stack (app + MongoDB + monitoring) | `docker-compose.full.yml` | `local` |
| App containers + cloud MongoDB/Kafka | `docker-compose.full.yml` | `prod` |
| App + Nginx only, MongoDB/Kafka on host | `docker-compose.app.yml` | `local` |
| App + Nginx only, cloud infrastructure | `docker-compose.app.yml` | `prod` |

```bash
cd notification-service
cp .env.example .env
# Edit .env — set JWT_SECRET_KEY and MongoDB/Kafka hosts for your profile
chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh   # local/dev only

# Pick one (always pass --env-file .env from project root):
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
docker compose -f docker/docker-compose.app.yml  --env-file .env up -d --build
```

## Layout

```text
notification-service/
├── .env.example                    # All required + optional variables (copy to .env)
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.full.yml     # App + Nginx + MongoDB + monitoring
│   ├── docker-compose.app.yml      # App + Nginx only (no hardcoded app env vars)
│   ├── nginx/
│   └── observability/
```

## Compose files

| File | What it runs | When to use |
|------|----------------|-------------|
| `docker-compose.full.yml` | Nginx, notification-service, MongoDB, Prometheus, Loki, Grafana | Local development with everything in Docker |
| `docker-compose.app.yml` | Nginx + notification-service only | Deploy the app against external MongoDB/Kafka (host or cloud) |

`docker-compose.app.yml` loads all application settings from the root `.env` via `env_file` — no hardcoded environment variables on the app service.

`docker-compose.full.yml` only asserts **inter-container communication** defaults on `notification-service` (e.g. `MONGODB_URI` pointing at `mongodb`, `KAFKA_BOOTSTRAP_SERVERS` pointing at the Kafka cluster) so the bundled local stack works out of the box. Override those in `.env` for production.

## Profiles

Set `COMPOSE_PROFILES=local` or `COMPOSE_PROFILES=prod` in your root `.env`.

| Profile | Infrastructure | Typical use |
|---------|----------------|-------------|
| **`local`** | Bundled containers (full stack) or host services via `host.docker.internal` (app only) | Development and integration testing |
| **`prod`** | External managed services (Atlas, MSK, etc.) | Staging/production-like runs |

### Full stack (`docker-compose.full.yml`)

- **`local`** — starts MongoDB, Prometheus, Loki, and Grafana alongside the app. Connects to the shared Kafka cluster on `drugstore-kafka-network`.
- **`prod`** — starts only Nginx and notification-service; set `MONGODB_URI` and `KAFKA_BOOTSTRAP_SERVERS` to your cloud endpoints in `.env`.

### App only (`docker-compose.app.yml`)

- **`local`** — set `MONGODB_URI` and `KAFKA_BOOTSTRAP_SERVERS` to `host.docker.internal` endpoints in `.env` when MongoDB/Kafka run on the host.
- **`prod`** — set cloud Atlas/MSK endpoints in `.env`.

## First-time setup

From **`notification-service/`** (project root):

```bash
cp .env.example .env
# Edit .env — JWT_SECRET_KEY is required

chmod +x docker/nginx/ssl/generate-certs.sh
./docker/nginx/ssl/generate-certs.sh

# Full local stack: start the Kafka cluster (creates drugstore-kafka-network)
cd ../infrastructure/kafka && docker compose up -d && cd -
```

## How to run

All commands assume your current directory is **`notification-service/`**.

### Full stack — local (recommended for development)

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

Scale app replicas behind Nginx:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --scale notification-service=3
```

### Full stack — prod (app containers + cloud infrastructure)

Set `COMPOSE_PROFILES=prod` and cloud `MONGODB_URI` / `KAFKA_BOOTSTRAP_SERVERS` in `.env`, then:

```bash
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
```

### App only — local (external MongoDB/Kafka on the host)

In `.env`:

```bash
COMPOSE_PROFILES=local
MONGODB_URI=mongodb://host.docker.internal:27017/notification_db
KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9093
```

```bash
docker compose -f docker/docker-compose.app.yml --env-file .env up -d --build
```

### App only — prod (cloud Atlas / MSK / etc.)

Set `COMPOSE_PROFILES=prod` and cloud endpoints in `.env`, then:

```bash
docker compose -f docker/docker-compose.app.yml --env-file .env up -d --build
```

## Endpoints

| Service | URL (local full stack) |
|---------|-------------------------|
| API (HTTPS via Nginx) | `https://localhost:8091/` |
| Actuator health (via Nginx) | `https://localhost:8091/actuator/health` |
| Actuator health (direct) | `http://localhost:8093/actuator/health` |
| Prometheus UI | `http://localhost:9093` |
| Loki | `http://localhost:3101/ready` |
| Grafana | `http://localhost:3001` (admin / admin by default) |

External API traffic goes through Nginx on ports **8090** (HTTP) and **8091** (HTTPS). Port **8093** exposes the app directly on the host for dev tools without going through Nginx.

## Environment variables

See **`.env.example`** at the project root for the full list of required and optional variables.

| Variable | Local (full) | Prod |
|----------|--------------|------|
| `MONGODB_URI` | `mongodb://mongodb:27017/notification_db` | Atlas / managed MongoDB connection string |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka-1:9092,...` (Docker network) | Cloud Kafka bootstrap servers |
| `JWT_SECRET_KEY` | Required | Required |
| `GMAIL_USERNAME` / `GMAIL_APP_PASSWORD` | Optional (email disabled without them) | Required for email |
| `TWILIO_*` | Optional (`TWILIO_ENABLED=false` by default) | Set for SMS |

## Common operations

```bash
# View logs
docker compose -f docker/docker-compose.full.yml --env-file .env logs -f notification-service

# Stop everything
docker compose -f docker/docker-compose.full.yml --env-file .env down

# Stop and remove volumes (destructive — wipes local MongoDB data)
docker compose -f docker/docker-compose.full.yml --env-file .env down -v

# Rebuild after code changes
docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build notification-service
```

## Production notes

- Replace self-signed Nginx certs in `nginx/ssl/` with CA-signed or Let's Encrypt certificates.
- Do not commit `.env` with real secrets (ignored via monorepo `**/.env` rule).
- For Kubernetes/ECS, use `docker/Dockerfile` directly and inject env vars from your orchestrator's secret store.
- Managed monitoring (Grafana Cloud, Datadog, etc.) replaces the bundled Prometheus/Loki/Grafana stack in production.

## Troubleshooting

**Nginx fails to start (missing certs)** — Run `./docker/nginx/ssl/generate-certs.sh`. If Docker created empty `nginx.key`/`nginx.crt` directories, remove them and regenerate.

**App cannot reach Kafka in full local mode** — Start the Kafka stack first: `cd ../infrastructure/kafka && docker compose up -d`. Verify `drugstore-kafka-network` exists: `docker network ls | grep drugstore-kafka`.

**App cannot reach MongoDB in app-only local mode** — Ensure MongoDB listens on the host (`27017`) and set `MONGODB_URI=mongodb://host.docker.internal:27017/notification_db` in `.env`.

**Profile prod still tries to connect to `mongodb`** — Set `COMPOSE_PROFILES=prod` and cloud `MONGODB_URI` / `KAFKA_BOOTSTRAP_SERVERS` in `.env`.

**Port conflicts with other Drugstore services** — Adjust `NOTIFICATION_HOST_PORT`, `NGINX_HTTP_PORT`, `NGINX_HTTPS_PORT`, and monitoring ports in `.env`.
