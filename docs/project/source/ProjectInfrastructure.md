---
deploymentLayers:
  # address-service
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
  # auth-service
  - name: "Client Layer"
    color: "#4CAF50"
    components:
      - name: "Frontend Application"
        icon: "react"
        description: "React/Angular/Vue frontend consuming auth API"
      - name: "Mobile App (Future)"
        icon: "mobile"
        description: "PLACEHOLDER: Mobile applications via HTTPS/REST"

  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
        icon: "nginx"
        description: "Terminates TLS on :443, redirects HTTP :80 to HTTPS, least_conn load-balances to auth-service replicas via Docker DNS"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "Auth Service (Spring Boot)"
        icon: "spring"
        description: "Java 23 + Spring Boot 3.3.2 with DDD architecture"
      - name: "JWT Token Manager"
        icon: "token"
        description: "TokenFactory + TokenManager for ACCESS/REFRESH/ACTIVATION/TWO_FA"
      - name: "OAuth2 Authentication"
        icon: "oauth2"
        description: "CustomOAuth2UserService for social login"

  - name: "Communication Layer"
    color: "#FF9800"
    components:
      - name: "gRPC to User-Service"
        icon: "grpc"
        description: "Protobuf 3.25.1 + gRPC 1.60.0 for user data"
      - name: "Kafka Producer"
        icon: "kafka"
        description: "Spring Kafka for publishing 8+ event types"

  - name: "Storage Layer"
    color: "#9C27B0"
    components:
      - name: "Redis 7"
        icon: "redis"
        description: "Sessions (refresh tokens), rate limits, non-JWT tokens"
      - name: "User Service DB (PostgreSQL)"
        icon: "postgres"
        description: "External user data via gRPC (not direct DB access)"

# DockerFile[]
  # cart-service
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
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
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
  # employee-service
  - name: "Client Layer"
    color: "#4CAF50"
    components:
      - name: "Admin Frontend"
        icon: "react"
        description: "Admin UI for employee management"
      - name: "Config Server"
        icon: "config"
        description: "Spring Cloud Config Server (from parent dir)"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "Employee Service (Spring Boot)"
        icon: "spring"
        description: "Java 23 + Spring Boot 3.3.2 with CQS"
      - name: "Command Controller"
        icon: "controller"
        description: "EmployeeCommandController for write operations"
      - name: "Query Controller"
        icon: "controller"
        description: "EmployeeQueryController for read operations"

  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
        icon: "nginx"
        description: "TLS edge, redirects HTTP to HTTPS, and routes to employee-service replicas over internal HTTP :8081"

  - name: "Data Layer"
    color: "#9C27B0"
    components:
      - name: "PostgreSQL 15"
        icon: "postgres"
        description: "Employees, Certifications tables with indexes"
      - name: "Flyway Migrations"
        icon: "flyway"
        description: "V1__create_tables.sql, V2__insert_dummy_data.sql"

  - name: "Cache Layer"
    color: "#FF9800"
    components:
      - name: "Redis"
        icon: "redis"
        description: "Rate limiting with libs-kernel"

# DockerFile[]
  # inventory-service
  - name: "Reverse Proxy / Edge"
    color: "#009688"
    components:
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
        icon: "nginx"
        description: "inventory-nginx — terminates TLS on host :443"

  - name: "Application Layer"
    color: "#4CAF50"
    components:
      - name: "Inventory Service"
        icon: "spring"
        description: "Spring Boot 3.3.2 application with Java 23 (build.gradle) but Dockerfile uses Java 17"
      - name: "Actuator"
        icon: "monitoring"
        description: "Health, info, metrics, env, prometheus endpoints exposed"

  - name: "Data Layer"
    color: "#2196F3"
    components:
      - name: "PostgreSQL 15"
        icon: "postgresql"
        description: "Persistent storage with Flyway migrations (currently disabled)"
      - name: "Redis"
        icon: "redis"
        description: "Cache with 1 hour TTL, lettuce connection pool"

  - name: "Messaging Layer"
    color: "#FF9800"
    components:
      - name: "RabbitMQ"
        icon: "rabbitmq"
        description: "AMQP messaging for inventory events (INCONSISTENT: other services use Kafka)"

# DockerFile[]
  # order-service
  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
        icon: "nginx"
        description: "TLS termination on :443, HTTP→HTTPS on :80, load-balances order-service replicas"

  - name: "API Layer"
    color: "#4CAF50"
    components:
      - name: "SaleOrderController"
        icon: "controller"
        description: "REST endpoints for order CRUD operations accessible by ADMIN and EMPLOYEE roles"
      - name: "SaleOrderStatusController"
        icon: "controller"
        description: "REST endpoints for order status transitions and workflow management"
      - name: "UserOrderController"
        icon: "controller"
        description: "REST endpoints for customer-specific order access with CUSTOMER and ADMIN role access"
      - name: "AddressController"
        icon: "controller"
        description: "REST endpoints for delivery address management"
      - name: "UserController"
        icon: "controller"
        description: "REST endpoints for user management operations"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "OrderApplicationFacade"
        icon: "service"
        description: "Unified facade implementing both command and query services"
      - name: "OrderCommandHandler"
        icon: "handler"
        description: "Handles order command operations with logging decorators"
      - name: "OrderStatusCommandHandler"
        icon: "handler"
        description: "Handles order status transition commands"
      - name: "OrderQueryHandler"
        icon: "handler"
        description: "Handles order query operations with pagination and filtering"

  - name: "Domain Layer"
    color: "#FF9800"
    components:
      - name: "Order Aggregate Root"
        icon: "domain"
        description: "Core domain entity encapsulating order state, business rules, and validation"
      - name: "OrderStatus Enum"
        icon: "domain"
        description: "State machine defining valid order status transitions"
      - name: "DeliveryMethod Enum"
        icon: "domain"
        description: "Enum for STORE_PICKUP, EXPRESS_DELIVERY, STANDARD_DELIVERY"
      - name: "OrderCreatedEvent"
        icon: "event"
        description: "Domain event published when a new order is created"
      - name: "OrderStatusChangedEvent"
        icon: "event"
        description: "Domain event published when order status changes"

  - name: "Infrastructure Layer"
    color: "#9C27B0"
    components:
      - name: "OrderRepositoryImpl"
        icon: "repository"
        description: "JPA-based order repository implementation with specification-based search"
      - name: "JpaOrderRepository"
        icon: "repository"
        description: "Spring Data JPA repository interface for OrderModel"
      - name: "UserServiceImpl"
        icon: "service"
        description: "User service with caching and logging decorators"
      - name: "AddressServiceImpl"
        icon: "service"
        description: "Address service for delivery address management"

  - name: "Observability Layer"
    color: "#F44336"
    components:
      - name: "Spring Boot Actuator"
        icon: "monitoring"
        description: "Health checks, metrics, and environment info endpoints"
      - name: "Spring Boot Admin Client"
        icon: "monitoring"
        description: "Registers with admin server for centralized management"
      - name: "Logstash Encoder"
        icon: "logging"
        description: "JSON log encoding for ELK stack integration"
      - name: "OpenAPI/Swagger"
        icon: "docs"
        description: "Automated API documentation with springdoc-openapi"

# DockerFile[]
  # payment-service
  - name: "Reverse Proxy / Edge"
    color: "#009688"
    expanded: true
    responsibilities:
      - "Terminate TLS for browsers and API clients"
      - "Balance traffic across payment-service replicas"
    technologies:
      - "nginx:1.27-alpine"
    components:
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
        icon: "nginx"
        description: "Host ports :443 / :80; proxies HTTP to payment-service:8080"

  - name: "API Layer (Input Adapters)"
    color: "#4CAF50"
    components:
      - name: "PaymentController"
        icon: "controller"
        description: "REST endpoints for payment lifecycle (initiate, refund, query)"
      - name: "SaleController"
        icon: "controller"
        description: "REST endpoints for sale queries (read-only from API)"
      - name: "StripeWebhookController"
        icon: "controller"
        description: "Webhook receiver for Stripe events with signature verification"
    expanded: true
    responsibilities:
      - "Accept HTTP requests with validation"
      - "Map requests to application service methods"
      - "Return standardized ResponseWrapper responses"
      - "Verify Stripe webhook signatures"
    technologies:
      - "Spring Web MVC"
      - "Spring Validation"
      - "OpenAPI/Swagger"
      - "Jackson"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "PaymentApplicationService"
        icon: "service"
        description: "Unified interface for payment and sale operations"
      - name: "PaymentApplicationServiceImpl"
        icon: "service"
        description: "Orchestrates payment lifecycle and sale creation (if exists)"
    expanded: true
    responsibilities:
      - "Orchestrate domain logic execution"
      - "Handle payment initiation and refund processing"
      - "Process Stripe webhook events"
      - "Coordinate between Payment and Sale aggregates"
    technologies:
      - "Spring Boot"
      - "Lombok"
      - "Shared Kernel Library"

  - name: "Domain Layer (Core)"
    color: "#FF9800"
    components:
      - name: "Payment"
        icon: "domain"
        description: "Aggregate root with lifecycle: PENDING → PROCESSING → COMPLETED → REFUNDED"
      - name: "Sale"
        icon: "domain"
        description: "Aggregate root auto-generated from completed payments"
      - name: "PaymentStatus"
        icon: "domain"
        description: "Enum with 6 states and business rule validation"
      - name: "SaleStatus"
        icon: "domain"
        description: "Enum with 4 states for sale lifecycle"
      - name: "PaymentMethod"
        icon: "domain"
        description: "Enum for 6 payment methods"
      - name: "PaymentCompletedEvent"
        icon: "event"
        description: "Domain event when payment succeeds"
      - name: "PaymentFailedEvent"
        icon: "event"
        description: "Domain event when payment fails"
      - name: "Money"
        icon: "value"
        description: "Value object for monetary amounts with currency"
      - name: "PaymentGatewayRef"
        icon: "value"
        description: "Value object tracking Stripe PaymentIntent and Charge IDs"
    expanded: true
    responsibilities:
      - "Encapsulate business rules and invariants"
      - "Validate state transitions"
      - "Publish domain events"
      - "Enforce aggregate consistency boundaries"
    technologies:
      - "Java 23"
      - "Lombok"
      - "Domain-Driven Design"

  - name: "Infrastructure Layer (Output Adapters)"
    color: "#9C27B0"
    components:
      - name: "PaymentRepositoryImpl"
        icon: "repository"
        description: "JPA-based payment persistence"
      - name: "SaleRepositoryImpl"
        icon: "repository"
        description: "JPA-based sale persistence"
      - name: "JpaPaymentRepository"
        icon: "repository"
        description: "Spring Data JPA interface for PaymentEntity"
      - name: "JpaSaleRepository"
        icon: "repository"
        description: "Spring Data JPA interface for SaleEntity"
      - name: "StripeGatewayAdapter"
        icon: "gateway"
        description: "STUB - returns null/empty, not implemented!"
      - name: "SpringEventPublisherAdapter"
        icon: "event"
        description: "Spring ApplicationEventPublisher adapter"
      - name: "PaymentMapper"
        icon: "mapper"
        description: "Domain to JPA entity mapping"
      - name: "SaleMapper"
        icon: "mapper"
        description: "Domain to JPA entity mapping"
    expanded: true
    responsibilities:
      - "Implement output ports from domain"
      - "Persist domain objects"
      - "Integrate with external services (Stripe)"
      - "Publish domain events via Spring Events"
    technologies:
      - "Spring Data JPA"
      - "Hibernate"
      - "PostgreSQL"
      - "Redis"
      - "Stripe API (Planned)"

  - name: "Observability Layer"
    color: "#F44336"
    components:
      - name: "Spring Boot Actuator"
        icon: "monitoring"
        description: "Health and metrics endpoints (configured but not in build.gradle)"
      - name: "Spring Boot Admin Client"
        icon: "monitoring"
        description: "Registers with admin server for centralized management"
      - name: "OpenAPI/Swagger"
        icon: "docs"
        description: "Automated API documentation at /swagger-ui.html"
    expanded: false
    responsibilities:
      - "Expose health and metrics endpoints"
      - "Provide operational insights"
      - "Generate API documentation"
    technologies:
      - "Spring Boot Admin"
      - "SpringDoc OpenAPI"
      - "SLF4J"

# DockerFile[]
  # store-service
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
  # address-service
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
  # auth-service
  - service: "auth-service"
    description: "Multi-stage Docker build with shared-kernel library compilation and gRPC support"
    content: |
      # Dockerfile (in the root of auth-service)
      FROM eclipse-temurin:23-jdk-alpine AS builder

      WORKDIR /app

      # Copy gradle wrapper and parent build files
      COPY gradlew .
      COPY gradle gradle
      COPY gradle.properties .
      COPY build.gradle .
      COPY settings.gradle .

      # Remove the org.gradle.java.home property as it points to SDKMAN path
      RUN sed -i '/org.gradle.java.home/d' gradle.properties

      # Copy shared-kernel library
      COPY libs/shared-kernel libs/shared-kernel

      # Copy auth-service build file
      COPY auth-service/build.gradle auth-service/

      # Give execution permissions to gradlew
      RUN chmod +x gradlew

      # Build shared-kernel and publish to local Maven
      RUN ./gradlew :libs:shared-kernel:publishToMavenLocal --no-daemon

      # Download auth-service dependencies
      RUN ./gradlew :auth-service:dependencies --no-daemon

      # Copy auth-service source code
      COPY auth-service/src auth-service/src

      # Build the application
      RUN ./gradlew :auth-service:bootJar --no-daemon

      # Execution stage
      FROM eclipse-temurin:23-jre-alpine

      WORKDIR /app

      # Create non-root user for security
      RUN addgroup -S spring && adduser -S spring -G spring

      # Copy the built JAR
      COPY --from=builder /app/auth-service/build/libs/*.jar app.jar

      # Copy the SSL keystore
      COPY auth-service/src/main/resources/keystore.p12 /app/keystore.p12

      # Create directory for logs and config
      RUN mkdir -p /app/logs /app/config

      # Give permissions to the user
      RUN chown -R spring:spring /app

      # Create entrypoint script
      RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
          echo 'chown -R spring:spring /app/logs 2>/dev/null || true' >> /app/entrypoint.sh && \
          echo 'exec su-exec spring:spring java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -Dlogging.file.path=/app/logs -jar /app/app.jar "$@"' >> /app/entrypoint.sh && \
          chmod +x /app/entrypoint.sh

      # Install runtime utilities
      USER root
      RUN apk add --no-cache su-exec wget

      # Health check (using HTTPS on 8443)
      HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
        CMD wget --quiet --tries=1 --spider --no-check-certificate https://localhost:8443/actuator/health || exit 1

      # Expose port (HTTPS)
      EXPOSE 8443

      # Entry command
      ENTRYPOINT ["/app/entrypoint.sh"]

  - service: "redis"
    description: "Redis 7 Alpine image for session storage and rate limiting"
    content: |
      image: redis:7-alpine
      container_name: auth-redis
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
    description: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    content: |
      image: nginx:1.27-alpine
      container_name: auth-nginx
      ports:
        - "80:80"    # HTTP → redirects to HTTPS
        - "443:443"  # HTTPS — TLS termination + load balancing
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        auth-service:
          condition: service_healthy
      networks:
        - drugstore_network
      healthcheck:
        test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/health"]
        interval: 15s
        timeout: 5s
        retries: 3
  # cart-service
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
    description: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
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
  # employee-service
  - service: "employee-service"
    description: "PLACEHOLDER: Dockerfile not found in employee-service root"
    content: |
      # PLACEHOLDER: Dockerfile needs to be created
      # Reference dockerfile from address-service or cart-service for template
      # Should use eclipse-temurin:23-jdk-alpine for build
      # Should use eclipse-temurin:23-jre-alpine for runtime
      # Include shared-kernel library build step
      # Copy keystore.p12 for HTTPS (if applicable)
      # Health check on appropriate port (check application.yml)

  - service: "nginx"
    description: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    content: |
      image: nginx:1.27-alpine
      container_name: employee-nginx
      ports:
        - "80:80"
        - "443:443"
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        employee-service:
          condition: service_started
  # inventory-service
  - service: "inventory-service"
    description: "Dockerfile using openjdk:17-jdk-slim - VERSION MISMATCH with build.gradle which specifies Java 23"
    content: |
      # Use an official OpenJDK runtime as a parent image
      FROM openjdk:17-jdk-slim

      # Set the working directory in the container
      WORKDIR /app

      # Copy the entire project structure
      COPY .. .

      # Install dependencies and build the specific project
      RUN ./gradlew :inventory-service:build -x test

      # Set the working directory to the inventory-service for the runtime
      WORKDIR /app/inventory-service

      # Expose the port the app runs on
      EXPOSE 8082

      # Run the jar file
      ENTRYPOINT ["java", "-jar", "build/libs/inventory-service-0.0.1-SNAPSHOT.jar"]

  - service: "nginx"
    description: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    content: |
      image: nginx:1.27-alpine
      container_name: inventory-nginx
      ports:
        - "80:80"
        - "443:443"
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        inventory-service:
          condition: service_healthy
  # order-service
  - service: "order-service"
    description: "Spring Boot container on HTTP :8080 in compose (image order-service:latest)"
    content: |
      image: order-service:latest
      hostname: order-service
      expose: ["8080"]
      ports: ["8086:8080"]
      environment:
        - SERVER_PORT=8080
        - SPRING_PROFILES_ACTIVE=docker
  - service: "nginx"
    description: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    content: |
      image: nginx:1.27-alpine
      container_name: order-nginx
      ports:
        - "80:80"
        - "443:443"
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        order-service:
          condition: service_healthy
  - service: "opensearch"
    description: "OpenSearch 2.9.0 container for log aggregation and search"
    content: "image: opensearchproject/opensearch:2.9.0\nenvironment:\n  - discovery.type=single-node\n  - OPENSEARCH_JAVA_OPTS=-Xms512m -Xmx512m\n  - plugins.security.disabled=true\nports:\n  - \"9200:9200\""
  - service: "logstash"
    description: "Logstash 8.11.0 container for processing and shipping logs to OpenSearch"
    content: "image: docker.elastic.co/logstash/logstash:8.11.0\nports:\n  - \"5044:5044\"\n  - \"9600:9600\"\nenvironment:\n  LS_JAVA_OPTS: \"-Xmx256m -Xms128m\"\n  XPACK_MONITORING_ENABLED: \"false\""
  - service: "opensearch-dashboards"
    description: "OpenSearch Dashboards 2.9.0 for log visualization"
    content: "image: opensearchproject/opensearch-dashboards:2.9.0\nports:\n  - \"5601:5601\"\nenvironment:\n  OPENSEARCH_HOSTS: '[\"http://elasticsearch:9200\"]'"
  # payment-service
  - service: "payment-service"
    description: "Dockerfile not yet created - planned for cloud deployment. Use 'gradle build' to create jar."
    content: "# Dockerfile placeholder - to be implemented\n# Build with: docker build -t payment-service:latest .\n# Planned base image: eclipse-temurin:23-jre-alpine\n# Planned port: 8085 (HTTP, no SSL)"
  - service: "postgres"
    description: "PostgreSQL container for local development (commented out in docker-compose - not present)"
    content: "# PostgreSQL not configured in docker-compose.yml\n# Planned: image: postgres:15\n# Planned port: 5432"
  - service: "redis"
    description: "Redis container for caching (not configured in docker-compose)"
    content: "# Redis not configured in docker-compose.yml\n# Planned: image: redis:7-alpine\n# Planned port: 6379"
  - service: "nginx"
    description: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    content: |
      image: nginx:1.27-alpine
      container_name: payment-nginx
      ports:
        - "80:80"
        - "443:443"
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        payment-service:
          condition: service_healthy
  # store-service
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
cloudServices:
  # address-service
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
  # auth-service
  - name: "PLACEHOLDER: AWS ElastiCache"
    purpose: "Managed Redis for production session and rate limit storage"
    icon: "aws-elasticache"
    cost: "PLACEHOLDER: ~$20-80/month depending on node type"

  - name: "PLACEHOLDER: AWS MSK (Managed Streaming for Kafka)"
    purpose: "Managed Kafka for production event streaming"
    icon: "aws-msk"
    cost: "PLACEHOLDER: ~$100-500/month depending on brokers"

  - name: "PLACEHOLDER: AWS ECS/EKS"
    purpose: "Container orchestration for running the auth-service"
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

# DeploymentLayer[]
  # cart-service
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
  # employee-service
  - name: "PLACEHOLDER: AWS RDS"
    purpose: "Managed PostgreSQL for production employee data"
    icon: "aws-rds"
    cost: "PLACEHOLDER: ~$30-100/month"

  - name: "PLACEHOLDER: AWS ElastiCache"
    purpose: "Managed Redis for production rate limiting"
    icon: "aws-elasticache"
    cost: "PLACEHOLDER: ~$20-80/month"

  - name: "PLACEHOLDER: Spring Cloud Config Server"
    purpose: "Centralized configuration management"
    icon: "spring-cloud"
    cost: "PLACEHOLDER: Free (self-hosted)"

  - name: "PLACEHOLDER: AWS ECS/EKS"
    purpose: "Container orchestration"
    icon: "aws-ecs"
    cost: "PLACEHOLDER: ~$30-150/month"

# CloudService[]
  # inventory-service
  - name: "PostgreSQL"
    purpose: "Primary database for inventory, batches, reservations, and movements"
    icon: "postgresql"
    cost: "PLACEHOLDER"
  - name: "Redis"
    purpose: "Caching layer for inventory queries and rate limiting"
    icon: "redis"
    cost: "PLACEHOLDER"
  - name: "RabbitMQ"
    purpose: "Message queue for asynchronous inventory events"
    icon: "rabbitmq"
    cost: "PLACEHOLDER"

# DeploymentLayer[]
  # order-service
  - name: "PostgreSQL"
    purpose: "Primary relational database for order persistence"
    icon: "database"
    cost: "TBD - Planned for cloud deployment"
  - name: "Redis"
    purpose: "Caching layer for improved read performance"
    icon: "cache"
    cost: "TBD - Planned for cloud deployment"
  - name: "OpenSearch"
    purpose: "Log aggregation and search"
    icon: "search"
    cost: "TBD - Planned for cloud deployment"
  - name: "Spring Boot Admin"
    purpose: "Application monitoring and management"
    icon: "monitoring"
    cost: "Free (self-hosted)"

# DeploymentLayer[]
  # payment-service
  - name: "PostgreSQL"
    purpose: "Primary relational database for Payment and Sale persistence"
    icon: "database"
    cost: "TBD - Planned for cloud deployment"
  - name: "Redis"
    purpose: "Caching layer for improved read performance"
    icon: "cache"
    cost: "TBD - Planned for cloud deployment"
  - name: "Stripe"
    purpose: "Payment gateway for processing payments and refunds"
    icon: "payment"
    cost: "Transaction fees apply (2.9% + $0.30 per transaction)"
  - name: "Kafka"
    purpose: "Event streaming platform (planned for future)"
    icon: "queue"
    cost: "TBD - Dependency added but not implemented"
  - name: "Spring Boot Admin"
    purpose: "Application monitoring and management"
    icon: "monitoring"
    cost: "Free (self-hosted)"

# DeploymentLayer[]
  # store-service
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
metrics:
  # address-service
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
  # auth-service
  - label: "Java Version"
    value: "23"
    icon: "java"
    description: "Running on Eclipse Temurin JDK 23 with Spring Boot 3.3.2"

  - label: "Redis"
    value: "7-alpine"
    icon: "redis"
    description: "Session storage, rate limiting, and token persistence"

  - label: "Kafka"
    value: "PLACEHOLDER"
    icon: "kafka"
    description: "Event streaming platform for publishing auth events"

  - label: "gRPC"
    value: "1.60.0"
    icon: "grpc"
    description: "Protobuf-based RPC for user-service communication"

  - label: "Container Image"
    value: "eclipse-temurin:23-jre-alpine"
    icon: "docker"
    description: "Multi-stage Docker build with shared-kernel library"

  - label: "HTTPS Port"
    value: "8443"
    icon: "lock"
    description: "SSL/TLS enabled with keystore.p12 certificate (internal only — traffic enters via Nginx)"

  - label: "Reverse Proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "TLS termination on :443, HTTP→HTTPS redirect on :80, least_conn load balancing across replicas"

  - label: "Health Check"
    value: "/actuator/health"
    icon: "heart"
    description: "Spring Boot Actuator with 30s interval"

  - label: "Integration tests"
    value: "Testcontainers"
    icon: "test-tube"
    description: "JUnit + Redis & Kafka containers; in-process gRPC UserService; profiles integration-test + test; Docker required (skipped if unavailable)"

# CloudService[]
  # cart-service
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
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "TLS termination on :443, HTTP→HTTPS redirect on :80, least_conn load balancing across replicas"

  - label: "Health Check"
    value: "/actuator/health"
    icon: "heart"
    description: "Spring Boot Actuator with 30s interval"

# CloudService[]
  # employee-service
  - label: "Java Version"
    value: "23"
    icon: "java"
    description: "Running on Eclipse Temurin JDK 23 with Spring Boot 3.3.2"

  - label: "Database"
    value: "PostgreSQL 15"
    icon: "database"
    description: "Persistent storage with Flyway migrations (V1, V2)"

  - label: "Cache"
    value: "Redis"
    icon: "redis"
    description: "Used for rate limiting with libs-kernel"

  - label: "Config Server"
    value: "Spring Cloud Config"
    icon: "config"
    description: "Centralized configuration from config-server"

  - label: "Container Image"
    value: "PLACEHOLDER"
    icon: "docker"
    description: "PLACEHOLDER: Dockerfile not found in employee-service"

  - label: "HTTPS Port"
    value: "443 (Nginx)"
    icon: "lock"
    description: "TLS terminated by Nginx at :443, proxied internally to employee-service:8081"

  - label: "Health Check"
    value: "/actuator/health"
    icon: "heart"
    description: "Spring Boot Actuator (PLACEHOLDER: verify)"

  - label: "Reverse Proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "HTTP→HTTPS redirect on :80, TLS termination on :443, least_conn load balancing"

# InfrastructureMetric[]
  # inventory-service
  - label: "API Response Time"
    value: "PLACEHOLDER"
    icon: "speed"
    description: "Average API response time for inventory operations"
  - label: "Cache Hit Rate"
    value: "PLACEHOLDER"
    icon: "storage"
    description: "Redis cache hit rate for inventory queries"
  - label: "Batch Expiration Alerts"
    value: "PLACEHOLDER"
    icon: "warning"
    description: "Number of batches near expiration (30 days threshold)"
  - label: "Active Reservations"
    value: "PLACEHOLDER"
    icon: "lock"
    description: "Currently active stock reservations"
  - label: "Reverse Proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "TLS :443, redirect :80; upstream inventory_backend → inventory-service:8080 (least_conn)"

# CloudService[]
  # order-service
  - label: "Service Port"
    value: "8080 (compose)"
    icon: "server"
    description: "HTTP inside Docker network when SPRING_PROFILES_ACTIVE=docker; optional standalone SSL profile may differ"
  - label: "Reverse Proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "TLS on host :443, redirect :80, upstream order_backend with least_conn to order-service:8080"
  - label: "Database"
    value: "PostgreSQL 15"
    icon: "database"
    description: "Relational database for order persistence with Flyway migrations"
  - label: "Cache"
    value: "Redis 7"
    icon: "cache"
    description: "In-memory data store for caching frequently accessed order data"
  - label: "Search Engine"
    value: "OpenSearch 2.9.0"
    icon: "search"
    description: "Distributed search and analytics engine for log aggregation"
  - label: "Log Processor"
    value: "Logstash 8.11.0"
    icon: "logs"
    description: "Data processing pipeline for shipping logs to OpenSearch"
  - label: "Dashboard"
    value: "OpenSearch Dashboards 2.9.0"
    icon: "dashboard"
    description: "Visualization and monitoring dashboard for logs and metrics"
  - label: "Rate Limiting"
    value: "5000 req/hour"
    icon: "rate-limit"
    description: "Global rate limit with per-profile limits for standard, sensitive, public, and admin endpoints"

# CloudService[]
  # payment-service
  - label: "Service Port"
    value: "8080 (compose)"
    icon: "server"
    description: "HTTP inside Docker network; host publishes 8085:8080 for direct access"
  - label: "Reverse Proxy"
    value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
    icon: "nginx"
    description: "TLS :443 and redirect :80; upstream payment_backend → payment-service:8080 with least_conn"
  - label: "Database"
    value: "PostgreSQL"
    icon: "database"
    description: "Relational database for payment and sale persistence with Flyway migrations"
  - label: "Cache"
    value: "Redis"
    icon: "cache"
    description: "In-memory data store for caching frequently accessed payment data"
  - label: "Message Broker"
    value: "Kafka (Dependency Only)"
    icon: "queue"
    description: "Kafka dependency in build.gradle but no implementation found"
  - label: "Payment Gateway"
    value: "Stripe (Planned)"
    icon: "payment"
    description: "StripeGatewayAdapter is a STUB - not implemented"
  - label: "Monitoring"
    value: "Spring Boot Admin 3.0.0"
    icon: "monitoring"
    description: "Application monitoring and management client"

# CloudService[]
  # store-service
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
    description: "Log shipping via loki-logback-appender in app."
  - label: "Dashboards"
    value: "Grafana 11.1.4"
    icon: "grafana"
    description: "Default admin credentials in compose — **rotate for any shared env**."

# CloudService[] (placeholder — not deployed)
---

# Project Infrastructure

> Auto-generated by `scripts/merge_service_sources.py`. Edit service-level `docs/project/source/*.md` files, then regenerate.

<!-- BEGIN address-service -->
<!-- Source: address-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure

> Docker Compose setup ready for local development with PostgreSQL, Redis, Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).

<!-- END address-service -->

<!-- BEGIN auth-service -->
<!-- Source: auth-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure

> Docker Compose has Redis, Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).

<!-- END auth-service -->

<!-- BEGIN cart-service -->
<!-- Source: cart-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure

> Docker Compose ready with PostgreSQL, Redis, Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).

<!-- END cart-service -->

<!-- BEGIN employee-service -->
<!-- Source: employee-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure

> Employee stack now includes Nginx as reverse proxy and load balancer. External traffic enters via HTTPS on :443 at Nginx and is proxied internally to employee-service on HTTP :8081. HTTP :80 is redirected to HTTPS. This enables horizontal scaling of employee-service replicas behind a stable TLS endpoint.

Check and validate nginx i just shutdown my brain while dockerization, need to assert https at connection to front or portafolio

<!-- END employee-service -->

<!-- BEGIN inventory-service -->
<!-- Source: inventory-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure

> **Compose:** App-only `docker-compose.yml` at the service root. Shared infra (DB, Redis, observability) lives outside this monorepo.

> **CRITICAL ISSUES:**
> 1. **Java Version Mismatch**: build.gradle specifies Java 23 (line 12: `JavaLanguageVersion.of(23)`) but Dockerfile uses `openjdk:17-jdk-slim`. This will cause runtime issues as compiled classes with Java 23 (class version 69) won't run on Java 17 (max class version 61).
> 2. **RabbitMQ vs Kafka Inconsistency**: inventory-service uses RabbitMQ (spring-boot-starter-amqp) while other services (address, auth, cart) use Kafka. This creates integration issues.
> 3. **docker-compose.yml**: Present with nginx + observability — reconcile Dockerfile / ports / Flyway with local runtime.
> 4. **Flyway Disabled**: Flyway is configured but `enabled: false` in application.yml (line 90), meaning database migrations won't run automatically.
> 5. **Port Mismatch**: Dockerfile exposes port 8082 but application.yml sets server.port=8083.

---

## Automated integration tests (CI-friendly stack)

When `./gradlew test` runs, the **`test`** Spring profile loads **`application-test.yml`**: H2 in-memory database (PostgreSQL compatibility mode), Flyway disabled, Hibernate schema create-drop, Redis/Kafka auto-config excluded, global rate limiting disabled. REST integration tests send **`Authorization: Bearer …`** JWTs built with **`IntegrationTestJwtSupport`** so **`JwtAuthenticationFilter`** and **`JwtTokenValidator`** execute without mocking Spring Security. See **`docs/project/generated/ProjectFeature.md`** for the full checklist.

<!-- END inventory-service -->

<!-- BEGIN notification-service -->
<!-- Source: notification-service/docs/project/source/ProjectInfrastructure.md -->
---

# InfrastructureMetric[]

metrics:

- label: "Reverse Proxy"
  value: "Not bundled"
  icon: "nginx"
  description: "No nginx/ folder or edge container in this repo — terminate TLS at platform ingress or copy pattern from sibling services"

# CloudService[]

cloudServices:

- name: ""

  purpose: ""

  icon: ""

  cost: ""

# DeploymentLayer[]

deploymentLayers:

- name: "" color: ""

    # DeploymentComponent[]

    components:
    - name: ""
      icon: ""
      description: ""

# DockerFile[]

dockerFiles:

- service: "" description: "" content: ""

---

# Infrastructure

> This module does **not** include an Nginx compose service. Use Kubernetes Ingress, a shared gateway, or mirror `*-service/nginx/` + compose snippets from cart/order/product services when you need TLS termination at the edge.

<!-- END notification-service -->

<!-- BEGIN order-service -->
<!-- Source: order-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure
> **Compose:** App-only at the service root. Shared Postgres/Redis/observability live outside this monorepo.

<!-- END order-service -->

<!-- BEGIN payment-service -->
<!-- Source: payment-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure
> **Compose:** `docker-compose.yml` runs payment-service on HTTP **8080** inside the network, **Nginx** (`payment-nginx`) on host **80/443**, plus Prometheus, Loki, and Grafana. HTTPS for external callers terminates at Nginx; optional plain HTTP via host **8085**. Stripe adapter remains a stub — see codebase. PostgreSQL and Redis are expected via Spring configuration/env when not added to compose.

<!-- END payment-service -->

<!-- BEGIN product-service -->
<!-- Source: product-service/docs/project/source/ProjectInfrastructure.md -->
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

<!-- END product-service -->

<!-- BEGIN store-service -->
<!-- Source: store-service/docs/project/source/ProjectInfrastructure.md -->
# Infrastructure

Compose file **`docker-compose.yml`** defines **`drugstore_network`**, persistent volumes for Postgres/Redis/Observability, and **mandatory env** variables **`JWT_SECRET_KEY`** and **`SPRING_KAFKA_BOOTSTRAP_SERVERS`** (Kafka container is **not** defined in this file — supply an external broker or add a kafka service).

> [!danger] Blocking env without local Kafka  
> Services will fail to start if **`SPRING_KAFKA_BOOTSTRAP_SERVERS`** is unset, even though event publishing is a **no-op** in code — consider relaxing Spring Kafka auto-config or providing an embedded/disabled profile.

> [!warning] Grafana default login  
> `GF_SECURITY_ADMIN_USER/PASSWORD` default to **admin/admin** — change immediately outside localhost.

> [!note] Gradle docker task typo  
> `buildDockerImage` in `build.gradle` still tags **`order-service:latest`** — fix before CI pushes images.

<!-- END store-service -->

<!-- BEGIN user-service -->
<!-- Source: user-service/docs/project/source/ProjectInfrastructure.md -->
---

# InfrastructureMetric[]

metrics:

- label: "Reverse Proxy"
  value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
  icon: "nginx"
  description: "user-nginx — TLS :443, HTTP :80 → redirect; /health on :80; upstream user_backend → user-service:8080 (least_conn, Docker DNS scaling)"

- label: "Application HTTP (Compose)"
  value: "8086 (default host)"
  icon: "spring"
  description: "Mapped as ${ORDER_SERVICE_HOST_PORT:-8086}:8080 — env var name is legacy/misleading"

- label: "PostgreSQL"
  value: "15 Alpine"
  icon: "postgres"
  description: "Host port 5431→5432; DB user_db by default"

- label: "Redis"
  value: "7 Alpine"
  icon: "redis"
  description: "Host port 6378→6379"

- label: "Prometheus"
  value: "v2.54.1"
  icon: "prometheus"
  description: "Host :9090 — scrapes targets from shared Prometheus scrape config (outside this monorepo)"

- label: "Grafana"
  value: "11.1.4"
  icon: "grafana"
  description: "Host :3000; default admin/admin in compose (dev only)"

- label: "Loki"
  value: "3.1.1"
  icon: "loki"
  description: "Host :3100 for log aggregation"

# CloudService[]

cloudServices:

- name: "Amazon RDS PostgreSQL (placeholder)"
  purpose: "Managed primary database for production"
  icon: "aws"
  cost: "TBD — $/month estimate not configured"

- name: "Amazon ElastiCache Redis (placeholder)"
  purpose: "Shared cache / rate limit backing store"
  icon: "aws"
  cost: "TBD"

- name: "Amazon MSK or Confluent Kafka (placeholder)"
  purpose: "user.created / user.updated / user.deleted topics"
  icon: "kafka"
  cost: "TBD"

- name: "GCP Cloud Run / GKE (placeholder)"
  purpose: "Container hosting for user-service + Nginx Ingress"
  icon: "gcp"
  cost: "TBD"

# DeploymentLayer[]

deploymentLayers:

- name: "Edge / Ingress"
  color: "#009688"
  components:
    - name: "Nginx (Docker Compose)"
      icon: "nginx"
      description: "TLS termination, least_conn load balance to replicated user-service containers"
    - name: "Future: AWS ALB / Cloud Load Balancing (dummy)"
      icon: "cloud"
      description: "Placeholder — terminate TLS and forward to service mesh or K8s Service"

- name: "Compute"
  color: "#2196F3"
  components:
    - name: "user-service container"
      icon: "docker"
      description: "Fat JAR from multi-stage Dockerfile (Gradle bootJar)"
    - name: "Kubernetes Deployment (dummy)"
      icon: "k8s"
      description: "replicas: 3, rolling update — not present in-repo"

- name: "Data"
  color: "#795548"
  components:
    - name: "PostgreSQL"
      icon: "postgres"
      description: "Flyway migrations under src/main/resources/db/migration when profile enables Flyway"
    - name: "Redis"
      icon: "redis"
      description: "Cache + rate limiting support via libs_kernel"

- name: "Observability"
  color: "#E91E63"
  components:
    - name: "Prometheus + Grafana + Loki"
      icon: "grafana"
      description: "Local stack in docker-compose.yml; wire to prod equivalents (dummy: Datadog ID placholder-001)"

# DockerFile[]

dockerFiles:

- service: "user-service"
  description: "Multi-stage: Temurin 23 JDK builder (gradlew bootJar), Temurin 23 JRE Alpine runtime, non-root spring user, /app/logs"
  content: |
    FROM eclipse-temurin:23-jdk-noble AS builder
    WORKDIR /app
    COPY gradlew gradle settings.gradle build.gradle ./
    RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon
    COPY src src
    RUN ./gradlew bootJar --no-daemon
    FROM eclipse-temurin:23-jre-alpine
    WORKDIR /app
    RUN addgroup -S spring && adduser -S spring -G spring && apk add --no-cache wget
    COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar
    RUN mkdir -p /app/logs && chown spring:spring /app/logs
    USER spring
    ENTRYPOINT ["java", "-jar", "/app/app.jar"]

- service: "nginx"
  description: "Reverse proxy — see nginx/nginx.conf; TLS certs auto-generated by docker-entrypoint script in ssl volume"
  content: |
    image: nginx:1.27-alpine
    container_name: user-nginx
    ports: ["80:80", "443:443"]
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl

---

# Infrastructure

## Dangerous / missing in this stack

- **Grafana admin password** `admin` / `admin` in compose — **rotate or disable** outside local dev.
- **JWT_SECRET_KEY** required in compose (no default) — good; ensure secrets manager in real cloud (`dummy-secret-ref: vault://user-service/jwt`).
- **Kafka** is **required** via `SPRING_KAFKA_BOOTSTRAP_SERVERS` — service will fail startup if unset.
- **No gRPC port published** in compose for user-service; even if wired later, coordinate with Prometheus **9090** host binding.

## Raw commands (reference)

- Copy `.env.example` → `.env`, set Kafka + JWT.
- `docker compose up --build` from `user-service/` (network `drugstore_network`).

<!-- END user-service -->
