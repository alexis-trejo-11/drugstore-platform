---
# ArchitectureLayer[]
layers:
  - name: "Controllers (Inbound)"
    description: "REST API layer handling HTTP requests with JWT authentication, rate limiting, and role-based access control"
    color: "#4CAF50"
    expanded: true
    components:
      - "UserAddressController - Endpoints for users to manage their own addresses"
      - "AddressAdminController - Administrative endpoints for managing all addresses"
    responsibilities:
      - "Request validation via Jakarta Validation"
      - "JWT authentication via SecurityRequirement"
      - "Rate limiting with @RateLimit annotation"
      - "Response wrapping with ResponseWrapper"
    technologies:
      - "Spring Web MVC"
      - "Spring Security"
      - "SpringDoc OpenAPI"
      - "Libs Kernel (shared JWT filter)"

  - name: "Service Layer"
    description: "Business logic orchestration with validation, mapping, and transaction management"
    color: "#2196F3"
    expanded: true
    components:
      - "AddressService - Core business logic for address CRUD operations"
      - "AddressValidator - Validation logic separation"
      - "AddressMapper - Entity-DTO mapping"
    responsibilities:
      - "Address CRUD operations with transactions"
      - "User type determination (CUSTOMER/EMPLOYEE)"
      - "Address limit validation per user type"
      - "Default address management"
      - "Soft delete implementation"
    technologies:
      - "Spring Service"
      - "Spring Transaction Management"
      - "Lombok"

  - name: "Validation Layer"
    description: "Country-specific postal code validation using Factory pattern"
    color: "#FF9800"
    expanded: false
    components:
      - "PostalCodeValidatorFactory - Factory for country-specific validators"
      - "USPostalCodeValidator - US ZIP code validation"
      - "MXPostalCodeValidator - Mexico postal code validation"
      - "CAPostalCodeValidator - Canada postal code validation"
      - "ESPostalCodeValidator - Spain postal code validation"
      - "UKPostalCodeValidator - UK postcode validation"
      - "DefaultPostalCodeValidator - Fallback for unsupported countries"
    responsibilities:
      - "Regex-based postal code format validation"
      - "Country code to validator mapping"
      - "Format description for error messages"
    technologies:
      - "Strategy Pattern"
      - "Factory Pattern"

  - name: "Data Access Layer"
    description: "JPA-based persistence with PostgreSQL and Flyway migrations"
    color: "#9C27B0"
    expanded: false
    components:
      - "AddressEntity - JPA entity with UUID ID, user_id, address fields"
      - "AddressRepository - Spring Data JPA repository"
    responsibilities:
      - "CRUD operations on addresses table"
      - "Custom queries for user-specific lookups"
      - "Default address reset operations"
      - "Soft delete support (active flag)"
    technologies:
      - "Spring Data JPA"
      - "Hibernate"
      - "PostgreSQL"
      - "Flyway Migrations"

  - name: "Configuration Layer"
    description: "Cross-cutting concerns: security, CORS, rate limiting, OpenAPI, audit logging"
    color: "#F44336"
    expanded: false
    components:
      - "SecurityConfig - JWT filter chain, role-based authorization"
      - "CORSConfig - Cross-origin resource sharing configuration"
      - "RateLimitAspect - AOP aspect for Redis rate limiting"
      - "RedisRateLimiter - Token bucket rate limiter"
      - "OpenApiConfig - Swagger/OpenAPI documentation setup"
      - "AuditLoggerConfig - Audit logging configuration"
      - "GlobalExceptionHandler - Centralized exception handling"
    responsibilities:
      - "JWT authentication filter setup"
      - "Rate limit enforcement via Redis"
      - "API documentation generation"
      - "Exception handling and error responses"
      - "CORS policy enforcement"
    technologies:
      - "Spring Security"
      - "Spring AOP"
      - "Redis"
      - "SpringDoc OpenAPI"

# DesignPattern[]
designPatterns:
  - title: "Factory Pattern"
    emoji: "🏭"
    description: "PostalCodeValidatorFactory creates country-specific validators based on ISO country code"
    category: "Creational"
    badge: "Validation"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/PostalCodeValidatorFactory.java"

  - title: "Strategy Pattern"
    emoji: "📋"
    description: "Each country has its own PostalCodeValidator implementation with isValid() and getFormatDescription() methods"
    category: "Behavioral"
    badge: "Validation"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/PostalCodeValidator.java"

  - title: "DTO Pattern"
    emoji: "📦"
    description: "Separate DTOs (Address, AddressRequest, AddressSummary) for API requests/responses with Jakarta Validation"
    category: "Structural"
    badge: "API Design"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/utils/dto/"

  - title: "Repository Pattern"
    emoji: "🗄️"
    description: "AddressRepository abstracts data access using Spring Data JPA with custom query methods"
    category: "Structural"
    badge: "Data Access"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/repository/AddressRepository.java"

  - title: "Builder Pattern"
    emoji: "🔨"
    description: "AddressEntity uses @Builder (Lombok) for fluent object creation with optional fields"
    category: "Creational"
    badge: "Entity Design"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/entity/AddressEntity.java"

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Nginx Load Balancing"
    description: "Nginx upstream block uses `least_conn` and Docker DNS expansion of the address-service hostname so `docker compose up --scale address-service=N` requires zero config changes"
  - title: "Stateless Service Design"
    description: "Service is stateless with JWT authentication, allowing horizontal scaling behind a load balancer"
  - title: "Database Connection Pooling"
    description: "Spring Data JPA with HikariCP connection pooling for efficient database connections"
  - title: "Redis for Rate Limiting"
    description: "Distributed rate limiting via Redis allows consistent enforcement across multiple service instances"
  - title: "Pagination Support"
    description: "Admin endpoints support Pageable for efficient large dataset handling"

# StrategyItem[] - Security
securityStrategies:
  - title: "JWT Authentication"
    description: "Bearer token authentication via custom JwtAuthenticationFilter from shared libs-kernel"
  - title: "Role-Based Access Control"
    description: "ADMIN role for administrative endpoints, USER role for own address management (CUSTOMER/EMPLOYEE)"
  - title: "Rate Limiting"
    description: "Redis-backed rate limiting with profiles: STANDARD (general) and SENSITIVE (create/update/delete)"
  - title: "Input Validation"
    description: "Jakarta Validation on DTOs with country-specific postal code regex validation"
  - title: "Nginx TLS Termination"
    description: "Nginx terminates TLS at the edge (self-signed certs for dev). Internal communication uses HTTPS between Nginx and address-service with `proxy_ssl_verify off` for the Docker private network"
  - title: "HTTPS/SSL"
    description: "Docker container runs on 8443 with SSL certificates (keystore.p12) for encrypted communication; port is not exposed to the host — Nginx is the only entry point"
  - title: "CORS Configuration"
    description: "Controlled CORS policy allowing specific localhost ports for development"

# CacheStrategy[]
cacheStrategies:
  - name: "Redis Rate Limit Cache"
    description: "Rate limit counters stored in Redis with TTL matching the rate limit window"
    ttl: "60s (STANDARD) / 300s (SENSITIVE)"
    coverage: "All API endpoints with @RateLimit annotation"

# ArchitectureFeature[]
architectureFeatures:
  - title: "Multi-Country Support"
    emoji: "🌍"
    description: "Supports postal code validation for US, MX, CA, ES, UK with extensible factory pattern"
  - title: "Dual Controller Architecture"
    emoji: "🔐"
    description: "Separate controllers for users (own addresses) and admins (all addresses) with proper authorization"
  - title: "Soft Delete"
    emoji: "🗑️"
    description: "Addresses are marked inactive rather than deleted, preserving referential integrity"
  - title: "Audit Logging"
    emoji: "📝"
    description: "SLF4J logging for all create/update/delete operations with user and address context"

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
      label: "Address Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    - type: "queue"
      label: "Redis"
      color: "#FF9800"
      icon: "redis"
    - type: "monitoring"
      label: "Actuator"
      color: "#F44336"
      icon: "health"

  nodes:
    - id: "client"
      label: "Frontend/User"
      type: "client"
      x: 100
      y: 100
      connections: ["nginx"]
      status: "healthy"
      traffic: 100

    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 250
      y: 100
      connections: ["address-service"]
      status: "healthy"
      traffic: 100

    - id: "address-service"
      label: "Address Service"
      type: "service"
      x: 400
      y: 100
      connections: ["postgres", "redis", "actuator"]
      status: "healthy"
      traffic: 85

    - id: "postgres"
      label: "PostgreSQL 15"
      type: "database"
      x: 250
      y: 250
      connections: []
      status: "healthy"
      traffic: 60

    - id: "redis"
      label: "Redis 7"
      type: "queue"
      x: 550
      y: 250
      connections: []
      status: "healthy"
      traffic: 40

    - id: "actuator"
      label: "Actuator/Health"
      type: "monitoring"
      x: 400
      y: 250
      connections: []
      status: "healthy"
      traffic: 10

  connections:
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn1b"
      from: "nginx"
      to: "address-service"
      label: "HTTPS (internal)"
      protocol: "HTTPS"
      isActive: true
    - id: "conn2"
      from: "address-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true
    - id: "conn3"
      from: "address-service"
      to: "redis"
      label: "Rate Limit"
      protocol: "RESP"
      isActive: true
    - id: "conn4"
      from: "address-service"
      to: "actuator"
      label: "Health Checks"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client Request → Nginx"
      description: "User sends HTTPS request to Nginx on :443. Nginx terminates TLS and load-balances to an address-service replica (least_conn)"
      icon: "nginx"
    - number: 2
      title: "JWT Authentication"
      description: "Request forwarded by Nginx to address-service. JwtAuthenticationFilter validates Bearer token and sets SecurityContext with AuthUserDetails"
      icon: "lock"
    - number: 3
      title: "Rate Limit Check"
      description: "RateLimitAspect checks Redis for rate limit compliance (STANDARD or SENSITIVE profile)"
      icon: "speed"
    - number: 4
      title: "Controller Handling"
      description: "UserAddressController or AddressAdminController processes request with role validation"
      icon: "controller"
    - number: 5
      title: "Service Layer"
      description: "AddressService orchestrates business logic: validation, mapping, and repository calls"
      icon: "gear"
    - number: 6
      title: "Data Persistence"
      description: "AddressRepository saves/retrieves data from PostgreSQL via Spring Data JPA"
      icon: "database"
    - number: 7
      title: "Response Wrapping"
      description: "ResponseWrapper.success/created/updated wraps result and returns to client"
      icon: "check"

  eventFlow:
    - number: 1
      title: "Address Created Event (Future)"
      description: "PLACEHOLDER: Publish address.created event to Kafka for user-service synchronization"
      icon: "event"
    - number: 2
      title: "Address Updated Event (Future)"
      description: "PLACEHOLDER: Publish address.updated event to notify other services of changes"
      icon: "event"
    - number: 3
      title: "Address Deleted Event (Future)"
      description: "PLACEHOLDER: Publish address.deleted event for audit and cleanup in other services"
      icon: "event"

# TechDecisionsModel
techDecisions:
  - title: "Postal Code Validation Strategy"
    problem: "Need to validate postal codes for multiple countries with different formats"
    solution: "Implemented Factory Pattern with Strategy Pattern: PostalCodeValidatorFactory creates country-specific validators"
    outcome: "Extensible design - new countries can be added by implementing PostalCodeValidator interface"
    icon: "validation"
    alternatives:
      - "Use external API (requires network, slower)"
      - "Single regex for all countries (not maintainable)"
      - "No validation (data quality issues)"

  - title: "Soft Delete vs Hard Delete"
    problem: "Users may want to restore deleted addresses; referential integrity with orders needed"
    solution: "Soft delete by setting 'active=false' on AddressEntity, with @PrePersist setting active=true"
    outcome: "Data preservation, ability to restore, maintains foreign key relationships"
    icon: "delete"
    alternatives:
      - "Hard delete with CASCADE (loses data)"
      - "Archive table (more complex)"
      - "Separate deleted_at timestamp (current approach)"

  - title: "Dual Controller Architecture"
    problem: "Need different access patterns: users manage own addresses, admins manage all"
    solution: "Separate UserAddressController (path: /api/v2/user/addresses) and AddressAdminController (path: /api/v2/addresses/admin)"
    outcome: "Clear separation of concerns, proper authorization per role, cleaner code"
    icon: "architecture"
    alternatives:
      - "Single controller with if/else for roles (less clean)"
      - "Separate microservices (overkill)"
      - "Admin endpoints in user controller with admin flag (confusing)"

  - title: "Rate Limiting Implementation"
    problem: "Need to protect API from abuse and DDoS attacks"
    solution: "Redis-backed rate limiter using token bucket algorithm, applied via AOP aspect with annotations"
    outcome: "Distributed rate limiting works across multiple service instances, configurable per endpoint"
    icon: "security"
    alternatives:
      - "In-memory rate limiting (doesn't work with multiple instances)"
      - "API Gateway rate limiting (less granular)"
      - "No rate limiting (security risk)"

  - title: "UUID vs Auto-Increment ID"
    problem: "Need unique, non-guessable IDs for addresses"
    solution: "Use UUID generation strategy (@GeneratedValue(strategy = GenerationType.UUID)) for AddressEntity.id"
    outcome: "Non-guessable IDs, no coordination needed between database instances"
    icon: "id"
    alternatives:
      - "Auto-increment (guessable, requires DB coordination)"
      - "Snowflake ID (more complex)"
      - "String UUID in app layer (less efficient)"
---
# Architecture

> Well-structured layered architecture with good separation of concerns. Potential improvements: add Kafka integration for events (address created/updated/deleted), implement caching for frequently accessed addresses (Redis), add Micrometer metrics for observability, and create Circuit Breaker for resilience.
