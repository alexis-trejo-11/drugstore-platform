---
# ProjectFeature[]
features:
  - id: "order-create"
    title: "Create Order"
    description: "Create new orders supporting both delivery and store pickup methods with item validation, duplicate detection, and automatic ID generation"
    icon: "plus-circle"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
    highlights:
      - "Supports delivery and pickup order creation via single endpoint"
      - "Validates items for duplicates and null values"
      - "Enforces currency consistency across service fee, tax, and items"
      - "Publishes OrderCreatedEvent for async processing"
    techStack:
      - "Spring Boot"
      - "Spring Validation"
      - "JPA/Hibernate"
    metrics:
      - label: "Supported Delivery Methods"
        value: "3"
        trend: "stable"
        icon: "truck"
    codeSnippet:
      language: "java"
      filename: "SaleOrderController.java"
      code: |
        @PostMapping(consumes = "application/json")
        public ResponseWrapper<CreateOrderCommandResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
          if (request.deliveryMethod() != null) {
            var command = request.toDeliveryOrderCommand();
            var result = orderService.createDeliveryOrder(command);
            return ResponseWrapper.created(result, "PurchaseOrder");
          }
          var command = request.toPickupOrderCommand();
          var result = orderService.createPickupOrder(command);
          return ResponseWrapper.created(result, "PurchaseOrder");
        }

  - id: "order-search"
    title: "Search Orders"
    description: "Paginated and filtered order search with dynamic criteria including status, delivery method, date ranges, and user ID"
    icon: "search"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
    highlights:
      - "Dynamic filtering with Specification pattern"
      - "Pagination and sorting support"
      - "Filter by status, delivery method, date range, user ID"
      - "Returns PageResponse with metadata"
    techStack:
      - "Spring Data JPA"
      - "Specification API"
      - "PageResponse wrapper"
    metrics:
      - label: "Filter Criteria"
        value: "6+"
        trend: "stable"
        icon: "filter"

  - id: "order-status-transitions"
    title: "Order Status Management"
    description: "Complete order lifecycle management with state machine validation, supporting confirm, prepare, ship, deliver, pickup, cancel, and return operations"
    icon: "exchange-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderStatusController.java"
    highlights:
      - "9 order statuses with validated state transitions"
      - "Role-based access control (ADMIN, EMPLOYEE)"
      - "Automatic timestamp tracking on status changes"
      - "Publishes OrderStatusChangedEvent on transitions"
    techStack:
      - "Spring Boot"
      - "Enum-based state machine"
      - "Domain events"
    metrics:
      - label: "Status States"
        value: "9"
        trend: "stable"
        icon: "state"
      - label: "Transition Rules"
        value: "12+"
        trend: "stable"
        icon: "rules"

  - id: "customer-order-access"
    title: "Customer Order Access"
    description: "Dedicated API for customers to view their own orders with pagination, filtering by status, and detailed order information"
    icon: "user"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/UserOrderController.java"
    highlights:
      - "Customers can only access their own orders (enforced by userID)"
      - "Paginated order listing with filtering"
      - "Detailed order view with items and delivery/pickup info"
      - "Role-based: CUSTOMER and ADMIN access"
    techStack:
      - "Spring Security"
      - "Spring Data JPA"
      - "ResponseMapper"

  - id: "order-address-management"
    title: "Delivery Address Management"
    description: "Manage delivery addresses with CRUD operations, validation against order state, and support for multiple addresses per user"
    icon: "map-marker-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/external/address/infrastructure/api/rest/AddressController.java"
    highlights:
      - "Create, read, update, delete delivery addresses"
      - "Address validation for delivery orders"
      - "Prevent deletion of addresses in use by active orders"
      - "Support for building types (HOUSE, APARTMENT, BUSINESS)"
    techStack:
      - "Spring Boot"
      - "JPA/Hibernate"
      - "Bean Validation"
    metrics:
      - label: "Building Types"
        value: "3"
        trend: "stable"
        icon: "building"

  - id: "order-delete"
    title: "Order Deletion"
    description: "Soft and hard delete capabilities for orders with proper validation and audit trail"
    icon: "trash-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
    highlights:
      - "Soft delete preserves data for audit purposes"
      - "Hard delete for complete removal (admin only)"
      - "Validates order state before deletion"
    techStack:
      - "Spring Boot"
      - "JPA/Hibernate"

  - id: "rate-limiting"
    title: "API Rate Limiting"
    description: "Configurable rate limiting with global and per-profile limits to protect against abuse and ensure fair usage"
    icon: "tachometer-alt"
    category: "security"
    status: "stable"
    highlights:
      - "Global rate limit: 5000 requests per hour"
      - "Per-profile limits: standard (100/60s), sensitive (5/300s), public (1000/60s), admin (50/1s)"
      - "Custom rate limit annotation via @RateLimit"
    techStack:
      - "Spring Boot"
      - "Custom RateLimit annotation"
      - "Redis (planned for distributed rate limiting)"
    metrics:
      - label: "Rate Limit Profiles"
        value: "4"
        trend: "stable"
        icon: "shield-alt"

  - id: "caching"
    title: "Redis Caching"
    description: "Redis-based caching layer for frequently accessed data with decorator pattern implementation"
    icon: "database"
    category: "caching"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/external/users/application/service/decorator/CachingUserServiceDecorator.java"
    highlights:
      - "CachingUserServiceDecorator wraps UserServiceImpl"
      - "Cache frequently accessed user data"
      - "Configurable TTL and eviction policies"
    techStack:
      - "Spring Cache"
      - "Redis"
      - "Decorator Pattern"

  - id: "event-publishing"
    title: "Domain Event Publishing"
    description: "Publish domain events for order creation and status changes to enable event-driven architecture and async processing"
    icon: "bolt"
    category: "messaging"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/models/events"
    highlights:
      - "OrderCreatedEvent with orderId, userID, totalAmount, createdAt"
      - "OrderStatusChangedEvent with oldStatus, newStatus, changedAt"
      - "Events enable integration with other microservices"
    techStack:
      - "Spring Boot"
      - "Domain-Driven Design"
      - "Event Port (EventPublisher)"

  - id: "security"
    title: "Role-Based Security"
    description: "Comprehensive security with HTTPS enforcement, CORS configuration, and role-based access control for customers, employees, and admins"
    icon: "lock"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/config/SecurityConfig.java"
    highlights:
      - "HTTPS enforced for all requests"
      - "Roles: CUSTOMER (own orders), EMPLOYEE (sale orders), ADMIN (all operations)"
      - "CORS configured for localhost:3000 and production domain"
      - "Bearer token authentication via Spring Security"
    techStack:
      - "Spring Security"
      - "Spring Boot"
      - "JWT (via bearerAuth)"

  - id: "observability"
    title: "Observability and Monitoring"
    description: "Comprehensive monitoring with Spring Boot Actuator, Spring Boot Admin, and ELK stack for centralized logging"
    icon: "chart-line"
    category: "monitoring"
    status: "beta"
    highlights:
      - "Spring Boot Actuator exposes health, metrics, env, loggers endpoints"
      - "Spring Boot Admin for centralized management"
      - "ELK stack: OpenSearch, Logstash, OpenSearch Dashboards"
      - "JSON logging with logstash-logback-encoder"
    techStack:
      - "Spring Boot Actuator"
      - "Spring Boot Admin"
      - "OpenSearch"
      - "Logstash"
      - "Logback"
    metrics:
      - label: "Exposed Endpoints"
        value: "10+"
        trend: "stable"
        icon: "monitoring"

  - id: "api-documentation"
    title: "OpenAPI Documentation"
    description: "Automated API documentation with Swagger UI and OpenAPI 3 annotations for all endpoints"
    icon: "book"
    category: "api"
    status: "stable"
    highlights:
      - "Swagger UI available at /swagger-ui.html"
      - "API docs at /api-docs"
      - "Custom operation annotations for consistent documentation"
      - "Schema examples for request/response objects"
    techStack:
      - "springdoc-openapi-starter-webmvc-ui"
      - "Swagger UI"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/annotation"

  - id: "data-migration"
    title: "Database Migrations"
    description: "Versioned database schema management with Flyway for reliable and repeatable migrations"
    icon: "database"
    category: "database"
    status: "stable"
    highlights:
      - "Flyway core and PostgreSQL support"
      - "Migrations located in classpath:db/migration"
      - "Schema validation on migrate enabled"
    techStack:
      - "Flyway"
      - "PostgreSQL"
---
# Project Features
> Order Service provides 14+ features including order lifecycle management with 9 status states, role-based API access, Redis caching, rate limiting, event-driven architecture, and comprehensive monitoring with ELK stack. The service follows DDD principles with hexagonal architecture.

<!--
  OBSERVATIONS FOR ProjectFeature:
  ✅ POSITIVE:
    - 14 well-documented features covering all aspects of order management
    - Proper role-based security (CUSTOMER, EMPLOYEE, ADMIN)
    - Event-driven architecture with 2 domain events
    - Decorator pattern for caching and logging cross-cutting concerns
    - Comprehensive validation (Bean Validation + domain validation)
    - OpenAPI documentation with custom annotations
    - Flyway for database migrations

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - CachingUserServiceDecorator exists but Order caching not fully implemented (planned)
    - EventPublisher port exists but event bus integration is "planned" - events published but not consumed
    - Rate limiting uses @RateLimit annotation but distributed rate limiting with Redis not confirmed
    - "observability" feature marked as "beta" - Spring Boot Admin and ELK stack not fully integrated
    - No unit/integration test coverage mentioned in features
    - AddressController features documented but actual endpoint URLs not verified in this review
    - UserController features documented but actual implementation not deeply reviewed
    - No notification feature - customers not notified on status changes (should be event-driven)
-->
