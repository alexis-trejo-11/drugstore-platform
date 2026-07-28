# Infrastructure

## Infrastructure Metrics

- **Runtime Port:** `8080` (product-service container)
- **Reverse Proxy:** Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
- **Primary Datastore:** PostgreSQL 15
- **Cache:** Redis 7
- **Observability:** Prometheus + Loki + Grafana

## Deployment Layers

### Reverse Proxy / Edge

- Nginx terminates TLS on `:443`.
- Redirects HTTP `:80` to HTTPS.
- Routes to upstream `product-service:8080` using `least_conn`.

### Application Layer

- Product Service Spring Boot container (`dockerfile` build).
- Health endpoint used for compose health checks.

### Data and Cache Layer

- PostgreSQL for persistent catalog state.
- Redis for cache regions used by query decorators.

### Observability Layer

- Prometheus scrapes metrics.
- Loki receives logs.
- Grafana dashboards and datasource provisioning.

## Cloud Targets (placeholders)

- AWS ECS (future orchestration target)
- AWS RDS PostgreSQL (future managed DB option)
- AWS ElastiCache Redis (future managed cache option)
- AWS MSK (future managed Kafka option)

## Key Compose Snippets

### nginx service

- `nginx:1.27-alpine`
- Mounted `nginx.conf` and TLS material from `./nginx/ssl`
- Depends on healthy `product-service`

### product-service service

- `SPRING_PROFILES_ACTIVE=docker`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${DB_NAME:-product_db}`
- `SPRING_DATA_REDIS_HOST=redis`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` required from env

## Notes

- Grafana admin credentials are hardcoded (`admin/admin`) in compose; secure them for shared/staging/prod.
- Production TLS cert management and renewal workflow is not documented yet.