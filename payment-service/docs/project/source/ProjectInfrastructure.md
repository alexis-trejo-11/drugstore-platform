---
# InfrastructureMetric[]
metrics:
  - label: "Service Port" value: "8080 (compose)" icon: "server" description: "HTTP inside Docker network; host publishes 8085:8080 for direct access"
  - label: "Reverse Proxy" value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
  - label: "Database" value: "PostgreSQL" icon: "database" description: "Relational database for payment and sale persistence with Flyway migrations"
  - label: "Cache" value: "Redis" icon: "cache" description: "In-memory data store for caching frequently accessed payment data"
  - label: "Message Broker" value: "Kafka (Dependency Only)" icon: "queue" description: "Kafka dependency in build.gradle but no implementation found"
  - label: "Payment Gateway" value: "Stripe (Planned)" icon: "payment" description: "StripeGatewayAdapter is a STUB - not implemented"
  - label: "Monitoring" value: "Spring Boot Admin 3.0.0" icon: "monitoring" description: "Application monitoring and management client"

# CloudService[]
cloudServices:
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
deploymentLayers:
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
      - name: "PaymentController" icon: "controller" description: "REST endpoints for payment lifecycle (initiate, refund, query)"
      - name: "SaleController" icon: "controller" description: "REST endpoints for sale queries (read-only from API)"
      - name: "StripeWebhookController" icon: "controller" description: "Webhook receiver for Stripe events with signature verification"
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
      - name: "PaymentApplicationService" icon: "service" description: "Unified interface for payment and sale operations"
      - name: "PaymentApplicationServiceImpl" icon: "service" description: "Orchestrates payment lifecycle and sale creation (if exists)"
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
      - name: "Payment" icon: "domain" description: "Aggregate root with lifecycle: PENDING → PROCESSING → COMPLETED → REFUNDED"
      - name: "Sale" icon: "domain" description: "Aggregate root auto-generated from completed payments"
      - name: "PaymentStatus" icon: "domain" description: "Enum with 6 states and business rule validation"
      - name: "SaleStatus" icon: "domain" description: "Enum with 4 states for sale lifecycle"
      - name: "PaymentMethod" icon: "domain" description: "Enum for 6 payment methods"
      - name: "PaymentCompletedEvent" icon: "event" description: "Domain event when payment succeeds"
      - name: "PaymentFailedEvent" icon: "event" description: "Domain event when payment fails"
      - name: "Money" icon: "value" description: "Value object for monetary amounts with currency"
      - name: "PaymentGatewayRef" icon: "value" description: "Value object tracking Stripe PaymentIntent and Charge IDs"
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
      - name: "PaymentRepositoryImpl" icon: "repository" description: "JPA-based payment persistence"
      - name: "SaleRepositoryImpl" icon: "repository" description: "JPA-based sale persistence"
      - name: "JpaPaymentRepository" icon: "repository" description: "Spring Data JPA interface for PaymentEntity"
      - name: "JpaSaleRepository" icon: "repository" description: "Spring Data JPA interface for SaleEntity"
      - name: "StripeGatewayAdapter" icon: "gateway" description: "STUB - returns null/empty, not implemented!"
      - name: "SpringEventPublisherAdapter" icon: "event" description: "Spring ApplicationEventPublisher adapter"
      - name: "PaymentMapper" icon: "mapper" description: "Domain to JPA entity mapping"
      - name: "SaleMapper" icon: "mapper" description: "Domain to JPA entity mapping"
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
      - name: "Spring Boot Actuator" icon: "monitoring" description: "Health and metrics endpoints (configured but not in build.gradle)"
      - name: "Spring Boot Admin Client" icon: "monitoring" description: "Registers with admin server for centralized management"
      - name: "OpenAPI/Swagger" icon: "docs" description: "Automated API documentation at /swagger-ui.html"
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
dockerFiles:
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
---

# Infrastructure
> **Compose:** `docker-compose.yml` runs payment-service on HTTP **8080** inside the network, **Nginx** (`payment-nginx`) on host **80/443**, plus Prometheus, Loki, and Grafana. HTTPS for external callers terminates at Nginx; optional plain HTTP via host **8085**. Stripe adapter remains a stub — see codebase. PostgreSQL and Redis are expected via Spring configuration/env when not added to compose.
