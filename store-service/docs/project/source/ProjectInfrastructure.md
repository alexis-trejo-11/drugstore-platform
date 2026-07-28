---
# InfrastructureMetric[]
metrics:
  - label: "Reverse proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "TLS :443, HTTP→HTTPS :80; health via /health; upstream store_backend → store-service:8080 (least_conn implied in nginx.conf)."
  - label: "Application runtime"
    value: "Eclipse Temurin 23 (JRE Alpine)"
    icon: "java"
    description: "Multi-stage dockerfile: JDK 23 noble build, JRE Alpine runtime + wget for healthchecks."
  - label: "Database"
    value: "PostgreSQL 15-alpine"
    icon: "postgres"
    description: "Host port 5431→5432; Flyway manages `stores` table."
  - label: "Cache"
    value: "Redis 7-alpine"
    icon: "redis"
    description: "Host port 6378→6379; optional REDIS_PASSWORD."
  - label: "Metrics"
    value: "Prometheus 2.54.1"
    icon: "prometheus"
    description: "Scrapes store-service /actuator/prometheus — verify scrape config in ./shared Prometheus scrape config (outside this monorepo)."
  - label: "Logs"
    value: "Grafana Loki 3.1.1"
    icon: "loki"
    description: "Logs via stdout; shared Promtail ships to Loki."
  - label: "Dashboards"
    value: "Grafana 11.1.4"
    icon: "grafana"
    description: "Default admin credentials in compose — **rotate for any shared env**."

# CloudService[] (placeholder — not deployed)
cloudServices:
  - name: "Amazon EKS"
    purpose: "Placeholder: Kubernetes target for store-service + ClusterIP Postgres operator"
    icon: "eks"
    cost: "TBD ($/month cluster + nodes)"
  - name: "Amazon RDS PostgreSQL"
    purpose: "Placeholder: managed primary DB with automated backups"
    icon: "rds"
    cost: "TBD"
  - name: "Amazon ElastiCache Redis"
    purpose: "Placeholder: shared Redis for cache between pods"
    icon: "elasticache"
    cost: "TBD"
  - name: "AWS Secrets Manager"
    purpose: "Placeholder: JWT_SECRET_KEY, DB credentials"
    icon: "secrets"
    cost: "TBD"
  - name: "Amazon MSK"
    purpose: "Placeholder: Kafka for StoreStatusChangedEvent (when implemented)"
    icon: "kafka"
    cost: "TBD"

# DeploymentLayer[]
deploymentLayers:
  - name: "Edge / TLS"
    color: "#009688"
    components:
      - name: "Nginx"
        icon: "nginx"
        description: "store-nginx; optional auto-generated dev certs via docker-entrypoint.d script."
  - name: "Compute"
    color: "#1976D2"
    components:
      - name: "store-service"
        icon: "spring"
        description: "Spring Boot fat JAR; scale with `docker compose up --scale store-service=N`."
  - name: "Data & cache"
    color: "#E65100"
    components:
      - name: "postgres"
        icon: "postgres"
        description: "Single instance volume `store-postgres-data`."
      - name: "redis"
        icon: "redis"
        description: "Single instance volume `store-redis-data`."
  - name: "Observability"
    color: "#6A1B9A"
    components:
      - name: "Prometheus"
        icon: "prometheus"
        description: "TSDB volume store-prometheus-data."
      - name: "Loki"
        icon: "loki"
        description: "Log aggregation volume store-loki-data."
      - name: "Grafana"
        icon: "grafana"
        description: "Provisioning under ./shared Grafana provisioning (outside this monorepo)."

# DockerFile[]
dockerFiles:
  - service: "store-service"
    description: "Multi-stage: gradle bootJar → JRE alpine non-root spring user"
    content: |
      FROM eclipse-temurin:23-jdk-noble AS builder
      WORKDIR /app
      COPY gradlew gradle settings.gradle build.gradle ./
      RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon
      COPY src ./src
      RUN ./gradlew bootJar --no-daemon
      FROM eclipse-temurin:23-jre-alpine
      WORKDIR /app
      RUN addgroup -S spring && adduser -S spring -G spring && apk add --no-cache wget
      COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar
      USER spring
      ENTRYPOINT ["java","-jar","/app/app.jar"]
  - service: "nginx"
    description: "Image nginx:1.27-alpine; config + ssl volume mounts from ./nginx"
    content: |
      image: nginx:1.27-alpine
      container_name: store-nginx
      ports: ["80:80","443:443"]
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl:/etc/nginx/ssl
---

# Infrastructure

Compose file **`docker-compose.yml`** defines **`drugstore_network`**, persistent volumes for Postgres/Redis/Observability, and **mandatory env** variables **`JWT_SECRET_KEY`** and **`SPRING_KAFKA_BOOTSTRAP_SERVERS`** (Kafka container is **not** defined in this file — supply an external broker or add a kafka service).

> [!danger] Blocking env without local Kafka  
> Services will fail to start if **`SPRING_KAFKA_BOOTSTRAP_SERVERS`** is unset, even though event publishing is a **no-op** in code — consider relaxing Spring Kafka auto-config or providing an embedded/disabled profile.

> [!warning] Grafana default login  
> `GF_SECURITY_ADMIN_USER/PASSWORD` default to **admin/admin** — change immediately outside localhost.

> [!note] Gradle docker task typo  
> `buildDockerImage` in `build.gradle` still tags **`order-service:latest`** — fix before CI pushes images.
