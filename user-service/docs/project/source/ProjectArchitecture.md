---
# ArchitectureLayer[]
layers:
  - name: "Delivery (REST)"
    description: "Controllers under users.user.adapter.input.rest and profile.infrastructure.web.rest; JWT filters from libs_kernel; rate limit annotations."
    color: "#4CAF50"
    expanded: true
    components:
      - "UserQueryManagerController"
      - "UserManagerController"
      - "ProfileController"
      - "Global exception handlers"
    responsibilities:
      - "Map HTTP to commands/queries"
      - "Return ResponseWrapper envelopes"
      - "Enforce ROLE_ADMIN / ROLE_MANAGER on /manager/**"
    technologies:
      - "Spring MVC"
      - "Spring Security"
      - "SpringDoc"

  - name: "Application"
    description: "CommandBus / QueryBus dispatch to use cases; profile use cases for read/update."
    color: "#FF9800"
    expanded: true
    components:
      - "CommandBus / QueryBus"
      - "ProfileUseCases / UpdateProfileUseCase"
    responsibilities:
      - "Orchestrate domain operations"
      - "Transaction boundaries (via adapters)"
    technologies:
      - "Plain Java + Spring DI"

  - name: "Domain"
    description: "User aggregate rules, value objects (Email, PhoneNumber, UserId), enums (UserRole, UserStatus), domain events for Kafka."
    color: "#9C27B0"
    expanded: false
    components:
      - "User entity + CreateUserParams"
      - "Profile model"
      - "UserCreatedEvent / UserUpdateEvent / UserDeletedEvent"
    responsibilities:
      - "Invariants and business vocabulary"
    technologies:
      - "Java domain module"

  - name: "Infrastructure"
    description: "JPA repositories, Kafka consumer + handlers, UserGrpcServer (no server host), Redis config, observability."
    color: "#607D8B"
    expanded: true
    components:
      - "UserJpaRepository / ProfileJpaRepository"
      - "UserEventConsumer"
      - "UserGrpcServer"
      - "KafkaConsumerConfig"
    responsibilities:
      - "Persistence and messaging I/O"
    technologies:
      - "Spring Data JPA"
      - "Spring Kafka"
      - "grpc-java (stubs)"

# DesignPattern[]
designPatterns:
  - title: "Ports and adapters (hexagonal)"
    emoji: "⬡"
    description: "Input REST + output JPA/gRPC/Kafka implement core ports (UserRepository, buses)."
    category: "architecture"
    badge: "core"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform"

  - title: "CQRS-style buses"
    emoji: "⇄"
    description: "Explicit CommandBus vs QueryBus instead of god-service injection."
    category: "application"
    badge: "pattern"
    githubExampleUrl: ""

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Stateless instances"
    description: "JWT + Redis; Compose documents scaling user-service replicas behind Nginx (Docker DNS + least_conn)."
  - title: "Paging on list endpoints"
    description: "by-role / by-status use PageRequest → PageResponse."

# StrategyItem[] - Security
securityStrategies:
  - title: "JWT Bearer"
    description: "All /api/** except documented permits require authenticated principal; claims built in libs_kernel filter."
  - title: "Role-gated mutations"
    description: "/api/v2/users/manager/** → hasAnyRole(ADMIN, MANAGER)."
  - title: "Gap — gRPC not exposed / unsecured by default"
    description: "GRPC_SECURITY_ENABLED defaults false; no TLS or mTLS wiring in-repo; server lifecycle missing."

# CacheStrategy[]
cacheStrategies:
  - name: "Spring Cache → Redis"
    description: "application.yml configures redis cache; eviction/TTL tuned per deployment."
    ttl: "deployment-specific"
    coverage: "Cache annotations where applied (verify use sites)"

# ArchitectureFeature[]
architectureFeatures:
  - title: "Event consumption"
    emoji: "📨"
    description: "Listens user.created | user.updated | user.deleted; manual ack — failure path risky without DLQ."
  - title: "Observability hooks"
    emoji: "📈"
    description: "Actuator + Prometheus registry + Loki appender."

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    - type: "client"
      label: "Client / SPA"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx TLS"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "user-service"
      color: "#2196F3"
      icon: "spring"
    - type: "data"
      label: "PostgreSQL / Redis"
      color: "#795548"
      icon: "database"
  nodes:
    - id: "client"
      label: "Client"
      type: "client"
      x: 80
      y: 100
      connections: ["nginx"]
      status: "healthy"
      traffic: 100
    - id: "nginx"
      label: "Nginx (:443)"
      type: "gateway"
      x: 260
      y: 100
      connections: ["user-service"]
      status: "healthy"
      traffic: 95
    - id: "user-service"
      label: "user-service (:8080)"
      type: "service"
      x: 480
      y: 100
      connections: ["postgres", "redis", "kafka"]
      status: "degraded-notes"
      traffic: 80
    - id: "postgres"
      label: "PostgreSQL"
      type: "data"
      x: 700
      y: 40
      connections: []
      status: "healthy"
      traffic: 0
    - id: "redis"
      label: "Redis"
      type: "data"
      x: 700
      y: 120
      connections: []
      status: "healthy"
      traffic: 0
    - id: "kafka"
      label: "Kafka"
      type: "data"
      x: 700
      y: 200
      connections: []
      status: "unknown"
      traffic: 0
  connections:
    - id: "c1"
      from: "client"
      to: "nginx"
      label: "HTTPS"
      protocol: "HTTPS"
      isActive: true
    - id: "c2"
      from: "nginx"
      to: "user-service"
      label: "HTTP internal"
      protocol: "HTTP"
      isActive: true
    - id: "c3"
      from: "user-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true
    - id: "c4"
      from: "user-service"
      to: "redis"
      label: "Redis protocol"
      protocol: "TCP"
      isActive: true
    - id: "c5"
      from: "user-service"
      to: "kafka"
      label: "Consumer"
      protocol: "Kafka"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client → Nginx"
      description: "TLS on 443 (self-signed certs in dev)."
      icon: "nginx"
    - number: 2
      title: "Nginx → Spring Boot"
      description: "Proxy to upstream user_backend (user-service:8080)."
      icon: "spring"
    - number: 3
      title: "Security filter"
      description: "JWT validated; ROLE_* for manager routes."
      icon: "lock"
  eventFlow:
    - number: 1
      title: "Kafka → UserEventConsumer"
      description: "JSON deserialized to domain events; handler invoked; acknowledge() on success only."
      icon: "kafka"
    - number: 2
      title: "Failure path"
      description: "**Dangerous**: no DLQ — exception leaves message unacked (potential poison retry)."
      icon: "alert"

# TechDecisionsModel
techDecisions:
  decisions:
    - title: "Shared kernel JWT"
      problem: "Consistent auth across microservices."
      solution: "Reuse libs_kernel JwtAuthenticationFilter + handlers."
      outcome: "Centralized parsing; issuer/secret must match token producer (e.g. auth-service)."
      icon: "key"
      alternatives:
        - "Spring Authorization Server embedded in user-service"
    - title: "JPA over raw SQL"
      problem: "Relational persistence with schema migrations."
      solution: "Spring Data JPA + Flyway in hardened profiles."
      outcome: "**Dev profile** disables Flyway and uses ddl-auto update — inconsistent with Docker profile intent."
      icon: "database"
      alternatives:
        - "MyBatis / jOOQ"
    - title: "gRPC stubs without server lifecycle"
      problem: "Fast inter-service queries."
      solution: "Protobuf contract + blocking stub impl class."
      outcome: "**Not production-ready** until ServerBuilder lifecycle added or spring-grpc adopted."
      icon: "warning"
      alternatives:
        - "REST internal API"
        - "spring-boot-starter-grpc"
---

# Architecture

## Critical gaps

- **gRPC server not bootstrapped**: `UserGrpcServer` never registered on a `NettyServerBuilder`; external callers cannot dial this process for RPC today.
- **Configuration cross-contamination**: `application-docker.yml` and SpringDoc scan packages still reference **store** / **products** strings — classify as defect, not stylistic drift.
- **Inter-service identity**: JWT issuer/secret coupling with whoever mints tokens; document contract (placeholder audience: `drugstore-api`).
