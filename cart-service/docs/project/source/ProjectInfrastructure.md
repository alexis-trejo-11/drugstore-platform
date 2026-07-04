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
    description: "Persistent storage with Flyway migrations"

  - label: "Cache"
    value: "Redis 7"
    icon: "redis"
    description: "Spring Cache with Redis for cart lookups"

  - label: "gRPC"
    value: "1.60.0"
    icon: "grpc"
    description: "Protobuf 3.25.1 for order-service communication"

  - label: "Kafka"
    value: "product-events"
    icon: "kafka"
    description: "Topic for product update/delete events"

  - label: "Container Image"
    value: "eclipse-temurin:23-jre-alpine"
    icon: "docker"
    description: "Multi-stage Docker build with shared-kernel"

  - label: "HTTPS Port"
    value: "8443"
    icon: "lock"
    description: "SSL/TLS enabled with keystore.p12 (internal only — traffic enters via Nginx)"

  - label: "Reverse Proxy"
    value: "Nginx 1.27"
    icon: "nginx"
    description: "TLS termination on :443, HTTP→HTTPS redirect on :80, least_conn load balancing across replicas"

  - label: "Health Check"
    value: "/actuator/health"
    icon: "heart"
    description: "Spring Boot Actuator with 30s interval"

# CloudService[]
cloudServices:
  - name: "PLACEHOLDER: AWS RDS"
    purpose: "Managed PostgreSQL for production cart data"
    icon: "aws-rds"
    cost: "PLACEHOLDER: ~$30-100/month"

  - name: "PLACEHOLDER: AWS ElastiCache"
    purpose: "Managed Redis for production caching"
    icon: "aws-elasticache"
    cost: "PLACEHOLDER: ~$20-80/month"

  - name: "PLACEHOLDER: AWS MSK"
    purpose: "Managed Kafka for product events"
    icon: "aws-msk"
    cost: "PLACEHOLDER: ~$100-500/month"

  - name: "PLACEHOLDER: AWS ECS/EKS"
    purpose: "Container orchestration"
    icon: "aws-ecs"
    cost: "PLACEHOLDER: ~$30-150/month"

# DeploymentLayer[]
deploymentLayers:
  - name: "Client Layer"
    color: "#4CAF50"
    components:
      - name: "Frontend Application"
        icon: "react"
        description: "React/Angular/Vue consuming cart API"
      - name: "Order Service"
        icon: "grpc"
        description: "Calls cart via gRPC during checkout"

  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Nginx 1.27"
        icon: "nginx"
        description: "Terminates TLS on :443, redirects HTTP :80 to HTTPS, least_conn load-balances to cart-service replicas via Docker DNS"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "Cart Service (Spring Boot)"
        icon: "spring"
        description: "Java 23 + Spring Boot 3.3.2 with DDD"
      - name: "Cart Aggregate Root"
        icon: "domain"
        description: "Cart.java with items and afterwardsItems"

  - name: "Data Layer"
    color: "#9C27B0"
    components:
      - name: "PostgreSQL 15"
        icon: "postgres"
        description: "Carts, CartItems, AfterwardItems tables"
      - name: "Flyway Migrations"
        icon: "flyway"
        description: "Database versioning"

  - name: "Cache Layer"
    color: "#FF9800"
    components:
      - name: "Redis 7"
        icon: "redis"
        description: "Spring Cache for cart lookups"

  - name: "Event Layer"
    color: "#F44336"
    components:
      - name: "Kafka"
        icon: "kafka"
        description: "Product events consumption"

# DockerFile[]
dockerFiles:
  - service: "cart-service"
    description: "Multi-stage Docker build with shared-kernel library compilation"
    content: |
      # Dockerfile (in the root of cart-service)
      FROM eclipse-temurin:23-jdk-alpine AS builder

      WORKDIR /app

      # Copy gradle wrapper and build files
      COPY gradlew .
      COPY gradle gradle
      COPY gradle.properties .
      COPY build.gradle .
      COPY settings.gradle .

      # Remove the org.gradle.java.home property
      RUN sed -i '/org.gradle.java.home/d' gradle.properties

      # Copy shared-kernel library
      COPY libs/shared-kernel libs/shared-kernel

      # Copy cart-service build file
      COPY cart-service/build.gradle cart-service/

      # Give execution permissions to gradlew
      RUN chmod +x gradlew

      # Build shared-kernel and publish to local Maven
      RUN ./gradlew :libs:shared-kernel:publishToMavenLocal --no-daemon

      # Download dependencies
      RUN ./gradlew :cart-service:dependencies --no-daemon

      # Copy source code
      COPY cart-service/src cart-service/src

      # Build the application
      RUN ./gradlew :cart-service:bootJar --no-daemon

      # Execution stage
      FROM eclipse-temurin:23-jre-alpine

      WORKDIR /app

      # Create non-root user
      RUN addgroup -S spring && adduser -S spring -G spring

      # Copy the built JAR
      COPY --from=builder /app/cart-service/build/libs/*.jar app.jar

      # Copy SSL keystore
      COPY cart-service/src/main/resources/keystore.p12 /app/keystore.p12

      # Create directories
      RUN mkdir -p /app/logs /app/config

      # Give permissions
      RUN chown -R spring:spring /app

      # Create entrypoint script
      RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
          echo 'chown -R spring:spring /app/logs 2>/dev/null || true' >> /app/entrypoint.sh && \
          echo 'exec su-exec spring:spring java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -Dlogging.file.path=/app/logs -jar /app/app.jar "$@"' >> /app/entrypoint.sh && \
          chmod +x /app/entrypoint.sh

      # Install su-exec
      USER root
      RUN apk add --no-cache su-exec wget

      # Health check
      HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
        CMD wget --quiet --tries=1 --no-check-certificate https://localhost:8443/actuator/health || exit 1

      EXPOSE 8443

      ENTRYPOINT ["/app/entrypoint.sh"]

  - service: "postgres"
    description: "PostgreSQL 15 for cart data"
    content: |
      image: postgres:15-alpine
      container_name: cart-postgres
      restart: unless-stopped
      environment:
        - POSTGRES_DB=cart_db
        - POSTGRES_USER=${DB_USER:-postgres}
        - POSTGRES_PASSWORD=${DB_PASSWORD:-postgres}
      ports:
        - "5433:5432"
      volumes:
        - postgres-data:/var/lib/postgresql/data
      networks:
        - drugstore_network
      healthcheck:
        test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-postgres} -d cart_db"]
        interval: 10s
        timeout: 5s
        retries: 5

  - service: "redis"
    description: "Redis 7 for caching"
    content: |
      image: redis:7-alpine
      container_name: cart-redis
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

  - service: "nginx"
    description: "Nginx reverse proxy — terminates TLS on :443, redirects :80 to HTTPS, least_conn load-balances across cart-service replicas via Docker DNS. cart-service port 8443 is NOT exposed to the host."
    content: |
      image: nginx:1.27-alpine
      container_name: cart-nginx
      ports:
        - "80:80"    # HTTP → redirects to HTTPS
        - "443:443"  # HTTPS — TLS termination + load balancing
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        cart-service:
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

> Docker Compose ready with PostgreSQL, Redis, Nginx reverse proxy, Prometheus, Loki, and Grafana. Nginx handles TLS termination on port 443 and HTTP-to-HTTPS redirection on port 80, load-balancing requests across cart-service replicas via Docker DNS (`least_conn`). The cart-service port 8443 is only exposed internally — all external traffic goes through Nginx. PLACEHOLDER: Kafka not included in docker-compose.yml (needs to be added for product-events). Missing: Kubernetes manifests, CI/CD pipeline. The service uses gRPC for order-service integration and Kafka for product events.
