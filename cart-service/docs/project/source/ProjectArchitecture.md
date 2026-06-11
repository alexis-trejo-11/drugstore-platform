---
# ArchitectureLayer[]
layers:
  - name: "Controllers (Inbound Adapters)"
    description: "REST API and gRPC service for cart management with JWT authentication"
    color: "#4CAF50"
    expanded: true
    components:
      - "UserCartController - User endpoints for my-cart, update items, move to afterwards, restore from afterwards"
      - "CartManagerController - Admin endpoints for customer cart lookup, cart search, cart by ID"
      - "CartGrpcService - gRPC service for GetUserCart and ClearCart (used by order-service)"
    responsibilities:
      - "REST API request handling"
      - "gRPC service implementation"
      - "Request mapping to commands/queries"
      - "Response wrapping with ResponseWrapper"
    technologies:
      - "Spring Web MVC"
      - "gRPC (io.grpc, spring-boot-grpc)"
      - "Protobuf 3.25.1"
      - "libs-kernel (shared ResponseWrapper)"

  - name: "Application Layer (Use Cases)"
    description: "DDD Application Services coordinating domain logic and use case implementation"
    color: "#2196F3"
    expanded: true
    components:
      - "CartQueryUseCaseImpl - Query use cases: getCartById, getCartByCustomerId, searchCarts"
      - "CartCommandUseCaseImpl - Command use cases: createCart, updateCartItems, moveItemToAfterwards, removeItemFromAfterwards, clearCart"
      - "CreateCartUseCase - Creates new cart for customer"
      - "UpdateCartItemsUseCase - Updates items in cart"
      - "MoveCartItemToAfterwardsUseCase - Moves items to afterwards list"
      - "ReturnCartItemFromAfterwardsUseCase - Restores items from afterwards"
      - "ClearCartUseCase - Clears cart (with optional exclusion list)"
    responsibilities:
      - "Command/Query separation (CQS)"
      - "Coordination between domain and infrastructure"
      - "Transaction management"
    technologies:
      - "Spring Service"
      - "Domain-Driven Design"
      - "Command Pattern"

  - name: "Domain Layer"
    description: "Core business logic with Cart aggregate root, value objects, and domain events"
    color: "#FF9800"
    expanded: false
    components:
      - "Cart - Aggregate root with items and afterwardsItems"
      - "CartItem - Entity representing item in cart with quantity, price, discount"
      - "AfterwardsItem - Entity for save-for-later functionality"
      - "Value Objects: CartId, CustomerId, ProductId, Quantity, ItemPrice, CartTimeStamps"
      - "Domain Events: CartPurchasedEvent (PLACEHOLDER: not published yet)"
      - "Enums: CartStatus"
    responsibilities:
      - "Business rule enforcement (max 100 unique items)"
      - "Item add/update/remove/clear logic"
      - "Price calculation (total, subtotal, discount)"
      - "Domain event generation"
    technologies:
      - "DDD Domain Model"
      - "Aggregate Root Pattern"
      - "Value Objects Pattern"

  - name: "Output Adapters (Infrastructure)"
    description: "Adapters implementing outbound ports: persistence, messaging, external services"
    color: "#9C27B0"
    expanded: false
    components:
      - "CartJpaRepository - Spring Data JPA repository"
      - "CartRepositoryImpl - Repository implementation with specifications"
      - "CartModelMapper - Entity to JPA model mapping"
      - "ProductEventConsumer - Kafka consumer for product-events topic"
      - "ProductEventHandler - Handles product update/delete events"
      - "ProductServiceConfig - PLACEHOLDER: configuration for product-service client"
      - "Redis Cache - Spring Cache with Redis for cart lookups"
    responsibilities:
      - "JPA persistence with PostgreSQL"
      - "Kafka event consumption"
      - "Redis caching"
      - "Cart search with specifications"
    technologies:
      - "Spring Data JPA"
      - "PostgreSQL 15"
      - "Spring Kafka"
      - "Spring Data Redis"
      - "Flyway Migrations"

  - name: "Configuration Layer"
    description: "Cross-cutting concerns: security, caching, Kafka, gRPC, OpenAPI"
    color: "#F44336"
    expanded: false
    components:
      - "SecurityConfig - JWT filter chain, role-based authorization (USER, ADMIN)"
      - "RedisCacheConfig - Spring Cache + Redis configuration"
      - "RateLimitAspect - AOP aspect for rate limiting (PLACEHOLDER: not used on controllers)"
      - "RedisRateLimiter - Token bucket rate limiter"
      - "KafkaConfig - Kafka consumer configuration"
      - "ProductServiceConfig - Product service client config"
      - "OpenApiConfig - Swagger/OpenAPI documentation setup"
      - "GlobalExceptionHandler - Centralized exception handling"
      - "CartGrpcMapper - Protobuf to/from domain mapping"
    responsibilities:
      - "Security filter chain"
      - "Cache configuration"
      - "Kafka consumer setup"
      - "gRPC service configuration"
      - "Global exception handling"
    technologies:
      - "Spring Security"
      - "Spring AOP"
      - "Spring Kafka"
      - "Spring Cache"
      - "SpringDoc OpenAPI"

# DesignPattern[]
designPatterns:
  - title: "Aggregate Root Pattern"
    emoji: "🏰"
    description: "Cart is the aggregate root encapsulating items and afterwardsItems with business logic for add/update/remove/clear operations"
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/Cart.java"

  - title: "Value Object Pattern"
    emoji: "🧱"
    description: "Strongly-typed values: CartId, CustomerId, ProductId, Quantity (with validation), ItemPrice (BigDecimal wrapper), CartTimeStamps"
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/valueobjects"

  - title: "Command Pattern"
    emoji: "📋"
    description: "Command objects: CreateCartCommand, UpdateCartCommand, ClearCartCommand, CreateAfterwardsCommand, RemoveAfterwardsCommand"
    category: "Behavioral"
    badge: "CQS"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/application/command"

  - title: "Repository Pattern"
    emoji: "🗄️"
    description: "CartRepository port interface with CartJpaRepository and CartRepositoryImpl using Specification for search"
    category: "Structural"
    badge: "Persistence"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/output/persistence/repository/CartJpaRepository.java"

  - title: "Ports & Adapters"
    emoji: "🔌"
    description: "CartCommandUseCase/CartQueryUseCase ports with CartCommandUseCaseImpl/CartQueryUseCaseImpl adapters"
    category: "Architectural"
    badge: "Hexagonal"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/port"

  - title: "Factory Pattern"
    emoji: "🏭"
    description: "CartId.generate() for UUID generation, Cart.create() and Cart.reconstruct() factory methods"
    category: "Creational"
    badge: "Factory"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/Cart.java"

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Nginx Load Balancing"
    description: "Nginx upstream block uses `least_conn` and Docker DNS expansion of the cart-service hostname so `docker compose up --scale cart-service=N` requires zero config changes"
  - title: "Stateless Service Design"
    description: "Service is stateless with JWT authentication, allowing horizontal scaling behind a load balancer"
  - title: "Redis Caching"
    description: "Spring Cache with Redis for frequently accessed cart data, reducing PostgreSQL load"
  - title: "Pagination Support"
    description: "Admin search endpoints support Pageable for efficient large dataset handling"
  - title: "gRPC for Inter-Service Communication"
    description: "High-performance gRPC calls from order-service during checkout flow"

# StrategyItem[] - Security
securityStrategies:
  - title: "Nginx TLS Termination"
    description: "Nginx terminates TLS at the edge (self-signed certs for dev). Internal Docker communication uses HTTPS with `proxy_ssl_verify off` — all external traffic enters only through Nginx on port 443"
  - title: "JWT Authentication"
    description: "Bearer token authentication via shared JwtAuthenticationFilter from libs-kernel"
  - title: "Role-Based Access Control"
    description: "USER role for customer endpoints, ADMIN role for management endpoints"
  - title: "Redis Rate Limiting"
    description: "PLACEHOLDER: RedisRateLimiter exists but @RateLimit annotations not applied to controllers"
  - title: "HTTPS/SSL"
    description: "Docker container runs on 8443 with SSL certificates (keystore.p12); port is not exposed to host — Nginx is the only entrypoint"

# CacheStrategy[]
cacheStrategies:
  - name: "Redis Cart Cache"
    description: "Spring Cache with Redis for cart lookups, configured via RedisCacheConfig"
    ttl: "3600s (configurable via ADDRESS_SERVICE_CACHE_TTL)"
    coverage: "Cart queries (PLACEHOLDER: @Cacheable annotations needed)"

# ArchitectureFeature[]
architectureFeatures:
  - title: "DDD Aggregate Root"
    emoji: "🏰"
    description: "Cart aggregate with encapsulated business logic for item management (max 100 unique items)"
  - title: "Afterwards (Save-for-Later)"
    emoji: "📥"
    description: "Separate list for items saved for later with move-to and restore-from operations"
  - title: "gRPC Service Interface"
    emoji: "🔌"
    description: "Exposes GetUserCart and ClearCart for order-service integration during checkout"
  - title: "Kafka Event Consumption"
    emoji: "📢"
    description: "Listens to product-events topic to update cart items on price/availability changes"
  - title: "Domain Events"
    emoji: "📢"
    description: "CartPurchasedEvent defined but PLACEHOLDER: not published to Kafka yet"

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
      label: "Cart Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    - type: "queue"
      label: "Kafka"
      color: "#FF9800"
      icon: "kafka"
    - type: "cache"
      label: "Redis"
      color: "#F44336"
      icon: "redis"
    - type: "gateway"
      label: "Order Service (gRPC)"
      color: "#607D8B"
      icon: "grpc"
    - type: "monitoring"
      label: "Actuator/Admin"
      color: "#795548"
      icon: "health"

  nodes:
    - id: "client"
      label: "Frontend/User"
      type: "client"
      x: 100
      y: 100
      connections: ["nginx"]
      status: "healthy"
      traffic: 120

    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 250
      y: 100
      connections: ["cart-service"]
      status: "healthy"
      traffic: 120

    - id: "cart-service"
      label: "Cart Service"
      type: "service"
      x: 400
      y: 100
      connections: ["postgres", "redis", "kafka", "order-service-grpc", "actuator"]
      status: "healthy"
      traffic: 90

    - id: "postgres"
      label: "PostgreSQL 15"
      type: "database"
      x: 250
      y: 250
      connections: []
      status: "healthy"
      traffic: 50

    - id: "redis"
      label: "Redis 7"
      type: "cache"
      x: 550
      y: 250
      connections: []
      status: "healthy"
      traffic: 30

    - id: "kafka"
      label: "Kafka"
      type: "queue"
      x: 250
      y: 400
      connections: []
      status: "healthy"
      traffic: 20

    - id: "order-service-grpc"
      label: "Order Service"
      type: "gateway"
      x: 550
      y: 400
      connections: []
      status: "healthy"
      traffic: 15

    - id: "actuator"
      label: "Actuator/Admin"
      type: "monitoring"
      x: 400
      y: 250
      connections: []
      status: "healthy"
      traffic: 5

  connections:
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn1b"
      from: "nginx"
      to: "cart-service"
      label: "HTTPS (internal)"
      protocol: "HTTPS"
      isActive: true
    - id: "conn2"
      from: "cart-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true
    - id: "conn3"
      from: "cart-service"
      to: "redis"
      label: "Cache"
      protocol: "RESP"
      isActive: true
    - id: "conn4"
      from: "cart-service"
      to: "kafka"
      label: "product-events"
      protocol: "Kafka"
      isActive: true
    - id: "conn5"
      from: "order-service-grpc"
      to: "cart-service"
      label: "gRPC"
      protocol: "HTTP/2"
      isActive: true
    - id: "conn6"
      from: "cart-service"
      to: "actuator"
      label: "Health/Admin"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client Request → Nginx"
      description: "User sends HTTPS request to Nginx on :443. Nginx terminates TLS and load-balances to a cart-service replica (least_conn)"
      icon: "nginx"
    - number: 2
      title: "JWT Authentication"
      description: "Request forwarded by Nginx to cart-service. JwtAuthenticationFilter validates token and sets SecurityContext with userId"
      icon: "lock"
    - number: 3
      title: "Controller Handling"
      description: "UserCartController or CartManagerController processes request"
      icon: "controller"
    - number: 4
      title: "Use Case Execution"
      description: "CartQueryUseCaseImpl or CartCommandUseCaseImpl executes business logic"
      icon: "gear"
    - number: 5
      title: "Domain Logic"
      description: "Cart aggregate root handles business rules (max 100 items, price calculation)"
      icon: "domain"
    - number: 6
      title: "Data Persistence/Cache"
      description: "CartRepositoryImpl retrieves/saves via CartJpaRepository with PostgreSQL"
      icon: "database"
    - number: 7
      title: "Response"
      description: "CartResponse returned via CartResponseMapper"
      icon: "check"

  eventFlow:
    - number: 1
      title: "Product Event Received"
      description: "ProductEventConsumer receives ProductEvent from Kafka product-events topic"
      icon: "kafka"
    - number: 2
      title: "Product Event Handled"
      description: "ProductEventHandler updates cart items (price changes, availability)"
      icon: "event"
    - number: 3
      title: "Cart Updated"
      description: "Cart items updated in PostgreSQL with new product data"
      icon: "update"
    - number: 4
      title: "PLACEHOLDER: Cart Purchased"
      description: "CartPurchasedEvent should be published to Kafka when cart is cleared after successful order"
      icon: "event"

# TechDecisionsModel
techDecisions:
  - title: "DDD Aggregate Root for Cart"
    problem: "Need to encapsulate cart business logic and maintain invariants (max items, price calculation)"
    solution: "Cart.java as aggregate root with items and afterwardsItems, enforcing business rules within the aggregate"
    outcome: "Clean domain model, business logic encapsulated, easier to test domain logic"
    icon: "domain"
    alternatives:
      - "Anemic model with logic in services (less DDD)"
      - "Separate aggregates for Cart and CartItem (more complex)"
      - "No domain layer (harder to maintain business rules)"

  - title: "gRPC for Order-Service Integration"
    problem: "Order-service needs to access cart during checkout and clear cart after successful order"
    solution: "Implemented CartGrpcService with GetUserCart and ClearCart methods using Protobuf 3.25.1"
    outcome: "High-performance, type-safe inter-service communication via HTTP/2"
    icon: "grpc"
    alternatives:
      - "REST API call (slower, JSON overhead)"
      - "Shared database (tight coupling)"
      - "Message queue (async, not suitable for checkout flow)"

  - title: "Afterwards (Save-for-Later) Feature"
    problem: "Users want to save items for later without removing them from cart view"
    solution: "Separate AfterwardsItem list in Cart aggregate with move-to and restore-from operations"
    outcome: "Clean separation of active cart items and saved-for-later items"
    icon: "feature"
    alternatives:
      - "Single list with isAfterwards flag (less clear)"
      - "Separate aggregate for afterwards (overkill)"
      - "No save-for-later feature (reduced UX)"

  - title: "Kafka for Product Events"
    problem: "Cart items need to reflect product changes (price updates, product deleted/disabled)"
    solution: "ProductEventConsumer listens to product-events topic and ProductEventHandler updates cart items"
    outcome: "Eventually consistent cart data with latest product information"
    icon: "kafka"
    alternatives:
      - "REST call to product-service on every cart access (slower)"
      - "Shared database (tight coupling)"
      - "No product event handling (stale data in carts)"

  - title: "Value Objects for Type Safety"
    problem: "Need strongly-typed values with validation (ProductId, Quantity, ItemPrice)"
    solution: "Value objects wrapping primitive types with validation in constructors"
    outcome: "Type safety, validation at boundaries, self-documenting code"
    icon: "value"
    alternatives:
      - "Primitive types everywhere (less safe)"
      - "String for everything (no validation)"
      - "JPA entities for everything (overkill for simple values)"
---
# Architecture

> Well-structured DDD cart service with aggregate root pattern. Has 11 unit tests for domain layer. Potential issues: No @RateLimit on REST endpoints (unlike address-service), CartPurchasedEvent defined but not published to Kafka, Java 23 compatibility issues. Improvements needed: Add integration tests, Kubernetes manifests, CI/CD pipeline, publish cart events to Kafka.
