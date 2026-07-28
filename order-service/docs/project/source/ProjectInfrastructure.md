---
# InfrastructureMetric[]
metrics:
  - label: "Service Port" value: "8080 (compose)" icon: "server" description: "HTTP inside Docker network when SPRING_PROFILES_ACTIVE=docker; optional standalone SSL profile may differ"
  - label: "Reverse Proxy" value: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
  - label: "Database" value: "PostgreSQL 15" icon: "database" description: "Relational database for order persistence with Flyway migrations"
  - label: "Cache" value: "Redis 7" icon: "cache" description: "In-memory data store for caching frequently accessed order data"
  - label: "Search Engine" value: "OpenSearch 2.9.0" icon: "search" description: "Distributed search and analytics engine for log aggregation"
  - label: "Log Processor" value: "Logstash 8.11.0" icon: "logs" description: "Data processing pipeline for shipping logs to OpenSearch"
  - label: "Dashboard" value: "OpenSearch Dashboards 2.9.0" icon: "dashboard" description: "Visualization and monitoring dashboard for logs and metrics"
  - label: "Rate Limiting" value: "5000 req/hour" icon: "rate-limit" description: "Global rate limit with per-profile limits for standard, sensitive, public, and admin endpoints"

# CloudService[]
cloudServices:
  - name: "PostgreSQL" purpose: "Primary relational database for order persistence" icon: "database" cost: "TBD - Planned for cloud deployment"
  - name: "Redis" purpose: "Caching layer for improved read performance" icon: "cache" cost: "TBD - Planned for cloud deployment"
  - name: "OpenSearch" purpose: "Log aggregation and search" icon: "search" cost: "TBD - Planned for cloud deployment"
  - name: "Spring Boot Admin" purpose: "Application monitoring and management" icon: "monitoring" cost: "Free (self-hosted)"

# DeploymentLayer[]
deploymentLayers:
  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
        icon: "nginx"
        description: "TLS termination on :443, HTTP→HTTPS on :80, load-balances order-service replicas"

  - name: "API Layer"
    color: "#4CAF50"
    components:
      - name: "SaleOrderController" icon: "controller" description: "REST endpoints for order CRUD operations accessible by ADMIN and EMPLOYEE roles"
      - name: "SaleOrderStatusController" icon: "controller" description: "REST endpoints for order status transitions and workflow management"
      - name: "UserOrderController" icon: "controller" description: "REST endpoints for customer-specific order access with CUSTOMER and ADMIN role access"
      - name: "AddressController" icon: "controller" description: "REST endpoints for delivery address management"
      - name: "UserController" icon: "controller" description: "REST endpoints for user management operations"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "OrderApplicationFacade" icon: "service" description: "Unified facade implementing both command and query services"
      - name: "OrderCommandHandler" icon: "handler" description: "Handles order command operations with logging decorators"
      - name: "OrderStatusCommandHandler" icon: "handler" description: "Handles order status transition commands"
      - name: "OrderQueryHandler" icon: "handler" description: "Handles order query operations with pagination and filtering"

  - name: "Domain Layer"
    color: "#FF9800"
    components:
      - name: "Order Aggregate Root" icon: "domain" description: "Core domain entity encapsulating order state, business rules, and validation"
      - name: "OrderStatus Enum" icon: "domain" description: "State machine defining valid order status transitions"
      - name: "DeliveryMethod Enum" icon: "domain" description: "Enum for STORE_PICKUP, EXPRESS_DELIVERY, STANDARD_DELIVERY"
      - name: "OrderCreatedEvent" icon: "event" description: "Domain event published when a new order is created"
      - name: "OrderStatusChangedEvent" icon: "event" description: "Domain event published when order status changes"

  - name: "Infrastructure Layer"
    color: "#9C27B0"
    components:
      - name: "OrderRepositoryImpl" icon: "repository" description: "JPA-based order repository implementation with specification-based search"
      - name: "JpaOrderRepository" icon: "repository" description: "Spring Data JPA repository interface for OrderModel"
      - name: "UserServiceImpl" icon: "service" description: "User service with caching and logging decorators"
      - name: "AddressServiceImpl" icon: "service" description: "Address service for delivery address management"

  - name: "Observability Layer"
    color: "#F44336"
    components:
      - name: "Spring Boot Actuator" icon: "monitoring" description: "Health checks, metrics, and environment info endpoints"
      - name: "Spring Boot Admin Client" icon: "monitoring" description: "Registers with admin server for centralized management"
      - name: "Logstash Encoder" icon: "logging" description: "JSON log encoding for ELK stack integration"
      - name: "OpenAPI/Swagger" icon: "docs" description: "Automated API documentation with springdoc-openapi"

# DockerFile[]
dockerFiles:
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
---

# Infrastructure
