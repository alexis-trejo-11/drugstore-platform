
---

# InfrastructureMetric[]

metrics:
- label: "Runtime Port"
  value: "8080"
  icon: "server"
  description: "Spring Boot service internal container port."
- label: "Reverse Proxy"
  value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
  icon: "nginx"
  description: "TLS :443, redirect :80; upstream product_backend -> product-service:8080 (least_conn)."
- label: "Primary Datastore"
  value: "PostgreSQL 15"
  icon: "database"
  description: "Main persistence for product catalog and migrations via Flyway."
- label: "Cache"
  value: "Redis 7"
  icon: "zap"
  description: "Used by Spring Cache for product query acceleration."
- label: "Observability"
  value: "Prometheus + Loki + Grafana"
  icon: "line-chart"
  description: "Metrics and logs stack included in compose for local monitoring."

# CloudService[]

cloudServices:
- name: "PostgreSQL"
  purpose: "Transactional product data store"
  icon: "database"
  cost: "self-hosted"
- name: "Redis"
  purpose: "Caching product reads and search"
  icon: "zap"
  cost: "self-hosted"
- name: "Kafka"
  purpose: "Event transport for product lifecycle events"
  icon: "message-square"
  cost: "external/shared in platform"
- name: "Prometheus"
  purpose: "Metrics scraping"
  icon: "activity"
  cost: "self-hosted"
- name: "Loki"
  purpose: "Log aggregation"
  icon: "file-text"
  cost: "self-hosted"
- name: "Grafana"
  purpose: "Monitoring dashboards"
  icon: "bar-chart-2"
  cost: "self-hosted"
- name: "AWS ECS (placeholder)"
  purpose: "Future container orchestration target"
  icon: "cloud"
  cost: "placeholder - define per environment"
- name: "AWS RDS PostgreSQL (placeholder)"
  purpose: "Future managed relational database option"
  icon: "database"
  cost: "placeholder - define per environment"
- name: "AWS ElastiCache Redis (placeholder)"
  purpose: "Future managed cache option"
  icon: "zap"
  cost: "placeholder - define per environment"
- name: "AWS MSK (placeholder)"
  purpose: "Future managed Kafka option"
  icon: "message-square"
  cost: "placeholder - define per environment"

# DeploymentLayer[]

deploymentLayers:

- name: "Reverse Proxy / Edge"
  color: "#009688"
  components:
  - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "product-nginx — HTTPS entrypoint for Docker Compose"

- name: "Application Layer"
  color: "#2196F3"
  components:
  - name: "Product Service"
    icon: "spring"
    description: "Spring Boot API service with security, caching, and Kafka producer."

- name: "Data and Cache Layer"
  color: "#673AB7"
  components:
  - name: "PostgreSQL 15"
    icon: "database"
    description: "Persistent relational storage."
  - name: "Redis 7"
    icon: "zap"
    description: "In-memory cache storage."

- name: "Observability Layer"
  color: "#FF9800"
  components:
  - name: "Prometheus"
    icon: "activity"
    description: "Metrics collection via /actuator/prometheus."
  - name: "Loki"
    icon: "file-text"
    description: "Log centralization from service appender."
  - name: "Grafana"
    icon: "bar-chart-2"
    description: "Visualization and dashboards."

# DockerFile[]

dockerFiles:

- service: "nginx"
  description: "Reverse proxy from docker-compose.yml"
  content: |
    image: nginx:1.27-alpine
    container_name: product-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
      - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
- service: "product-service"
  description: "Core app container and env configuration"
  content: |
    build:
      context: .
      dockerfile: ./dockerfile
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SERVER_PORT=8080
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${DB_NAME:-product_db}
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=${SPRING_KAFKA_BOOTSTRAP_SERVERS}
---

# Infrastructure

- **product-nginx** fronts the Spring app on HTTP **8080** and exposes TLS on **443**.
- Data services are local compose PostgreSQL and Redis instances.
- Monitoring stack ships with Prometheus, Loki, and Grafana.

## Notes

- Grafana admin credentials are hardcoded as `admin/admin` in compose; replace for shared environments.
- TLS cert generation scripts are local-development focused; production cert lifecycle is still missing.