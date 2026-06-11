# Infrastructure

This file mirrors **`docs/project/obsidian/ProjectInfrastructure.md`** in plain Markdown. Cloud rows use **explicit placeholders**.

## Topology (local Docker Compose)

| Component | Image / artifact | Ports (host→container) | Notes |
|-----------|------------------|-------------------------|-------|
| **user-service** | Build `dockerfile` | `${ORDER_SERVICE_HOST_PORT:-8086}:8080` | Env name is misleading (legacy **order**). |
| **nginx** | `nginx:1.27-alpine` | `80:80`, `443:443` | TLS; `/health` on :80 plain. |
| **postgres** | `postgres:15-alpine` | `5431:5432` | Default DB `user_db`. |
| **redis** | `redis:7-alpine` | `6378:6379` | |
| **prometheus** | `prom/prometheus:v2.54.1` | `9090:9090` | Binds host **9090** (coordinate with anything expecting gRPC on host). |
| **loki** | `grafana/loki:3.1.1` | `3100:3100` | |
| **grafana** | `grafana/grafana:11.1.4` | `3000:3000` | **Default admin/admin** — **dangerous outside lab**. |

**External dependency:** **Kafka** is **mandatory at runtime for compose** (`SPRING_KAFKA_BOOTSTRAP_SERVERS` has no fallback in compose env).

---

## Structured template (YAML block — mirrors Obsidian schema)

Used for tooling/import; cloud entries are **dummy**.

```yaml
# InfrastructureMetric[]
metrics:
  - label: "Reverse Proxy"
    value: "Nginx 1.27 Alpine"
    icon: "nginx"
    description: "user-nginx; TLS :443; upstream → user-service:8080 (least_conn)"
  - label: "Application HTTP"
    value: "8086 host default"
    icon: "spring"
    description: "ORDER_SERVICE_HOST_PORT→8086 (legacy env name)"

# CloudService[]  (dummy / not implemented in-repo)
cloudServices:
  - name: "AWS RDS PostgreSQL (placeholder)"
    purpose: "Primary HA DB"
    icon: "aws"
    cost: "TBD $/mo"
  - name: "AWS ElastiCache Redis (placeholder)"
    purpose: "Shared cache layer"
    icon: "aws"
    cost: "TBD"
  - name: "MSK Kafka (placeholder)"
    purpose: "Production user.* topics"
    icon: "aws"
    cost: "TBD"
  - name: "GCP GKE workload (dummy cluster id: prod-usc1-placeholder)"
    purpose: "Runs user-service + ingress"
    icon: "gcp"
    cost: "TBD"

# DeploymentLayer[]
deploymentLayers:
  - name: "Edge"
    color: "#009688"
    components:
      - name: "Nginx (Compose)"
        icon: "nginx"
        description: "TLS offload for local parity"
      - name: "Cloudflare / ALB (dummy)"
        icon: "cloud"
        description: "Not configured — placeholder only"
  - name: "Compute"
    color: "#2196F3"
    components:
      - name: "user-service container"
        icon: "docker"
        description: "Fat JAR; non-root user `spring`"
  - name: "Data"
    color: "#795548"
    components:
      - name: "PostgreSQL"
        icon: "postgres"
      - name: "Redis"
        icon: "redis"
  - name: "Observability"
    color: "#E91E63"
    components:
      - name: "Prometheus + Grafana + Loki"
        icon: "grafana"

# DockerFile[] (summaries)
dockerFiles:
  - service: user-service
    description: "Multi-stage Temurin 23 build → Alpine JRE 23 runtime"
  - service: nginx
    description: "Mount nginx.conf + ssl volume (+ entrypoint cert helper)"
```

## Reverse proxy behaviour

See `nginx/nginx.conf`: **`least_conn`** upstream `user_backend` targets **`user-service:8080`**, leveraging Docker DNS for **scaling replicas** (`docker compose up --scale user-service=N`).

### Danger / gap callouts

- **No HTTPS** directly on Spring Boot in standard compose path — TLS is **Nginx’s job** locally.  
- **Secrets**: Use real secret manager refs in prod (dummy: **`vault://secret/data/user-service#JWT_SECRET_KEY`**).
