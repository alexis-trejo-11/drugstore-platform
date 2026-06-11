---
# ArchitectureLayer[]
layers:
  - name: "Inbound Adapters (Web/API)"
    description: "REST controllers, request DTOs, and API annotations."
    color: "#1976D2"
    expanded: true
    components:
      - "ProductController"
      - "Request/Response DTO mappers"
    responsibilities:
      - "Translate HTTP contracts to use-case commands/queries"
      - "Validate input shape"
      - "Return standardized response wrappers"
    technologies:
      - "Spring Web"
      - "Jakarta Validation"

  - name: "Application/Core Use Cases"
    description: "Business orchestration layer that composes command and query use cases."
    color: "#388E3C"
    expanded: true
    components:
      - "JoinedProductUseCases"
      - "Create/Update/Delete/Restore/Search/Get use cases"
      - "Caching decorators"
    responsibilities:
      - "Execute catalog business workflows"
      - "Apply read/write cache behavior"
      - "Coordinate event publication"
    technologies:
      - "Java"
      - "Spring Core"
      - "Spring Cache"

  - name: "Outbound Adapters"
    description: "Persistence and messaging adapters that implement output ports."
    color: "#F57C00"
    expanded: true
    components:
      - "ProductRepositoryImpl + ProductJpaRepository"
      - "Kafka product event publisher"
      - "Redis cache manager config"
    responsibilities:
      - "Persist and query product aggregates"
      - "Publish lifecycle events"
      - "Provide cache infrastructure"
    technologies:
      - "Spring Data JPA"
      - "PostgreSQL"
      - "Spring Kafka"
      - "Redis"

# DesignPattern[]
designPatterns:
  - title: "Ports and Adapters (Hexagonal)"
    emoji: "🔌"
    description: "Core depends on ports while adapters implement infrastructure concerns."
    category: "architecture"
    badge: "core-first"
    githubExampleUrl: "src/main/java/io/github/alexisTrejo11/drugstore/products/core/port"
  - title: "Decorator for Caching"
    emoji: "🧩"
    description: "Cache wrappers augment command/query use-cases without changing controller contracts."
    category: "performance"
    badge: "cross-cutting"
    githubExampleUrl: "src/main/java/io/github/alexisTrejo11/drugstore/products/core/application/usecase/decorator"
  - title: "Soft Delete"
    emoji: "🗃️"
    description: "Entity uses deleted_at and restore operation to preserve historical records."
    category: "data-lifecycle"
    badge: "audit-friendly"
    githubExampleUrl: "src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/out/persistence/ProductModel.java"

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Horizontal service scaling"
    description: "Compose supports `--scale product-service=n` and nginx upstream load balancing."
  - title: "Read path caching"
    description: "Most frequent lookups (id/sku/barcode/search) are cached."
  - title: "Decoupled event integration"
    description: "Kafka topics isolate downstream consumers from request latency."

# StrategyItem[] - Security
securityStrategies:
  - title: "JWT stateless auth"
    description: "JWT filter validates token on protected routes."
  - title: "Role-based write access"
    description: "POST/PUT/PATCH/DELETE restricted to ADMIN or MANAGER."
  - title: "Rate limiting by profile"
    description: "Public and sensitive operations can have different request thresholds."

# CacheStrategy[]
cacheStrategies:
  - name: "productById"
    description: "Fast product retrieval by UUID."
    ttl: "10m"
    coverage: "GET /api/v2/products/{id}"
  - name: "productBySKU"
    description: "Fast lookup by normalized SKU."
    ttl: "10m"
    coverage: "GET /api/v2/products/sku/{sku}"
  - name: "productByBarcode"
    description: "Fast lookup by barcode."
    ttl: "10m"
    coverage: "GET /api/v2/products/barcode/{barcode}"
  - name: "productSearch"
    description: "Short-lived cache for paginated search criteria."
    ttl: "5m"
    coverage: "GET /api/v2/products"

# ArchitectureFeature[]
architectureFeatures:
  - title: "Clean separation between API, use-cases, and adapters"
    emoji: "🏗️"
    description: "Package layout keeps transport/infrastructure concerns out of core logic."
  - title: "Composable observability stack"
    emoji: "📈"
    description: "Prometheus, Loki, and Grafana included in local compose stack."
  - title: "Secure-by-default write API"
    emoji: "🔐"
    description: "Explicit role checks and deny-all fallback in security chain."

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    - type: "client"
      label: "Client"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Product Service"
      color: "#2196F3"
      icon: "spring"
  nodes:
    - id: "client"
      label: "Client"
      type: "client"
      x: 100
      y: 80
      connections: ["nginx"]
      status: "healthy"
      traffic: 100
    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 280
      y: 80
      connections: ["product-service"]
      status: "healthy"
      traffic: 95
    - id: "product-service"
      label: "Product Service"
      type: "service"
      x: 480
      y: 80
      connections: []
      status: "healthy"
      traffic: 90
  connections:
    - id: "c1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "c2"
      from: "nginx"
      to: "product-service"
      label: "HTTP :8080 (internal)"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client → Nginx"
      description: "product-nginx terminates TLS; forwards HTTP to product-service (upstream product_backend)"
      icon: "nginx"
  eventFlow:
    - number: 1
      title: "Product Service → Kafka"
      description: "Create/update/delete operations publish product lifecycle events for downstream services."
      icon: "kafka"

# TechDecisionsModel
techDecisions:
  decisions:
    - title: "Use decorator instead of cache annotations on controllers"
      problem: "Caching concerns should not leak into transport adapters."
      solution: "Applied cache wrappers around ProductQueryUseCases and ProductCommandUseCases."
      outcome: "Core API contracts stay stable while caching remains replaceable."
      icon: "layers"
      alternatives:
        - "Use @Cacheable annotations directly on controller/service methods"
    - title: "Soft delete with restore endpoint"
      problem: "Need recoverable deletion and auditability for product records."
      solution: "Use deleted_at column + SQLDelete + restore flow."
      outcome: "Operational safety for accidental deletes."
      icon: "database"
      alternatives:
        - "Hard delete records permanently"
---
# Architecture

- Docker Compose adds **product-nginx** for HTTPS clients; Spring listens on HTTP **8080** behind it.

## Notes

- There is a package naming mismatch between `ratelimit` and `rate_limit` modules that can confuse contributors.
- Audit metadata appears to report the wrong service name (`address-service`) in runtime logs.

