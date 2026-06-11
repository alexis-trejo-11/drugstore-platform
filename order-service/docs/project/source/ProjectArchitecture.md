---
# ArchitectureLayer[]
layers:
  - name: "API Layer (Input Adapters)"
    description: "REST controllers handling HTTP requests, input validation, and response mapping. Uses OpenAPI annotations for documentation."
    color: "#4CAF50"
    expanded: true
    components:
      - "SaleOrderController - Order CRUD operations"
      - "SaleOrderStatusController - Status transitions"
      - "UserOrderController - Customer order access"
      - "AddressController - Delivery address management"
      - "UserController - User management"
    responsibilities:
      - "Accept HTTP requests with validation"
      - "Map requests to application commands/queries"
      - "Return standardized ResponseWrapper responses"
      - "Enforce role-based security"
    technologies:
      - "Spring Web MVC"
      - "Spring Validation"
      - "OpenAPI/Swagger"
      - "Jackson"

  - name: "Application Layer"
    description: "Application services, command/query handlers, and facades that orchestrate domain logic execution"
    color: "#2196F3"
    expanded: true
    components:
      - "OrderApplicationFacade - Unified command and query interface"
      - "OrderCommandHandler - Handles create, delete, update operations"
      - "OrderStatusCommandHandler - Handles status transition commands"
      - "OrderQueryHandler - Handles query operations with pagination"
      - "UserServiceImpl - User management with decorators"
      - "AddressServiceImpl - Address management"
    responsibilities:
      - "Orchestrate domain logic execution"
      - "Handle command and query requests"
      - "Apply cross-cutting concerns (logging, caching)"
      - "Map between API and domain objects"
    technologies:
      - "Spring Boot"
      - "Lombok"
      - "Shared Kernel Library"

  - name: "Domain Layer (Core)"
    description: "Rich domain model with entities, value objects, domain events, and business rules following DDD principles"
    color: "#FF9800"
    expanded: true
    components:
      - "Order - Aggregate root with business logic"
      - "OrderStatus - Enum with state machine logic"
      - "DeliveryMethod - Enum for delivery types"
      - "OrderItem - Entity representing items in order"
      - "OrderCreatedEvent - Domain event for new orders"
      - "OrderStatusChangedEvent - Domain event for status changes"
      - "Money - Value object for monetary amounts"
      - "OrderID, UserID, ProductID, PaymentID - Strongly-typed IDs"
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
    description: "Persistence, external service integration, and technical concerns implementation"
    color: "#9C27B0"
    expanded: true
    components:
      - "OrderRepositoryImpl - JPA-based order persistence"
      - "JpaOrderRepository - Spring Data JPA interface"
      - "AddressRepositoryImpl - Address persistence"
      - "JpaAddressRepository - Address JPA interface"
      - "OrderModel, OrderItemModel - JPA entity models"
      - "OrderJpaMapper - Domain-to-JPA mapping"
      - "EventPublisher - Domain event publishing port"
    responsibilities:
      - "Implement output ports from domain"
      - "Persist domain objects"
      - "Integrate with external services"
      - "Handle technical concerns (caching, logging)"
    technologies:
      - "Spring Data JPA"
      - "Hibernate"
      - "PostgreSQL"
      - "Redis"
      - "Flyway"

  - name: "Observability Layer"
    description: "Monitoring, logging, and diagnostics infrastructure for production readiness"
    color: "#F44336"
    expanded: false
    components:
      - "Spring Boot Actuator - Health and metrics endpoints"
      - "Spring Boot Admin - Management console"
      - "Logstash Encoder - JSON log formatting"
      - "OpenSearch/Logstash/Dashboards - ELK stack"
    responsibilities:
      - "Expose health and metrics endpoints"
      - "Aggregate and visualize logs"
      - "Monitor application performance"
      - "Provide operational insights"
    technologies:
      - "Spring Boot Actuator"
      - "Spring Boot Admin"
      - "OpenSearch"
      - "Logstash"
      - "Logback"

# DesignPattern[]
designPatterns:
  - title: "Hexagonal Architecture (Ports and Adapters)"
    emoji: "🏛️"
    description: "Domain-centric architecture with ports (interfaces) defining dependencies and adapters implementing them. Separates business logic from infrastructure concerns."
    category: "Architecture"
    badge: "Core"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/ports"

  - title: "Aggregate Root (DDD)"
    emoji: "📦"
    description: "Order serves as Aggregate Root, encapsulating OrderItems and enforcing invariants. All modifications go through the Order entity."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/models/Order.java"

  - title: "Decorator Pattern"
    emoji: "🎨"
    description: "Used for cross-cutting concerns: CachingUserServiceDecorator and LoggingUserServiceDecorator wrap UserServiceImpl to add caching and logging behavior."
    category: "Structural"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/external/users/application/service/decorator"

  - title: "Facade Pattern"
    emoji: "🏠"
    description: "OrderApplicationFacade provides a unified interface combining OrderCommandService and OrderQueryService for simplified client access."
    category: "Structural"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/ports/input/OrderApplicationFacade.java"

  - title: "Builder Pattern"
    emoji: "🔨"
    description: "Order entity uses Lombok's @Builder for fluent creation. CreateOrderRequest uses records with factory methods toCommand()."
    category: "Creational"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/models/Order.java"

  - title: "Strategy Pattern (Delivery Methods)"
    emoji: "🎯"
    description: "Different delivery methods (STORE_PICKUP, EXPRESS_DELIVERY, STANDARD_DELIVERY) have method-specific validation and behavior in Order entity."
    category: "Behavioral"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/models/enums/DeliveryMethod.java"

  - title: "Specification Pattern"
    emoji: "📋"
    description: "OrderSpecifications provides reusable query criteria for searching orders with dynamic filters (status, date range, user, etc.)."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/persistence/specification/OrderSpecifications.java"

  - title: "State Pattern (Order Status)"
    emoji: "🔄"
    description: "OrderStatus enum implements state machine with canTransitionTo() method defining valid state transitions between 9 order states."
    category: "Behavioral"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/models/enums/OrderStatus.java"

  - title: "Mapper Pattern"
    emoji: "🗺️"
    description: "Separate mappers handle object mapping: OrderResponseMapper, OrderDetailResponseMapper, OrderJpaMapper, AddressResponseMapper, UserResponseMapper."
    category: "Structural"
    badge: "Helper"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/mapper"

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Redis Caching"
    description: "Redis cache for frequently accessed data (user info, addresses) reduces database load and improves response times"
  - title: "Pagination for Queries"
    description: "All list endpoints use pagination (PageResponse) to handle large datasets efficiently"
  - title: "Database Indexing"
    description: "Flyway migrations create appropriate indexes on frequently queried columns (userID, status, createdAt)"
  - title: "Stateless Services"
    description: "Service is stateless and can be horizontally scaled behind a load balancer"
  - title: "Event-Driven Architecture"
    description: "Domain events (OrderCreatedEvent, OrderStatusChangedEvent) enable async processing and service decoupling"

# StrategyItem[] - Security
securityStrategies:
  - title: "HTTPS Enforcement"
    description: "All requests require HTTPS via Spring Security requiresChannel() configuration with SSL on port 8446"
  - title: "Role-Based Access Control"
    description: "Three roles: CUSTOMER (own orders), EMPLOYEE (sale orders), ADMIN (all operations). Enforced via @SecurityRequirement and path matchers"
  - title: "Bearer Token Authentication"
    description: "JWT bearer token authentication via Spring Security (bearerAuth scheme in OpenAPI)"
  - title: "Rate Limiting"
    description: "Configurable rate limiting with global (5000/hr) and per-profile limits to prevent abuse"
  - title: "Input Validation"
    description: "Bean Validation annotations (@NotNull, @NotBlank, @Size) on all request DTOs with custom domain validation"
  - title: "CORS Configuration"
    description: "Configured CORS allowing specific origins, methods (GET, POST, PUT, PATCH, DELETE), and headers"

# CacheStrategy[]
cacheStrategies:
  - name: "User Cache"
    description: "Cache user data to avoid repeated database lookups"
    ttl: "300s"
    coverage: "UserServiceImpl with CachingUserServiceDecorator"
  - name: "Order Query Cache"
    description: "Cache frequently accessed order queries (planned enhancement)"
    ttl: "60s"
    coverage: "OrderQueryHandler (planned)"
  - name: "Address Cache"
    description: "Cache delivery addresses for repeat customers"
    ttl: "600s"
    coverage: "AddressServiceImpl (planned)"

# ArchitectureFeature[]
architectureFeatures:
  - title: "Hexagonal Architecture"
    emoji: "🏛️"
    description: "Clean separation between domain logic and infrastructure with ports and adapters"
  - title: "Rich Domain Model"
    emoji: "📦"
    description: "Order aggregate encapsulates business rules, validation, and state transitions"
  - title: "Event-Driven"
    emoji: "⚡"
    description: "Domain events enable loose coupling between services"
  - title: "Role-Based Security"
    emoji: "🔒"
    description: "Fine-grained access control with three distinct roles"
  - title: "Observability"
    emoji: "📊"
    description: "Comprehensive monitoring with Actuator, Admin, and ELK stack"
  - title: "API-First Design"
    emoji: "📝"
    description: "OpenAPI annotations and Swagger UI for interactive documentation"

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    - type: "client"
      label: "Client (Frontend/Mobile)"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx (TLS + LB)"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Order Service"
      color: "#FF9800"
      icon: "service"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    - type: "queue"
      label: "Event Bus (Planned)"
      color: "#F44336"
      icon: "queue"
    - type: "monitoring"
      label: "OpenSearch/ELK"
      color: "#607D8B"
      icon: "monitoring"
  nodes:
    - id: "client"
      label: "Client"
      type: "client"
      x: 100
      y: 50
      connections: ["nginx"]
      status: "healthy"
      traffic: 1000
    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 300
      y: 50
      connections: ["order-service"]
      status: "healthy"
      traffic: 800
    - id: "order-service"
      label: "Order Service"
      type: "service"
      x: 500
      y: 50
      connections: ["postgres", "redis", "events", "opensearch"]
      status: "healthy"
      traffic: 600
    - id: "postgres"
      label: "PostgreSQL"
      type: "database"
      x: 400
      y: 150
      connections: []
      status: "healthy"
      traffic: 400
    - id: "redis"
      label: "Redis Cache"
      type: "database"
      x: 600
      y: 150
      connections: []
      status: "healthy"
      traffic: 300
    - id: "events"
      label: "Event Bus"
      type: "queue"
      x: 500
      y: 200
      connections: ["other-services"]
      status: "healthy"
      traffic: 200
    - id: "opensearch"
      label: "OpenSearch"
      type: "monitoring"
      x: 700
      y: 100
      connections: []
      status: "healthy"
      traffic: 100
    - id: "other-services"
      label: "Other Services"
      type: "service"
      x: 500
      y: 280
      connections: []
      status: "healthy"
      traffic: 150
  connections:
    - id: "conn-1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn-2"
      from: "nginx"
      to: "order-service"
      label: "HTTP :8080 (internal)"
      protocol: "HTTP"
      isActive: true
    - id: "conn-3"
      from: "order-service"
      to: "postgres"
      label: "JDBC"
      protocol: "PostgreSQL"
      isActive: true
    - id: "conn-4"
      from: "order-service"
      to: "redis"
      label: "Redis Protocol"
      protocol: "RESP"
      isActive: true
    - id: "conn-5"
      from: "order-service"
      to: "events"
      label: "Domain Events"
      protocol: "Event"
      isActive: true
    - id: "conn-6"
      from: "events"
      to: "other-services"
      label: "Async Events"
      protocol: "Event"
      isActive: true
    - id: "conn-7"
      from: "order-service"
      to: "opensearch"
      label: "Logs"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client → Nginx"
      description: "HTTPS on :443 to Nginx; TLS terminates at edge. Nginx forwards HTTP to order-service:8080 (least_conn across replicas)"
      icon: "nginx"
    - number: 2
      title: "Security Filter"
      description: "Spring Security validates JWT token and checks role-based permissions (CUSTOMER, EMPLOYEE, ADMIN)"
      icon: "lock"
    - number: 3
      title: "Controller Handling"
      description: "Appropriate controller (SaleOrderController, UserOrderController) receives request and validates input"
      icon: "controller"
    - number: 4
      title: "Application Service"
      description: "OrderApplicationFacade routes to appropriate command or query handler"
      icon: "service"
    - number: 5
      title: "Domain Logic"
      description: "Order aggregate root executes business logic with validation and state transitions"
      icon: "domain"
    - number: 6
      title: "Persistence"
      description: "OrderRepositoryImpl persists changes via JPA to PostgreSQL, with Redis caching"
      icon: "database"
    - number: 7
      title: "Response"
      description: "ResponseWrapper with data is returned to client with appropriate HTTP status"
      icon: "response"
  eventFlow:
    - number: 1
      title: "Domain Event Creation"
      description: "Order aggregate creates OrderCreatedEvent or OrderStatusChangedEvent"
      icon: "event"
    - number: 2
      title: "Event Publishing"
      description: "EventPublisher port publishes event to event bus (to be implemented)"
      icon: "publish"
    - number: 3
      title: "Event Handling"
      description: "Other services (notification, inventory) react to events asynchronously"
      icon: "handler"
    - number: 4
      title: "Side Effects"
      description: "Notifications sent, inventory updated, analytics recorded based on events"
      icon: "effect"

# TechDecisionsModel
techDecisions:
  decisions:
    - title: "Hexagonal Architecture with DDD"
      problem: "Need to isolate business logic from infrastructure and support multiple adapters"
      solution: "Adopted hexagonal architecture with ports (interfaces) in domain layer and adapters in infrastructure layer"
      alternatives: ["Layered architecture", "Clean Architecture", "Traditional n-tier"]
      outcome: "Clean separation of concerns, testable domain logic, easy adapter swapping"
      icon: "architecture"

    - title: "Java 23 with Records and Pattern Matching"
      problem: "Need modern language features for concise, type-safe DTOs and value objects"
      solution: "Used Java 23 with records for DTOs (CreateOrderRequest, OrderResponse) and modern language features"
      alternatives: ["Java 17 (LTS)", "Kotlin", "Scala"]
      outcome: "Concise code, immutable DTOs, pattern matching for type-safe operations"
      icon: "language"

    - title: "Spring Boot 3.3.2 with Spring Cloud"
      problem: "Need a mature framework for building microservices with cloud-native features"
      solution: "Chose Spring Boot 3.3.2 with Spring Cloud 2023.0.3 for dependency management and cloud integrations"
      alternatives: ["Quarkus", "Micronaut", "Dropwizard"]
      outcome: "Rich ecosystem, easy integration with Spring Cloud services, extensive community support"
      icon: "spring"

    - title: "PostgreSQL with Flyway Migrations"
      problem: "Need reliable relational storage with versioned schema management"
      solution: "PostgreSQL 15 as primary database with Flyway for database migrations"
      alternatives: ["MySQL", "H2 (for tests only)", "MongoDB"]
      outcome: "ACID transactions, JSON support, reliable schema versioning across environments"
      icon: "database"

    - title: "Redis for Caching"
      problem: "Need to improve read performance for frequently accessed data"
      solution: "Integrated Redis with Spring Cache abstraction, using decorator pattern for transparent caching"
      alternatives: ["Ehcache (in-memory)", "Memcached", "No caching"]
      outcome: "Reduced database load, faster response times for cached data"
      icon: "cache"

    - title: "OpenSearch + Logstash + Dashboards (ELK Stack)"
      problem: "Need centralized logging and search capabilities for troubleshooting and analytics"
      solution: "Deployed OpenSearch 2.9.0 with Logstash 8.11.0 and OpenSearch Dashboards for log aggregation"
      alternatives: ["Elasticsearch (licensed)", "Splunk (commercial)", "Simple file logging"]
      outcome: "Centralized logs, powerful search, visualization dashboards for monitoring"
      icon: "logs"

    - title: "Order State Machine via Enum"
      problem: "Need to enforce valid order status transitions with clear business rules"
      solution: "OrderStatus enum with canTransitionTo() method defining all valid state transitions"
      alternatives: ["State pattern with classes", "Spring State Machine", "Database-driven state"]
      outcome: "Simple, type-safe state transitions, easy to understand and modify"
      icon: "state"

    - title: "Shared Kernel Library"
      problem: "Need common utilities (ResponseWrapper, PageResponse, RateLimit) across microservices"
      solution: "Created shared-kernel library (io.github.alexisTrejo11:shared-kernel:1.0.0) with common abstractions"
      alternatives: ["Copy-paste utilities", "Spring Boot Starters", "No shared code"]
      outcome: "Consistent patterns across services, reduced duplication, easier maintenance"
      icon: "library"
---
# Architecture
> Order Service follows hexagonal architecture with DDD principles. The domain layer contains the Order aggregate root with rich business logic. Ports define contracts; adapters implement them. Design patterns include Decorator (caching/logging), Facade (unified interface), Builder (object creation), and State (order status transitions). The service uses PostgreSQL, Redis, and OpenSearch/ELK stack.

<!--
  OBSERVATIONS FOR ProjectArchitecture:
  ✅ POSITIVE:
    - Clean hexagonal architecture with 5 well-defined layers
    - 9 design patterns identified and documented (DDD, GoF, and custom)
    - Rich domain model with aggregate root pattern
    - State machine implemented in OrderStatus enum
    - Specification pattern for dynamic queries
    - Architecture diagram with 8 nodes and 7 connections defined
    - Data flow documented for both request/response and event flows
    - 8 tech decisions documented with alternatives and outcomes

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - API Gateway shown in architecture diagram but NOT IMPLEMENTED (marked as "Planned")
    - Event Bus node in diagram but actual implementation is "planned" - EventPublisher port has no real implementation
    - "other-services" node is vague - no specific service names or connections defined
    - Architecture diagram coordinates (x,y) are approximate - may need adjustment for rendering
    - build.gradle has DUPLICATE bootRun configuration (lines 88-106 and 114-132)
    - No circuit breaker pattern implemented for resilience
    - No API versioning strategy documented beyond "/api/v2" path
    - Shared kernel library (io.github.alexistrejo11:shared-kernel:1.0.0) dependency not versioned with project
-->
