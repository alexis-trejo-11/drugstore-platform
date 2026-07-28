---
# InfrastructureMetric[]
metrics:
  - label: "Java Version"
    value: "23"
    icon: "java"
    description: "Running on Eclipse Temurin JDK 23 with Spring Boot 3.3.2"

  - label: "Database"
    value: "PostgreSQL 15"
    icon: "database"
    description: "Persistent storage with Flyway migrations (V1__initial_schema.sql)"

  - label: "Cache/Rate Limiter"
    value: "Redis 7"
    icon: "redis"
    description: "Distributed rate limiting with token bucket algorithm"

  - label: "Container Image"
    value: "eclipse-temurin:23-jre-alpine"
    icon: "docker"
    description: "Multi-stage Docker build with non-root user (spring:spring)"

  - label: "HTTPS Port"
    value: "8443"
    icon: "lock"
    description: "SSL/TLS enabled with keystore.p12 certificate"

  - label: "Health Check"
    value: "/actuator/health"
    icon: "heart"
    description: "Spring Boot Actuator health endpoint with 30s interval"

  - label: "Metrics Endpoint"
    value: "/actuator/prometheus"
    icon: "metrics"
    description: "Prometheus-compatible metrics exposed by Micrometer registry"

  - label: "Observability Stack"
    value: "Prometheus + Loki + Grafana"
    icon: "monitoring"
    description: "Containerized monitoring stack for metrics, logs, and dashboards"

  - label: "Reverse Proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "TLS termination on :443, HTTP→HTTPS redirect on :80, least_conn load balancing across replicas"

# CloudService[]
cloudServices:
  - name: "PLACEHOLDER: AWS RDS"
    purpose: "Managed PostgreSQL database for production deployment"
    icon: "aws-rds"
    cost: "PLACEHOLDER: ~$30-100/month depending on instance size"

  - name: "PLACEHOLDER: AWS ElastiCache"
    purpose: "Managed Redis for production rate limiting and caching"
    icon: "aws-elasticache"
    cost: "PLACEHOLDER: ~$20-80/month depending on node type"

  - name: "PLACEHOLDER: AWS ECS/EKS"
    purpose: "Container orchestration for running the Docker container"
    icon: "aws-ecs"
    cost: "PLACEHOLDER: ~$30-150/month depending on Fargate/EC2"

  - name: "PLACEHOLDER: AWS Certificate Manager"
    purpose: "SSL/TLS certificates management for HTTPS"
    icon: "aws-certificate"
    cost: "PLACEHOLDER: Free (with ELB)"

  - name: "PLACEHOLDER: AWS CloudWatch"
    purpose: "Logs and metrics collection for monitoring"
    icon: "aws-cloudwatch"
    cost: "PLACEHOLDER: ~$10-50/month based on log volume"

  - name: "Grafana OSS"
    purpose: "Visualization dashboards for Prometheus metrics and Loki logs"
    icon: "grafana"
    cost: "Free (self-hosted in Docker)"

  - name: "Prometheus OSS"
    purpose: "Scrapes and stores time-series metrics from /actuator/prometheus"
    icon: "prometheus"
    cost: "Free (self-hosted in Docker)"

  - name: "Loki OSS"
    purpose: "Centralized log storage and querying for Logback streams"
    icon: "loki"
    cost: "Free (self-hosted in Docker)"

# DeploymentLayer[]
deploymentLayers:
  - name: "Client Layer"
    color: "#4CAF50"
    components:
      - name: "Frontend Application"
        icon: "react"
        description: "React/Angular/Vue frontend consuming the Address Service API"
      - name: "Mobile App (Future)"
        icon: "mobile"
        description: "PLACEHOLDER: Mobile applications accessing addresses via HTTPS"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "Address Service (Spring Boot)"
        icon: "spring"
        description: "Java 23 + Spring Boot 3.3.2 running in Docker container on port 8443"
      - name: "JwtAuthenticationFilter"
        icon: "security"
        description: "Shared library component for JWT validation from libs-kernel"

  - name: "Data Layer"
    color: "#9C27B0"
    components:
      - name: "PostgreSQL 15"
        icon: "postgres"
        description: "Relational database with addresses table, indexed on user_id and isDefault"
      - name: "Flyway Migrations"
        icon: "flyway"
        description: "Database versioning with V1__initial_schema.sql"

  - name: "Cache Layer"
    color: "#FF9800"
    components:
      - name: "Redis 7"
        icon: "redis"
        description: "Token bucket rate limiting counters with TTL expiration"

  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
        icon: "nginx"
        description: "Terminates TLS on :443, redirects HTTP :80 to HTTPS, least_conn load-balances to address-service replicas via Docker DNS"

  - name: "Observability Layer"
    color: "#00ACC1"
    components:
      - name: "Prometheus"
        icon: "prometheus"
        description: "Scrapes application metrics from /actuator/prometheus over HTTPS (internal network)"
      - name: "Loki"
        icon: "loki"
        description: "Receives application logs through Loki4j HTTP appender"
      - name: "Grafana"
        icon: "grafana"
        description: "Dashboards and exploration UI for metrics and logs"

# DockerFile[]
dockerFiles:
  - service: "address-service"
    description: "Multi-stage Docker build with Eclipse Temurin JDK 23 for build and JRE for runtime"
    content: |
      # Dockerfile (in the root of address-service)
      FROM eclipse-temurin:23-jdk-alpine AS builder

      WORKDIR /app

      # Copy gradle wrapper and build files
      COPY gradlew .
      COPY gradle gradle
      COPY gradle.properties .
      COPY build.gradle .
      COPY settings.gradle .

      # Give execution permissions to gradlew
      RUN chmod +x gradlew

      # Download dependencies (cache layer)
      RUN ./gradlew dependencies --no-daemon

      # Copy source code
      COPY src src

      # Build the application
      RUN ./gradlew bootJar --no-daemon

      # Execution stage
      FROM eclipse-temurin:23-jre-alpine

      WORKDIR /app

      # Create non-root user for security
      RUN addgroup -S spring && adduser -S spring -G spring

      # Copy the built JAR
      COPY --from=builder /app/build/libs/*.jar app.jar

      # Create directory for logs and config
      RUN mkdir -p /app/logs /app/config

      # Give permissions to the user
      RUN chown -R spring:spring /app

      # Create entrypoint script to fix mounted volume permissions
      RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
          echo 'chown -R spring:spring /app/logs 2>/dev/null || true' >> /app/entrypoint.sh && \
          echo 'exec su-exec spring:spring java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -Dlogging.file.path=/app/logs -jar /app/app.jar "$@"' >> /app/entrypoint.sh && \
          chmod +x /app/entrypoint.sh

      # Install su-exec for proper user switching
      USER root
      RUN apk add --no-cache su-exec

      # Health check (using HTTPS on 8443)
      HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
        CMD wget --quiet --tries=1 --spider --no-check-certificate https://localhost:8443/actuator/health || exit 1

      # Expose port (HTTPS)
      EXPOSE 8443

      # Entry command (run as root to fix permissions, then drop to spring user)
      ENTRYPOINT ["/app/entrypoint.sh"]

  - service: "postgres"
    description: "PostgreSQL 15 Alpine image for local development with persistent volume"
    content: |
      image: postgres:15-alpine
      container_name: address-postgres
      restart: unless-stopped
      environment:
        - POSTGRES_DB=address_db
        - POSTGRES_USER=${DB_USER:-postgres}
        - POSTGRES_PASSWORD=${DB_PASSWORD:-postgres}
      ports:
        - "5433:5432"
      volumes:
        - postgres-data:/var/lib/postgresql/data
      networks:
        - drugstore_network
      healthcheck:
        test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-postgres} -d address_db"]
        interval: 10s
        timeout: 5s
        retries: 5

  - service: "redis"
    description: "Redis 7 Alpine image for local rate limiting with persistence"
    content: |
      image: redis:7-alpine
      container_name: address-redis
      restart: unless-stopped
      command: redis-server --appendonly yes
      ports:
        - "6378:6379"
      volumes:
        - redis-data:/data
      networks:
        - drugstore_network
      healthcheck:
        test: ["CMD", "redis-cli", "ping"]
        interval: 10s
        timeout: 5s
        retries: 5

  - service: "prometheus"
    description: "Prometheus server that scrapes the service metrics endpoint"
    content: |
      image: prom/prometheus:v2.54.1
      container_name: address-prometheus
      command:
        - --config.file=/etc/prometheus/prometheus.yml
        - --storage.tsdb.path=/prometheus
        - --web.enable-lifecycle
      ports:
        - "9090:9090"
      volumes:
        - ./shared Prometheus scrape config (outside this monorepo):/etc/prometheus/prometheus.yml:ro

  - service: "loki"
    description: "Loki log aggregation backend used by Grafana Explore and panels"
    content: |
      image: grafana/loki:3.1.1
      container_name: address-loki
      command: -config.file=/etc/loki/local-config.yaml
      ports:
        - "3100:3100"

  - service: "grafana"
    description: "Grafana dashboard service with provisioned Prometheus and Loki data sources"
    content: |
      image: grafana/grafana:11.1.4
      container_name: address-grafana
      ports:
        - "3000:3000"
      environment:
        - GF_SECURITY_ADMIN_USER=admin
        - GF_SECURITY_ADMIN_PASSWORD=admin
      volumes:
        - ./shared Grafana provisioning (outside this monorepo)datasources:/etc/grafana/provisioning/datasources:ro

  - service: "nginx"
    description: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    content: |
      image: nginx:1.27-alpine
      container_name: address-nginx
      ports:
        - "80:80"    # HTTP → redirects to HTTPS
        - "443:443"  # HTTPS — TLS termination + load balancing
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        address-service:
          condition: service_healthy
      networks:
        - drugstore_network
      healthcheck:
        test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/health"]
        interval: 15s
        timeout: 5s
        retries: 3
---
# Infrastructure

> Docker Compose setup ready for local development with PostgreSQL, Redis, Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
