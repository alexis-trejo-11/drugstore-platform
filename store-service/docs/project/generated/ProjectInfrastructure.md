---
# InfrastructureMetric[]
metrics:
  - label: "Reverse proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "HTTPS 443, HTTP 80; proxies to store-service pool; see nginx/nginx.conf."
  - label: " JVM"
    value: "Temurin 23 JRE Alpine (runtime)"
    icon: "java"
    description: "Built with Temurin 23 JDK Noble; non-root spring user."
  - label: "Database"
    value: "PostgreSQL 15-alpine"
    icon: "postgres"
    description: "Published on host 5431→5432."
  - label: "Redis"
    value: "Redis 7-alpine"
    icon: "redis"
    description: "Published on host 6378→6379."
  - label: "Prometheus"
    value: "v2.54.1"
    icon: "prometheus"
    description: "Port 9090; shared Prometheus (outside this monorepo)."
  - label: "Loki"
    value: "v3.1.1"
    icon: "loki"
    description: "Port 3100."
  - label: "Grafana"
    value: "v11.1.4"
    icon: "grafana"
    description: "Port 3000; default admin credentials in compose — change outside dev."

cloudServices:
  - name: "AWS Account (placeholder)"
    purpose: "Org ID 123456789012 — dummy"
    icon: "aws"
    cost: "Est. $500/month TBD"
  - name: "EKS cluster drugstore-nonprod (placeholder)"
    purpose: "Run store-service Helm chart v0.0.0-dummy"
    icon: "eks"
    cost: "TBD"
  - name: "RDS pg15.store.internal (placeholder)"
    purpose: "Primary OLTP — Multi-AZ on"
    icon: "rds"
    cost: "TBD"
  - name: "ElastiCache redis.store.internal (placeholder)"
    purpose: "Shared cache"
    icon: "elasticache"
    cost: "TBD"
  - name: "MSK cluster drugstore-events (placeholder)"
    purpose: "Future StoreStatusChangedEvent topic store.status.v1"
    icon: "kafka"
    cost: "TBD"

deploymentLayers:
  - name: "Edge"
    color: "#009688"
    components:
      - name: "Nginx"
        icon: "nginx"
        description: "TLS termination; HTTP health `/health` for container probe."
  - name: "App"
    color: "#1565C0"
    components:
      - name: "store-service"
        icon: "spring"
        description: "Scalable replicas; SPRING_PROFILES_ACTIVE=docker."
  - name: "Data"
    color: "#E65100"
    components:
      - name: "postgres"
        icon: "postgres"
        description: "Volume store-postgres-data."
      - name: "redis"
        icon: "redis"
        description: "Volume store-redis-data."
  - name: "Observability"
    color: "#4527A0"
    components:
      - name: "Prometheus"
        icon: "prometheus"
        description: "Scrape store /actuator/prometheus."
      - name: "Loki"
        icon: "loki"
        description: "Centralized logs."
      - name: "Grafana"
        icon: "grafana"
        description: "Dashboards — provision datasources from repo."

dockerFiles:
  - service: "store-service"
    description: "See repository dockerfile — multi-stage bootJar."
    content: |
      # Build: eclipse-temurin:23-jdk-noble, Run: eclipse-temurin:23-jre-alpine
      ENTRYPOINT ["java", "-jar", "/app/app.jar"]
  - service: "nginx"
    description: "Upstream image; local config + ssl volume."
    content: |
      image: nginx:1.27-alpine
---

# Infrastructure

## Docker Compose (local / lab)

File: **`docker-compose.yml`**.

- **Network:** `drugstore_network` (bridge).
- **Volumes:** `store-postgres-data`, `store-redis-data`, `store-prometheus-data`, `store-loki-data`, `store-grafana-data`.
- **Scaling:** `docker compose up --scale store-service=3` (no fixed `container_name` on the app service).
- **Required env (fail-fast):** `JWT_SECRET_KEY`, `SPRING_KAFKA_BOOTSTRAP_SERVERS` — see `.env.example`.

### Service matrix

| Service | Image / build | Host ports | Notes |
|---------|----------------|------------|--------|
| store-service | `dockerfile` build | `${STORE_SERVICE_HOST_PORT:-8080}:8080` | Waits for postgres/redis health + observability starts |
| nginx | `nginx:1.27-alpine` | 80, 443 | Depends on healthy store-service |
| postgres | `postgres:15-alpine` | 5431→5432 | `DB_NAME` default `store_db` |
| redis | `redis:7-alpine` | 6378→6379 | Optional `REDIS_PASSWORD` |
| prometheus | `prom/prometheus:v2.54.1` | 9090 | |
| loki | `grafana/loki:3.1.1` | 3100 | |
| grafana | `grafana/grafana:11.1.4` | 3000 | **admin/admin** in YAML — rotate |

## TLS / Nginx

- Config: `nginx/nginx.conf` (mounted read-only).
- Certificates: `nginx/ssl/` — entrypoint script can generate dev PEMs (see `nginx/docker-entrypoint.d/`).

**Danger:** Self-signed or auto-generated certs are for **dev only**.

## Cloud (placeholder only)

No cloud resources are provisioned from this repo. Example **dummy** target:

- **Region:** `us-east-1` (placeholder)
- **EKS:** `drugstore-nonprod` (placeholder)
- **RDS endpoint:** `store-db.dummy.amazonaws.com:5432`
- **Secrets:** `arn:aws:secretsmanager:us-east-1:123456789012:secret:store-service/jwt-dummy`

Replace every value before any real deployment.

## Build / CI footguns

- **`build.gradle` → `buildDockerImage`** still references image name **`order-service:latest`** — incorrect for this module.
- **Compose** does not include a **Kafka** container; point `SPRING_KAFKA_BOOTSTRAP_SERVERS` at a real broker or add a local `kafka` service.

## Health & readiness

- App: `GET http://localhost:8080/actuator/health` (inside container).
- Nginx: `wget --spider http://localhost/health` per compose healthcheck.

---

**Highlighted:** Grafana default password, mandatory Kafka env without functional publisher, and incorrect Gradle docker image name are the top operational hazards to fix next.
