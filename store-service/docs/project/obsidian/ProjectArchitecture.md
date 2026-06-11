---
# ArchitectureLayer[]
layers:
  - name: "Driving adapters (Inbound)"
    description: "REST controllers under /api/v2/stores; Spring MVC validation; Springdoc/OpenAPI annotations; libs_kernel ResponseWrapper & rate-limit aspects."
    color: "#3949AB"
    expanded: true
    components:
      - "StoreCommandController"
      - "StoreQueryController"
      - "DTOs + operation annotations"
    responsibilities:
      - "Map HTTP to commands/quotes and back to standardized JSON wrappers"
      - "Enforce bean validation on payloads"
    technologies:
      - "Spring Web"
      - "springdoc-openapi"

  - name: "Application core"
    description: "Use cases orchestrate domain and ports; transactional boundaries; cache eviction on writes."
    color: "#00897B"
    expanded: true
    components:
      - "StoreCommandUseCasesImpl"
      - "StoreQueryUseCasesImpl"
    responsibilities:
      - "Store mutations and queries"
      - "@CacheEvict on commands; @Cacheable on reads"
    technologies:
      - "Spring @Transactional"

  - name: "Domain"
    description: "Store aggregate, value objects (StoreCode, StoreID…), enums, specifications for search."
    color: "#6A1B9A"
    expanded: false
    components:
      - "Store"
      - "StoreSearchCriteria"
      - "Domain exceptions"
    responsibilities:
      - "Invariants and status semantics"
      - "Reconstruction from persistence params"
    technologies:
      - "Plain Java"

  - name: "Driven adapters (Outbound)"
    description: "JPA entity + mapper + repository adapter; noop-style event publisher placeholder."
    color: "#F57C00"
    expanded: true
    components:
      - "StoreRepositoryImpl / StoreEntityMapper"
      - "StoreEventPublisherImpl (stub)"
    responsibilities:
      - "PostgreSQL persistence via Hibernate"
      - "Emit integration events — not implemented"
    technologies:
      - "Spring Data JPA"
      - "Flyway migrations"

designPatterns:
  - title: "Hexagonal / Ports & Adapters"
    emoji: "⬡"
    description: "Inbound REST and outbound JPA/Kafka-ish port implement application interfaces; keeps domain isolated."
    category: "architecture"
    badge: "ports-adapters"
    githubExampleUrl: ""

  - title: "Transactional script + domain model"
    emoji: "📜"
    description: "Use cases load aggregates, mutate domain methods, persist; aligns with pragmatic DDD."
    category: "application"
    badge: "use-case"
    githubExampleUrl: ""

scalabilityStrategies:
  - title: "Stateless replicas behind Nginx"
    description: "Docker Compose scales `store-service` (`--scale`); Redis holds shared cache; Postgres is single writer (scale read replicas externally — **not configured here**)."
  - title: "HTTP compression (docker)"
    description: "`application-docker.yml` enables gzip for JSON payloads above threshold."

securityStrategies:
  - title: "JWT bearer + stateless session"
    description: "`JwtAuthenticationFilter` (libs_kernel); CSRF disabled (API)."
  - title: "Path & method authorization"
    description: "**GET /api/v2/stores/** is permitAll(); mutating verbs require ADMIN or MANAGER** — overlaps but does not equal OpenAPI `@SecurityRequirement` on all operations (reads work without Bearer)."
  - title: "Rate limiting profiles"
    description: "`SENSITIVE`, `STANDARD`, `PUBLIC`, etc.; two PATCH routes lack `@RateLimit` (gap)."

cacheStrategies:
  - name: "stores"
    description: "Entity-style cache by store id or business code lookup."
    ttl: "30m (RedisCacheConfiguration default)"
    coverage: "getByID / getByCode query paths"
  - name: "store_searches"
    description: "Keyed by SearchStoresQuery#toString() — **cache key instability** if formatting changes."
    ttl: "30m"
    coverage: "Multi-criteria search"
  - name: "store_status"
    description: "Paged listing by StoreStatus enum."
    ttl: "30m"
    coverage: "/status/{status}"

architectureFeatures:
  - title: "Soft delete awareness"
    emoji: "🗄️"
    description: "Schema includes `deleted_at`; domain/repository behaviour should align (verify callers)."
  - title: "Audit logging hook"
    emoji: "📝"
    description: "`audit.log` in YAML + `libs_kernel` audit logger configuration present — tune excluded paths per profile."

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
      label: "Store Service"
      color: "#2196F3"
      icon: "spring"
    - type: "data"
      label: "Postgres / Redis"
      color: "#FF7043"
      icon: "database"
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
      label: "Nginx (TLS)"
      type: "gateway"
      x: 280
      y: 80
      connections: ["store-service"]
      status: "healthy"
      traffic: 95
    - id: "store-service"
      label: "store-service (8080)"
      type: "service"
      x: 480
      y: 80
      connections: ["postgres", "redis"]
      status: "healthy"
      traffic: 90
    - id: "postgres"
      label: "PostgreSQL"
      type: "data"
      x: 660
      y: 40
      connections: []
      status: "healthy"
      traffic: 60
    - id: "redis"
      label: "Redis"
      type: "data"
      x: 660
      y: 120
      connections: []
      status: "healthy"
      traffic: 55
  connections:
    - id: "c1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "c2"
      from: "nginx"
      to: "store-service"
      label: "HTTP upstream (least_conn)"
      protocol: "HTTP"
      isActive: true
    - id: "c3"
      from: "store-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true
    - id: "c4"
      from: "store-service"
      to: "redis"
      label: "Redis protocol"
      protocol: "TCP"
      isActive: true

dataFlow:
  requestFlow:
    - number: 1
      title: "Client → Nginx → Spring"
      description: "TLS terminates at store-nginx; upstream balances store-service containers on :8080."
      icon: "nginx"
    - number: 2
      title: "Security filter"
      description: "JWT parsed when present; GET store routes allowed anonymously by SecurityConfig."
      icon: "lock"
    - number: 3
      title: "Use case → DB / cache"
      description: "Reads consult Redis after miss; writes evict cache regions then commit to Postgres."
      icon: "database"
  eventFlow:
    - number: 1
      title: "Status change (intended)"
      description: "Domain emits StoreStatusChangedEvent → **StoreEventPublisherImpl logs only**; no broker produce yet."
      icon: "alert"

techDecisions:
  decisions:
    - title: "PostgreSQL + JSONB schedule"
      problem: "Weekly hours and exceptions vary per store."
      solution: "Persist `schedule_config` as JSONB with GIN index for optional query patterns."
      outcome: "Flexible schema; validate at application layer."
      icon: "calendar"
      alternatives:
        - "Normalized schedule tables (heavier migrations)"
    - title: "Redis for cache (not session)"
      problem: "Repeated search and GET by id/code."
      solution: "Spring Cache abstraction with JSON values; 30m TTL."
      outcome: "Lower DB load; **watch serialization security**."
      icon: "speed"
      alternatives:
        - "Caffeine local-only (no cross-replica consistency)"
---

# Architecture

**store-nginx** terminates TLS and forwards to **store-service** (see `docker-compose.yml`). The **application core** follows **ports & adapters**: REST controllers are driving adapters; JPA repository and the messaging adapter implement driven ports.

> [!warning] Documentation vs runtime security  
> Controllers carry `@SecurityRequirement(name = "bearerAuth")` for Swagger, but **`SecurityConfig` permits all GETs under `/api/v2/stores/**` without authentication**. Treat OpenAPI security scheme as “optional for reads” unless you align code and docs.

> [!danger] Cache key & Redis typing  
> Search cache key uses `query.toString()`. Redis `ObjectMapper` activates **default typing** — review before exposing Redis outside a trusted VPC.
