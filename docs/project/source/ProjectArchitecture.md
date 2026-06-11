---
layers:
  # address-service
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
  # auth-service
  - name: "Controllers (Inbound Adapters)"
    description: "REST API layer handling HTTP requests with rate limiting and input validation"
    color: "#4CAF50"
    expanded: true
    components:
      - "LoginController - Login, 2FA login, refresh session endpoints"
      - "RegisterController - Customer/Employee/Admin registration"
      - "PasswordAuthController - Forgot/reset/change password flows"
      - "LogoutController - Logout single session or all sessions"
      - "TwoFactorConfigController - Enable/disable 2FA, send validation codes"
    responsibilities:
      - "Request validation via Jakarta Validation (@Valid)"
      - "Rate limiting with @RateLimit annotation"
      - "DTO mapping (Request -> Command pattern)"
      - "Response wrapping with ResponseWrapper"
    technologies:
      - "Spring Web MVC"
      - "libs-kernel (shared ResponseWrapper)"
      - "Jakarta Validation"

  - name: "Application Layer (Use Cases)"
    description: "DDD Application Services coordinating domain logic and orchestrating use cases"
    color: "#2196F3"
    expanded: true
    components:
      - "UseCasesOrquestrator - Main orchestrator implementing 4 interfaces"
      - "LoginUseCase - Regular and 2FA login logic"
      - "RegisterUseCase - User registration with role assignment"
      - "PasswordUseCases - Forgot/reset/change password flows"
      - "LogoutUseCases - Single and all sessions logout"
      - "TwoFaConfigUseCases - 2FA enable/disable/send code"
      - "TokenUseCases - Refresh access tokens"
      - "ActivateAccountUseCase - Account activation via token"
    responsibilities:
      - "Command object pattern (LoginCommand, SignupCommand, etc.)"
      - "Result objects (SessionPayload, SignUpResult)"
      - "Coordination between domain and infrastructure"
      - "Event publishing to Kafka"
    technologies:
      - "Spring Service"
      - "Domain-Driven Design"
      - "Command Pattern"

  - name: "Domain Layer"
    description: "Core business logic with entities, value objects, and domain events"
    color: "#FF9800"
    expanded: false
    components:
      - "User.java - Core user entity with roles"
      - "Value Objects: Email, Password, UserId, UserRole, Token, SessionId, OAuth2Provider, ExternalIDs, PhoneNumber"
      - "Domain Events: UserRegisteredEvent, UserLoginEvent, PasswordChangedEvent, AccountActivatedEvent, TwoFactorEnabled/DisabledEvent"
      - "JWTSessions - JWT session management model"
    responsibilities:
      - "Business rule enforcement"
      - "Domain event generation"
      - "Value object validation"
    technologies:
      - "DDD Domain Model"
      - "Value Objects Pattern"

  - name: "Output Adapters (Infrastructure)"
    description: "Adapters implementing outbound ports: persistence, messaging, external services"
    color: "#9C27B0"
    expanded: false
    components:
      - "TokenManager - JWT and numeric token management (implements TokenService)"
      - "TokenFactory - Creates ACCESS, REFRESH, ACTIVATION, TWO_FA tokens"
      - "RedisSessionRepository - Refresh token session storage in Redis"
      - "RedisTokenRepository - Non-JWT token persistence"
      - "UserEventProducer - Kafka event publishing (7+ event types)"
      - "NotificationEventProducer - Password reset/activation emails"
      - "UserServiceGrpcClient - gRPC client for user-service"
      - "BCryptPasswordEncoder - Password hashing"
      - "OAuth2AuthenticationSuccessHandler - OAuth2 login handling"
    responsibilities:
      - "Token generation and validation"
      - "Session persistence in Redis"
      - "Kafka event publishing"
      - "gRPC communication with user-service"
      - "OAuth2 authentication flow"
    technologies:
      - "Spring Data Redis"
      - "Spring Kafka"
      - "gRPC/Protobuf"
      - "JJWT 0.11.5"
      - "Spring OAuth2 Client"
      - "BCrypt"

  - name: "Configuration Layer"
    description: "Cross-cutting concerns: security, rate limiting, Kafka, gRPC, CORS"
    color: "#F44336"
    expanded: false
    components:
      - "SecurityConfig - JWT filter chain, OAuth2, session management"
      - "RateLimitAspect - AOP aspect for Redis rate limiting"
      - "RedisRateLimiter - Token bucket rate limiter"
      - "KafkaProducerConfig - Kafka template configuration"
      - "KafkaTopicConfig - Topic definitions"
      - "GrpcConfig - gRPC channel setup"
      - "CORSConfig - Cross-origin resource sharing"
      - "GlobalExceptionHandler - Centralized exception handling"
      - "AuditLoggerConfig - Audit logging setup"
      - "AuthServiceApplication — @ComponentScan(io.github.alexisTrejo11.drugstore.accounts, libs_kernel JWT/rate-limit)"
      - "Integration tests — Testcontainers (Redis, Kafka) + Netty in-process UserService gRPC stub (src/test/.../integration/)"
    responsibilities:
      - "Security filter chain configuration"
      - "Rate limiting enforcement"
      - "Kafka topic management"
      - "gRPC channel configuration"
      - "Global exception handling"
    technologies:
      - "Spring Security"
      - "Spring AOP"
      - "Spring Kafka"
      - "Spring Data Redis"
      - "libs-kernel (shared JWT filter)"

# DesignPattern[]
  # cart-service
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
  # employee-service
  - name: "Controllers"
    description: "Inbound REST adapters for command and query operations"
    color: "#4CAF50"
    expanded: true
    components:
      - "EmployeeCommandController"
      - "EmployeeQueryController"
    responsibilities:
      - "Request validation"
      - "Role-based access control"
      - "Command/query dispatch"
    technologies:
      - "Spring Web MVC"
      - "Spring Security"

  - name: "Application Layer"
    description: "Use cases and business orchestration"
    color: "#2196F3"
    expanded: true
    components:
      - "Employee command handlers"
      - "Employee query handlers"
    responsibilities:
      - "Business rule orchestration"
      - "Transaction boundaries"
    technologies:
      - "Spring Service"
      - "CQS pattern"

  - name: "Reverse Proxy Layer"
    description: "Edge TLS and load balancing for containerized deployment"
    color: "#009688"
    expanded: true
    components:
      - "Nginx 1.27"
    responsibilities:
      - "HTTP to HTTPS redirect"
      - "TLS termination"
      - "least_conn routing to replicas"
    technologies:
      - "Nginx"

# DesignPattern[]
  # inventory-service
  - name: "Controllers (Inbound Adapters)"
    description: "REST API layer for inventory management, reservations, and stock movements"
    color: "#4CAF50"
    expanded: true
    components:
      - "InventoryController - Core inventory operations (get by id/product, low-stock, create)"
      - "InventoryReservationController - Stock reservation management (reserve, confirm, release, cancel)"
      - "InventoryStockMovementController - Stock movements (adjust, transfer, get movements)"
      - "InventoryBatchController - Batch management (PLACEHOLDER: check if exists)"
    responsibilities:
      - "REST API request handling"
      - "Request mapping to commands/queries"
      - "Response wrapping with ResponseWrapper from libs-kernel"
      - "Jakarta Validation on DTOs"
      - "Integration-tested via MockMvc + JWT (profile test)"
    technologies:
      - "Spring Web MVC"
      - "libs-kernel (shared ResponseWrapper, mappers)"
      - "SpringDoc OpenAPI 2.7.0"

  - name: "Application Layer (Use Cases)"
    description: "Inventory, Stock, and Alert use cases coordinating domain logic"
    color: "#2196F3"
    expanded: true
    components:
      - "InventoryService - Core inventory operations (get, create, low-stock checks)"
      - "StockMovementUseCase - Stock adjustments and transfers"
      - "ReservationUseCase - Stock reservation management"
      - "InventoryAlertUseCase - Low-stock alert management"
      - "Command/Query Objects: CreateInventoryCommand, AdjustInventoryCommand, TransferInventoryCommand, ReserveStockCommand, ConfirmReservationCommand, ReleaseReservationCommand"
    responsibilities:
      - "Command/Query coordination"
      - "Transaction management"
      - "Business rule enforcement"
    technologies:
      - "Spring Service"
      - "CQS Pattern"
      - "Domain-Driven Design"

  - name: "Domain Layer"
    description: "Core business logic with Inventory, InventoryMovement, StockReservation, InventoryAlert entities"
    color: "#FF9800"
    expanded: false
    components:
      - "Inventory - Core entity with total/available/reserved quantities, reorder levels"
      - "InventoryBatch - Batch tracking with lot numbers, expiration dates, supplier info"
      - "InventoryMovement - Audit trail for stock adjustments and transfers"
      - "StockReservation - Temporary stock reservations for order processing"
      - "InventoryAlert - Low-stock alerts with severity levels"
      - "Value Objects: InventoryId, BatchId, MovementId, ReservationId, AlertId"
      - "Enums: InventoryStatus, BatchStatus, MovementType, ReservationStatus, AlertSeverity, AlertType, AlertStatus"
    responsibilities:
      - "Business rule enforcement (reorder levels, quantity validation)"
      - "Batch expiration tracking"
      - "Reservation conflict detection"
      - "Alert generation"
    technologies:
      - "DDD Domain Model"
      - "Value Objects Pattern"
      - "Enum Types"

  - name: "Output Adapters (Infrastructure)"
    description: "Adapters implementing outbound ports: persistence, messaging, caching"
    color: "#9C27B0"
    expanded: false
    components:
      - "JpaInventoryRepository - Spring Data JPA for InventoryEntity"
      - "JpaInventoryBatchRepository - Batch management"
      - "JpaInventoryMovementRepository - Stock movement tracking"
      - "JpaStockReservationRepository - Reservation persistence"
      - "JpaInventoryAlertRepository - Alert management"
      - "InventoryJpaEntityMapper - Domain to JPA entity mapping"
      - "RabbitMQ Publisher - Inventory events to message queue (PLACEHOLDER: verify implementation)"
    responsibilities:
      - "JPA persistence with PostgreSQL"
      - "Flyway database migrations"
      - "Redis caching for inventory lookups"
      - "RabbitMQ messaging (AMQP)"
    technologies:
      - "Spring Data JPA"
      - "PostgreSQL 15"
      - "Flyway Migrations 10.17.0"
      - "Spring Data Redis"
      - "RabbitMQ/AMQP"

  - name: "Configuration Layer"
    description: "Cross-cutting concerns: caching, rate limiting, messaging, OpenAPI"
    color: "#F44336"
    expanded: false
    components:
      - "CacheConfig - Spring Cache with Redis"
      - "RateLimitAspect - AOP aspect for rate limiting (from libs-kernel)"
      - "RabbitMQ Config - AMQP configuration (PLACEHOLDER: verify)"
      - "OpenApiConfig - Swagger/OpenAPI documentation (2.7.0)"
      - "GlobalExceptionHandler - Centralized exception handling"
      - "JacksonConfig - JSON serialization config"
      - "MDCFilter - MDC logging filter"
    responsibilities:
      - "Cache configuration"
      - "Rate limiting enforcement"
      - "RabbitMQ connection management"
      - "API documentation generation"
      - "Global exception handling"
    technologies:
      - "Spring Cache"
      - "Spring AOP"
      - "Spring AMQP (RabbitMQ)"
      - "SpringDoc OpenAPI 2.7.0"
      - "Logstash Logback Encoder 7.4"

# DesignPattern[]
  # notification-service
  - name: ""
    description: ""
    color: ""
    expanded: false
    components:
      - ""
    responsibilities:
      - ""
    technologies:
      - ""

# DesignPattern[]
  # order-service
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
  # payment-service
  - name: "API Layer (Input Adapters)"
    description: "REST controllers handling HTTP requests, input validation, and response mapping. Three controllers: Payment, Sale, and StripeWebhook."
    color: "#4CAF50"
    expanded: true
    components:
      - "PaymentController - Payment lifecycle operations (initiate, refund, query)"
      - "SaleController - Sale queries (read-only from API)"
      - "StripeWebhookController - Stripe webhook receiver with signature verification"
    responsibilities:
      - "Accept HTTP requests with validation"
      - "Verify Stripe webhook signatures"
      - "Map requests to application service methods"
      - "Return standardized ResponseWrapper responses"
    technologies:
      - "Spring Web MVC"
      - "Spring Validation"
      - "OpenAPI/Swagger"
      - "Jackson"

  - name: "Application Layer"
    description: "Application services orchestrating domain logic execution and coordinating between aggregates."
    color: "#2196F3"
    expanded: true
    components:
      - "PaymentApplicationService - Interface defining payment and sale operations"
      - "PaymentApplicationServiceImpl - Implementation (if exists, NOT FOUND in scan)"
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
    description: "Two aggregate roots (Payment and Sale) with rich business logic, domain events, and value objects following DDD principles."
    color: "#FF9800"
    expanded: true
    components:
      - "Payment - Aggregate root with 6-state lifecycle"
      - "Sale - Aggregate root auto-generated from completed payments"
      - "PaymentStatus - Enum with 6 states (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED)"
      - "SaleStatus - Enum with 4 states (CONFIRMED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED)"
      - "PaymentMethod - Enum for 6 payment methods"
      - "PaymentCompletedEvent - Domain event when payment succeeds"
      - "PaymentFailedEvent - Domain event when payment fails"
      - "Money - Value object for monetary amounts"
      - "PaymentGatewayRef - Value object tracking Stripe IDs"
      - "RefundInfo - Value object for refund details"
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
    description: "Persistence, external service integration, and technical concerns implementation."
    color: "#9C27B0"
    expanded: true
    components:
      - "PaymentRepositoryImpl - JPA-based payment persistence"
      - "SaleRepositoryImpl - JPA-based sale persistence"
      - "JpaPaymentRepository - Spring Data JPA interface"
      - "JpaSaleRepository - Spring Data JPA interface"
      - "StripeGatewayAdapter - STUB implementation of StripeGatewayPort"
      - "SpringEventPublisherAdapter - Spring ApplicationEventPublisher adapter"
      - "PaymentMapper - Domain to JPA entity mapping"
      - "SaleMapper - Domain to JPA entity mapping"
    responsibilities:
      - "Implement output ports from domain"
      - "Persist domain objects"
      - "Integrate with external services (Stripe)"
      - "Publish domain events"
    technologies:
      - "Spring Data JPA"
      - "Hibernate"
      - "PostgreSQL"
      - "Redis"
      - "Stripe API (Planned)"

  - name: "Observability Layer"
    description: "Monitoring and logging infrastructure for production readiness."
    color: "#F44336"
    expanded: false
    components:
      - "Spring Boot Admin Client - Management console registration"
      - "OpenAPI/Swagger - API documentation at /v3/api-docs"
    responsibilities:
      - "Expose health and metrics endpoints (Actuator NOT in build.gradle)"
      - "Generate API documentation"
      - "Provide operational insights"
    technologies:
      - "Spring Boot Admin"
      - "SpringDoc OpenAPI"
      - "SLF4J"

# DesignPattern[]
  # product-service
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
  # store-service
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

  # user-service
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
  # address-service
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
  # auth-service
  - title: "Orchestrator Pattern"
    emoji: "🎯"
    description: "UseCasesOrquestrator coordinates all authentication use cases, implementing 4 port interfaces (AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases)"
    category: "Architectural"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/UseCasesOrquestrator.java"

  - title: "Command Pattern"
    emoji: "📋"
    description: "Each action has a Command object (LoginCommand, SignupCommand, ChangePasswordCommand) for clear intent and validation"
    category: "Behavioral"
    badge: "CQRS-like"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/command"

  - title: "Value Object Pattern"
    emoji: "🧱"
    description: "Domain values wrapped in objects: Email, Password (BCrypt hashed), UserId, UserRole (enum), Token, SessionId, PhoneNumber, OAuth2Provider, ExternalIDs"
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/domain/valueobjects"

  - title: "Factory Pattern"
    emoji: "🏭"
    description: "TokenFactory creates different token types (ACCESS, REFRESH JWTs; ACTIVATION, TWO_FA numeric) based on TokenType enum"
    category: "Creational"
    badge: "Factory"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/security/tokens/factory/TokenFactory.java"

  - title: "Domain Events"
    emoji: "📢"
    description: "Events published to Kafka: UserRegisteredEvent, UserCreatedEvent, UserUpdatedEvent, UserDeletedEvent, UserLoginEvent, PasswordChangedEvent, AccountActivatedEvent, TwoFactorEnabled/DisabledEvent"
    category: "Behavioral"
    badge: "Event-Driven"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/domain/event"

  - title: "Adapter Pattern"
    emoji: "🔌"
    description: "Output adapters implement port interfaces: TokenManager (TokenService), UserEventProducer (UserEventPublisher), UserServiceGrpcClient (UserServiceClient), RedisSessionRepository (SessionRepository)"
    category: "Structural"
    badge: "Ports & Adapters"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output"

# StrategyItem[] - Scalability
  # cart-service
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
  # employee-service
  - title: "Command Query Separation"
    emoji: "⚙️"
    description: "Separate command and query controllers/use-cases improve clarity and scaling options"
    category: "Architectural"
    badge: "CQS"
    githubExampleUrl: ""

# StrategyItem[] - Scalability
  # inventory-service
  - title: "CQS Pattern"
    emoji: "📋"
    description: "Separate command (write) and query (read) operations with dedicated use cases and request objects"
    category: "Architectural"
    badge: "CQS"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/inbound/api/rest/controller/InventoryController.java"

  - title: "Value Object Pattern"
    emoji: "🧱"
    description: "InventoryId, BatchId, MovementId, ReservationId, AlertId as strongly-typed value objects"
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/core/inventory/domain/entity/valueobject"

  - title: "Repository Pattern"
    emoji: "🗄️"
    description: "JPA repositories for Inventory, Batch, Movement, Reservation, Alert entities"
    category: "Structural"
    badge: "Persistence"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/repository/jpa"

  - title: "Entity Pattern with Auditing"
    emoji: "📦"
    description: "JPA entities with createdAt, updatedAt fields using @PrePersist/@PreUpdate"
    category: "Persistence"
    badge: "JPA"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/model/InventoryEntity.java"

  - title: "Reservation Pattern"
    emoji: "🔒"
    description: "StockReservation entity prevents overselling by reserving stock during order processing"
    category: "Business Logic"
    badge: "Inventory"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/core/stock/domain/entity/StockReservation.java"

  - title: "Movement Tracking Pattern"
    emoji: "📈"
    description: "InventoryMovementEntity tracks all stock adjustments and transfers for audit trail"
    category: "Audit"
    badge: "Tracking"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/model/InventoryMovementEntity.java"

# StrategyItem[] - Scalability
  # notification-service
  - title: ""
    emoji: ""
    description: ""
    category: ""
    badge: ""
    githubExampleUrl: ""

# StrategyItem[] - Scalability
  # order-service
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
  # payment-service
  - title: "Dual Aggregate Roots (DDD)"
    emoji: "📦"
    description: "Payment and Sale are separate aggregate roots. Sale is auto-generated from completed Payment, representing confirmed business transactions."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model"

  - title: "Factory Method"
    emoji: "🔨"
    description: "Payment.create() and Sale.fromPayment() factory methods encapsulate object creation with validation."
    category: "Creational"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/Payment.java"

  - title: "State Machine (Enum-based)"
    emoji: "🔄"
    description: "PaymentStatus (6 states) and SaleStatus (4 states) enums define valid state transitions with business rule validation."
    category: "Behavioral"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/enums"

  - title: "Port-Adapter Pattern"
    emoji: "🏛️"
    description: "StripeGatewayPort (port) and StripeGatewayAdapter (adapter) separate domain from infrastructure. Easy to swap implementations."
    category: "Architecture"
    badge: "Hexagonal"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/output/StripeGatewayAdapter.java"

  - title: "Domain Events"
    emoji: "⚡"
    description: "PaymentCompletedEvent and PaymentFailedEvent enable loose coupling. SpringEventPublisherAdapter for in-process events."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/events"

  - title: "Value Object"
    emoji: "🎯"
    description: "Strongly-typed value objects (PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef) provide type safety."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/valueobjects"

  - title: "Reconstruct Pattern"
    emoji: "🔄"
    description: "Payment.reconstruct() and Sale.reconstruct() factory methods for rebuilding objects from persistence (JPA entities)."
    category: "Creational"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model"

  - title: "Domain Validation"
    emoji: "✅"
    description: "DomainValidation utility class for validating domain invariants (requireNonNull, requireNonBlank, requirePositive)."
    category: "Domain"
    badge: "Helper"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/validation/DomainValidation.java"

# StrategyItem[] - Scalability
  # product-service
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
  # store-service
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

  # user-service
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
  # address-service
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
  # auth-service
  - title: "Nginx Load Balancing"
    description: "Nginx upstream block uses `least_conn` and Docker DNS expansion of the auth-service hostname so `docker compose up --scale auth-service=N` requires zero config changes"
  - title: "Stateless JWT Authentication"
    description: "Access tokens are stateless JWTs, allowing horizontal scaling without session replication"
  - title: "Redis for Sessions"
    description: "Refresh token sessions stored in Redis for distributed session management across service instances"
  - title: "Kafka Event Publishing"
    description: "Asynchronous event publishing to Kafka for loosely coupled inter-service communication"
  - title: "gRPC for User Service"
    description: "High-performance gRPC communication with user-service using Protobuf serialization"

# StrategyItem[] - Security
  # cart-service
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
  # employee-service
  - title: "Nginx Horizontal Scaling"
    description: "Nginx upstream with least_conn allows docker-compose scaling without endpoint changes"
  - title: "Stateless Service Nodes"
    description: "Employee instances remain stateless and can be replicated behind Nginx"

# StrategyItem[] - Security
  # inventory-service
  - title: "Redis Caching"
    description: "Spring Cache with Redis for frequently accessed inventory data"
  - title: "Pagination Support"
    description: "Endpoints support Pageable for efficient large dataset handling"
  - title: "Batch Processing"
    description: "Inventory batches allow granular stock tracking and expiration management"

# StrategyItem[] - Security
  # notification-service
  - title: ""
    description: ""

# StrategyItem[] - Security
  # order-service
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
  # payment-service
  - title: "Redis Caching"
    description: "Redis in build.gradle for caching frequently accessed payment/sale data (not yet implemented in code)"
  - title: "Stateless Services"
    description: "Service is stateless and can be horizontally scaled behind a load balancer"
  - title: "Database Indexing"
    description: "Flyway migrations create appropriate indexes on frequently queried columns (paymentId, orderId, customerId)"
  - title: "Event-Driven Architecture"
    description: "Domain events enable async processing. Spring Events used now, extensible for Kafka (dependency already in build.gradle)"

# StrategyItem[] - Security
  # product-service
  - title: "Horizontal service scaling"
    description: "Compose supports `--scale product-service=n` and nginx upstream load balancing."
  - title: "Read path caching"
    description: "Most frequent lookups (id/sku/barcode/search) are cached."
  - title: "Decoupled event integration"
    description: "Kafka topics isolate downstream consumers from request latency."

# StrategyItem[] - Security
  # store-service
  - title: "Stateless replicas behind Nginx"
    description: "Docker Compose scales `store-service` (`--scale`); Redis holds shared cache; Postgres is single writer (scale read replicas externally — **not configured here**)."
  - title: "HTTP compression (docker)"
    description: "`application-docker.yml` enables gzip for JSON payloads above threshold."

  # user-service
  - title: "Stateless instances"
    description: "JWT + Redis; Compose documents scaling user-service replicas behind Nginx (Docker DNS + least_conn)."
  - title: "Paging on list endpoints"
    description: "by-role / by-status use PageRequest → PageResponse."

# StrategyItem[] - Security
securityStrategies:
  # address-service
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
  # auth-service
  - title: "JWT Access + Refresh Tokens"
    description: "Short-lived access tokens (15min) with long-lived refresh tokens (7 days) for secure session management"
  - title: "Two-Factor Authentication (TOTP)"
    description: "Time-based One-Time Password support via TokenFactory generating 6-digit codes with 5min expiration"
  - title: "OAuth2 Social Login"
    description: "Support for external OAuth2 providers (Google, GitHub, etc.) via Spring OAuth2 Client"
  - title: "BCrypt Password Encoding"
    description: "Passwords hashed with BCrypt adaptive one-way function via BCryptPasswordEncoder"
  - title: "Redis Rate Limiting"
    description: "Distributed rate limiting via Redis with SENSITIVE profile (10req/min) for auth endpoints"
  - title: "Nginx TLS Termination"
    description: "Nginx terminates TLS at the edge (self-signed certs for dev). Internal Docker communication uses HTTPS with `proxy_ssl_verify off` — all external traffic enters only through Nginx on port 443"
  - title: "Token Blacklisting"
    description: "RedisSessionRepository supports session blacklisting for immediate token revocation"

# CacheStrategy[]
  # cart-service
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
  # employee-service
  - title: "Edge TLS Termination"
    description: "Nginx provides HTTPS at :443 and redirects HTTP :80 to HTTPS"
  - title: "JWT + Role Authorization"
    description: "Service enforces token and role checks for employee endpoints"

# CacheStrategy[]
  # inventory-service
  - title: "PLACEHOLDER: JWT Authentication"
    description: "PLACEHOLDER: Security config not visible in scanned files - assumed JWT filter from libs-kernel"
  - title: "Rate Limiting"
    description: "GlobalRateLimitFilter and RateLimitAspect from libs-kernel for API protection"
  - title: "Input Validation"
    description: "Jakarta Validation on all request DTOs"

# CacheStrategy[]
  # notification-service
  - title: ""
    description: ""

# CacheStrategy[]
  # order-service
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
  # payment-service
  - title: "Stripe Webhook Signature Verification"
    description: "StripeWebhookVerifier validates Stripe-Signature header before processing webhook events"
  - title: "Environment Variables for Secrets"
    description: "Stripe API key and webhook secret loaded from environment variables (STRIPE_API_KEY, STRIPE_WEBHOOK_SECRET)"
  - title: "Security Placeholder"
    description: "⚠️ NO SecurityConfig found - controllers have 'Security placeholder' comments but no JWT/auth configured"
  - title: "Input Validation"
    description: "Bean Validation annotations (@Valid, @NotBlank) on all request DTOs"
  - title: "Webhook Endpoint Exclusion"
    description: "Stripe webhook endpoint should be excluded from JWT auth in SecurityFilterChain (documented in comments)"

# CacheStrategy[]
  # product-service
  - title: "JWT stateless auth"
    description: "JWT filter validates token on protected routes."
  - title: "Role-based write access"
    description: "POST/PUT/PATCH/DELETE restricted to ADMIN or MANAGER."
  - title: "Rate limiting by profile"
    description: "Public and sensitive operations can have different request thresholds."

# CacheStrategy[]
  # store-service
  - title: "JWT bearer + stateless session"
    description: "`JwtAuthenticationFilter` (libs_kernel); CSRF disabled (API)."
  - title: "Path & method authorization"
    description: "**GET /api/v2/stores/** is permitAll(); mutating verbs require ADMIN or MANAGER** — overlaps but does not equal OpenAPI `@SecurityRequirement` on all operations (reads work without Bearer)."
  - title: "Rate limiting profiles"
    description: "`SENSITIVE`, `STANDARD`, `PUBLIC`, etc.; two PATCH routes lack `@RateLimit` (gap)."

  # user-service
  - title: "JWT Bearer"
    description: "All /api/** except documented permits require authenticated principal; claims built in libs_kernel filter."
  - title: "Role-gated mutations"
    description: "/api/v2/users/manager/** → hasAnyRole(ADMIN, MANAGER)."
  - title: "Gap — gRPC not exposed / unsecured by default"
    description: "GRPC_SECURITY_ENABLED defaults false; no TLS or mTLS wiring in-repo; server lifecycle missing."

# CacheStrategy[]
cacheStrategies:
  # address-service
  - name: "Redis Rate Limit Cache"
    description: "Rate limit counters stored in Redis with TTL matching the rate limit window"
    ttl: "60s (STANDARD) / 300s (SENSITIVE)"
    coverage: "All API endpoints with @RateLimit annotation"

# ArchitectureFeature[]
  # auth-service
  - name: "Redis Session Cache"
    description: "Refresh token sessions stored in Redis with configurable TTL matching refresh token expiration"
    ttl: "7 days (configurable via jwt.refresh-expiration-seconds)"
    coverage: "All refresh token sessions"
  - name: "Redis Rate Limit Cache"
    description: "Rate limit counters in Redis with 1-minute TTL for token bucket algorithm"
    ttl: "1 minute"
    coverage: "All endpoints with @RateLimit annotation"

# ArchitectureFeature[]
  # cart-service
  - name: "Redis Cart Cache"
    description: "Spring Cache with Redis for cart lookups, configured via RedisCacheConfig"
    ttl: "3600s (configurable via ADDRESS_SERVICE_CACHE_TTL)"
    coverage: "Cart queries (PLACEHOLDER: @Cacheable annotations needed)"

# ArchitectureFeature[]
  # employee-service
  - name: "Redis Backed Rate Limit/Cache"
    description: "Redis supports cross-cutting control and caching behavior"
    ttl: "Config-driven"
    coverage: "Employee API traffic patterns"

# ArchitectureFeature[]
  # inventory-service
  - name: "Redis Inventory Cache"
    description: "Spring Cache with Redis for inventory lookups"
    ttl: "PLACEHOLDER: Check CacheConfig for TTL"
    coverage: "Inventory queries (PLACEHOLDER: @Cacheable annotations needed)"
  - name: "Redis Rate Limit Cache"
    description: "Rate limit counters in Redis with configurable profiles"
    ttl: "60s (default)"
    coverage: "All endpoints with rate limiting"

# ArchitectureFeature[]
  # notification-service
  - name: ""
    description: ""
    ttl: ""
    coverage: ""

# ArchitectureFeature[]
  # order-service
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
  # payment-service
  - name: "Payment Cache"
    description: "Planned Redis cache for frequently accessed payment data"
    ttl: "300s (planned)"
    coverage: "PaymentApplicationService (planned)"
  - name: "Sale Cache"
    description: "Planned Redis cache for sale queries"
    ttl: "600s (planned)"
    coverage: "SaleController (planned)"

# ArchitectureFeature[]
  # product-service
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
  # store-service
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

  # user-service
  - name: "Spring Cache → Redis"
    description: "application.yml configures redis cache; eviction/TTL tuned per deployment."
    ttl: "deployment-specific"
    coverage: "Cache annotations where applied (verify use sites)"

# ArchitectureFeature[]
architectureFeatures:
  # address-service
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
  # auth-service
  - title: "DDD Bounded Context"
    emoji: "🏰"
    description: "Authentication as a separate bounded context with clear domain model and ports/adapters architecture"
  - title: "Multi-Role Support"
    emoji: "👥"
    description: "Supports CUSTOMER, EMPLOYEE, and ADMIN roles with role-specific registration endpoints"
  - title: "Token Type Flexibility"
    emoji: "🎫"
    description: "Four token types: ACCESS (JWT), REFRESH (JWT), ACTIVATION (numeric), TWO_FA (numeric) via TokenFactory"
  - title: "Event-Driven Architecture"
    emoji: "⚡"
    description: "Publishes 8+ event types to Kafka for asynchronous inter-service communication"
  - title: "Integration test stack"
    emoji: "🧪"
    description: "AuthEndpointsIntegrationTest with Spring Boot, Testcontainers Redis/Kafka, profiles integration-test+test, and InMemoryUserGrpcServer for full HTTP flows without mocking core adapters"

# ArchitectureDiagramModel
  # cart-service
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
  # employee-service
  - title: "Nginx Front Door"
    emoji: "🚪"
    description: "Single ingress for TLS and load balancing"
  - title: "Command/Query Split"
    emoji: "🧩"
    description: "Separated controller responsibilities for write/read paths"

# ArchitectureDiagramModel
  # inventory-service
  - title: "Batch Tracking with Expiration"
    emoji: "📦"
    description: "InventoryBatchEntity tracks lot numbers, manufacturing/expiration dates, supplier info"
  - title: "Stock Reservation System"
    emoji: "🔒"
    description: "Prevents overselling by reserving stock during order processing with automatic release"
  - title: "Movement Audit Trail"
    emoji: "📈"
    description: "All stock adjustments and transfers tracked via InventoryMovementEntity"
  - title: "Low-Stock Alerts"
    emoji: "⚠️"
    description: "InventoryAlertEntity generates alerts when stock falls below reorder level"
  - title: "RabbitMQ Messaging"
    emoji: "🐇"
    description: "Publishes inventory events to RabbitMQ (inconsistency: other services use Kafka)"

# ArchitectureDiagramModel
  # notification-service
  - title: "Platform ingress / TLS"
    emoji: "🔒"
    description: "No bundled Nginx in this repo — terminate HTTPS at Kubernetes Ingress, cloud LB, or reuse sibling services’ nginx/ pattern"

# ArchitectureDiagramModel
  # order-service
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
  # payment-service
  - title: "Dual Aggregate Roots"
    emoji: "📦"
    description: "Payment and Sale aggregates with separate lifecycles and state machines"
  - title: "Domain Events"
    emoji: "⚡"
    description: "PaymentCompletedEvent and PaymentFailedEvent for loose coupling"
  - title: "Port-Adapter Architecture"
    emoji: "🏛️"
    description: "Clean separation between domain logic and infrastructure with StripeGatewayPort"
  - title: "Strongly-Typed IDs"
    emoji: "🎯"
    description: "PaymentID, OrderID, CustomerID value objects prevent ID confusion"
  - title: "Factory Methods"
    emoji: "🔨"
    description: "create(), fromPayment(), reconstruct() methods encapsulate object creation"
  - title: "Value Objects"
    emoji: "💎"
    description: "Money, PaymentGatewayRef, RefundInfo encapsulate domain concepts"

# ArchitectureDiagramModel
  # product-service
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
  # store-service
  - title: "Soft delete awareness"
    emoji: "🗄️"
    description: "Schema includes `deleted_at`; domain/repository behaviour should align (verify callers)."
  - title: "Audit logging hook"
    emoji: "📝"
    description: "`audit.log` in YAML + `libs_kernel` audit logger configuration present — tune excluded paths per profile."

  # user-service
  - title: "Event consumption"
    emoji: "📨"
    description: "Listens user.created | user.updated | user.deleted; manual ack — failure path risky without DLQ."
  - title: "Observability hooks"
    emoji: "📈"
    description: "Actuator + Prometheus registry + Loki appender."

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    # address-service
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

    # auth-service
    - type: "client"
      label: "Client"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Auth Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "Redis"
      color: "#9C27B0"
      icon: "redis"
    - type: "queue"
      label: "Kafka"
      color: "#FF9800"
      icon: "kafka"
    - type: "gateway"
      label: "User Service (gRPC)"
      color: "#F44336"
      icon: "grpc"
    - type: "monitoring"
      label: "Actuator"
      color: "#607D8B"
      icon: "health"

    # cart-service
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

    # employee-service
    - type: "client"
      label: "Client"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Employee Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    # inventory-service
    - type: "client"
      label: "Client"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Inventory Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    - type: "queue"
      label: "RabbitMQ"
      color: "#FF9800"
      icon: "rabbitmq"
    - type: "cache"
      label: "Redis"
      color: "#F44336"
      icon: "redis"
    - type: "monitoring"
      label: "Actuator"
      color: "#607D8B"
      icon: "health"

    # notification-service
    - type: ""
      label: ""
      color: ""
      icon: ""
    # order-service
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
    # payment-service
    - type: "client"
      label: "Frontend (Stripe.js)"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx (Docker edge)"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Payment Service"
      color: "#FF9800"
      icon: "service"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    - type: "queue"
      label: "Stripe"
      color: "#F44336"
      icon: "payment"
    - type: "monitoring"
      label: "Spring Boot Admin"
      color: "#607D8B"
      icon: "monitoring"
    # product-service
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
    # store-service
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
    # user-service
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
    # address-service
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

    # auth-service
    - id: "client"
      label: "Frontend/Client"
      type: "client"
      x: 100
      y: 100
      connections: ["nginx"]
      status: "healthy"
      traffic: 150

    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 250
      y: 100
      connections: ["auth-service"]
      status: "healthy"
      traffic: 150

    - id: "auth-service"
      label: "Auth Service"
      type: "service"
      x: 400
      y: 100
      connections: ["redis", "kafka", "user-service-grpc", "actuator"]
      status: "healthy"
      traffic: 120

    - id: "redis"
      label: "Redis 7"
      type: "database"
      x: 250
      y: 250
      connections: []
      status: "healthy"
      traffic: 80

    - id: "kafka"
      label: "Kafka"
      type: "queue"
      x: 550
      y: 250
      connections: []
      status: "healthy"
      traffic: 50

    - id: "user-service-grpc"
      label: "User Service"
      type: "gateway"
      x: 400
      y: 250
      connections: []
      status: "healthy"
      traffic: 40

    - id: "actuator"
      label: "Actuator/Health"
      type: "monitoring"
      x: 400
      y: 400
      connections: []
      status: "healthy"
      traffic: 10

    # cart-service
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

    # employee-service
    - id: "client"
      label: "Frontend/Admin"
      type: "client"
      x: 100
      y: 100
      connections:
        - "nginx"
      status: "healthy"
      traffic: 100
    - id: "nginx"
      label: "Nginx TLS + LB"
      type: "gateway"
      x: 260
      y: 100
      connections:
        - "employee-service"
      status: "healthy"
      traffic: 100
    - id: "employee-service"
      label: "Employee Service"
      type: "service"
      x: 430
      y: 100
      connections:
        - "postgres"
      status: "healthy"
      traffic: 80
    - id: "postgres"
      label: "PostgreSQL"
      type: "database"
      x: 430
      y: 250
      connections: []
      status: "healthy"
      traffic: 45
    # inventory-service
    - id: "client"
      label: "Frontend/Order Service"
      type: "client"
      x: 100
      y: 100
      connections: ["nginx"]
      status: "healthy"
      traffic: 130

    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 280
      y: 100
      connections: ["inventory-service"]
      status: "healthy"
      traffic: 120

    - id: "inventory-service"
      label: "Inventory Service"
      type: "service"
      x: 480
      y: 100
      connections: ["postgres", "rabbitmq", "redis", "actuator"]
      status: "healthy"
      traffic: 100

    - id: "postgres"
      label: "PostgreSQL 15"
      type: "database"
      x: 250
      y: 250
      connections: []
      status: "healthy"
      traffic: 70

    - id: "rabbitmq"
      label: "RabbitMQ"
      type: "queue"
      x: 550
      y: 250
      connections: []
      status: "healthy"
      traffic: 40

    - id: "redis"
      label: "Redis"
      type: "cache"
      x: 400
      y: 250
      connections: []
      status: "healthy"
      traffic: 30

    - id: "actuator"
      label: "Actuator"
      type: "monitoring"
      x: 400
      y: 400
      connections: []
      status: "healthy"
      traffic: 5

    # notification-service
    - id: ""
      label: ""
      type: "service"
      x: 0
      y: 0
      connections:
        - ""
      status: "healthy"
      traffic: 0
    # order-service
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
    # payment-service
    - id: "frontend"
      label: "Frontend"
      type: "client"
      x: 100
      y: 50
      connections: ["nginx", "stripe"]
      status: "healthy"
      traffic: 1000
    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 280
      y: 50
      connections: ["payment-service"]
      status: "healthy"
      traffic: 900
    - id: "payment-service"
      label: "Payment Service"
      type: "service"
      x: 500
      y: 50
      connections: ["postgres", "stripe-api", "events"]
      status: "healthy"
      traffic: 800
    - id: "postgres"
      label: "PostgreSQL"
      type: "database"
      x: 300
      y: 150
      connections: []
      status: "healthy"
      traffic: 400
    - id: "stripe"
      label: "Stripe"
      type: "queue"
      x: 700
      y: 50
      connections: ["payment-service"]
      status: "healthy"
      traffic: 600
    - id: "stripe-api"
      label: "Stripe API"
      type: "queue"
      x: 700
      y: 120
      connections: []
      status: "warning"
      traffic: 300
    - id: "events"
      label: "Domain Events"
      type: "monitoring"
      x: 500
      y: 150
      connections: ["other-services"]
      status: "healthy"
      traffic: 200
    - id: "other-services"
      label: "Other Services"
      type: "service"
      x: 500
      y: 250
      connections: []
      status: "healthy"
      traffic: 150
    # product-service
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
    # store-service
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
    # user-service
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
    # address-service
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
    # auth-service
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn1b"
      from: "nginx"
      to: "auth-service"
      label: "HTTPS (internal)"
      protocol: "HTTPS"
      isActive: true
    - id: "conn2"
      from: "auth-service"
      to: "redis"
      label: "Sessions + Rate Limit"
      protocol: "RESP"
      isActive: true
    - id: "conn3"
      from: "auth-service"
      to: "kafka"
      label: "Events"
      protocol: "Kafka"
      isActive: true
    - id: "conn4"
      from: "auth-service"
      to: "user-service-grpc"
      label: "gRPC/Protobuf"
      protocol: "HTTP/2"
      isActive: true
    - id: "conn5"
      from: "auth-service"
      to: "actuator"
      label: "Health Checks"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
    # cart-service
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
    # employee-service
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn2"
      from: "nginx"
      to: "employee-service"
      label: "HTTP :8081 (internal)"
      protocol: "HTTP"
      isActive: true
    - id: "conn3"
      from: "employee-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true

# DataFlowModel
    # inventory-service
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn1b"
      from: "nginx"
      to: "inventory-service"
      label: "HTTP :8080 (internal)"
      protocol: "HTTP"
      isActive: true
    - id: "conn2"
      from: "inventory-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true
    - id: "conn3"
      from: "inventory-service"
      to: "rabbitmq"
      label: "AMQP"
      protocol: "AMQP"
      isActive: true
    - id: "conn4"
      from: "inventory-service"
      to: "redis"
      label: "Cache/Rate Limit"
      protocol: "RESP"
      isActive: true
    - id: "conn5"
      from: "inventory-service"
      to: "actuator"
      label: "Health Checks"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
    # notification-service
    - id: ""
      from: ""
      to: ""
      label: ""
      protocol: ""
      isActive: true

# DataFlowModel
    # order-service
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
    # payment-service
    - id: "conn-1"
      from: "frontend"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn-1b"
      from: "nginx"
      to: "payment-service"
      label: "HTTP :8080 (internal)"
      protocol: "HTTP"
      isActive: true
    - id: "conn-2"
      from: "frontend"
      to: "stripe"
      label: "Stripe.js"
      protocol: "HTTPS"
      isActive: true
    - id: "conn-3"
      from: "payment-service"
      to: "postgres"
      label: "JDBC"
      protocol: "PostgreSQL"
      isActive: true
    - id: "conn-4"
      from: "payment-service"
      to: "stripe-api"
      label: "Stripe SDK"
      protocol: "HTTPS"
      isActive: false
    - id: "conn-5"
      from: "stripe"
      to: "payment-service"
      label: "Webhook"
      protocol: "HTTPS"
      isActive: true
    - id: "conn-6"
      from: "payment-service"
      to: "events"
      label: "Domain Events"
      protocol: "Spring Events"
      isActive: true
    - id: "conn-7"
      from: "events"
      to: "other-services"
      label: "Sale Creation"
      protocol: "Event"
      isActive: true

# DataFlowModel
    # product-service
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
    # store-service
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

    # user-service
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
    # address-service
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

    # auth-service
    - number: 1
      title: "Client Request → Nginx"
      description: "Client sends HTTPS request to Nginx on :443. Nginx terminates TLS and load-balances to an auth-service replica (least_conn)"
      icon: "nginx"
    - number: 2
      title: "Rate Limit Check"
      description: "Request forwarded by Nginx to auth-service. RateLimitAspect checks Redis for rate limit compliance (SENSITIVE: 10/min)"
      icon: "speed"
    - number: 3
      title: "Use Case Orchestration"
      description: "UseCasesOrquestrator delegates to LoginUseCase with LoginCommand"
      icon: "orchestrator"
    - number: 4
      title: "gRPC User Lookup"
      description: "UserServiceGrpcClient retrieves user details from user-service via gRPC"
      icon: "grpc"
    - number: 5
      title: "Token Generation"
      description: "TokenFactory creates ACCESS + REFRESH JWTs via TokenManager"
      icon: "token"
    - number: 6
      title: "Session Storage"
      description: "RedisSessionRepository stores refresh token in Redis"
      icon: "save"
    - number: 7
      title: "Event Publishing"
      description: "UserEventProducer publishes UserLoginEvent to Kafka"
      icon: "kafka"
    - number: 8
      title: "Response"
      description: "SessionResponse with access/refresh tokens returned to client"
      icon: "check"

    # cart-service
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

    # employee-service
    - number: 1
      title: "Client Request to Nginx"
      description: "External request arrives on HTTPS :443"
      icon: "nginx"
    - number: 2
      title: "Forward to Employee Service"
      description: "Nginx forwards to internal employee-service:8081"
      icon: "service"
    # inventory-service
    - number: 1
      title: "Client Request"
      description: "Order Service sends request to reserve stock at /api/v2/inventories/{id}/stock/reservations"
      icon: "send"
    - number: 2
      title: "Rate Limit Check"
      description: "GlobalRateLimitFilter checks Redis for rate limit compliance"
      icon: "speed"
    - number: 3
      title: "Controller Handling"
      description: "InventoryReservationController processes reservation request"
      icon: "controller"
    - number: 4
      title: "Use Case Execution"
      description: "ReservationUseCase reserves stock, checks availability"
      icon: "gear"
    - number: 5
      title: "Data Persistence"
      description: "StockReservation saved to PostgreSQL via JpaStockReservationRepository"
      icon: "database"
    - number: 6
      title: "Event Publishing"
      description: "Inventory event published to RabbitMQ (PLACEHOLDER: verify)"
      icon: "rabbitmq"
    - number: 7
      title: "Response"
      description: "ReservationId returned to client"
      icon: "check"

    # notification-service
    - number: 1
      title: ""
      description: ""
      icon: ""
    # order-service
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
    # payment-service
    - number: 1
      title: "Initiate Payment (via Nginx)"
      description: "Browser uses HTTPS :443 to payment-nginx; Nginx proxies HTTP to payment-service:8080. POST /api/v1/payments with orderId, customerId, amount"
      icon: "nginx"
    - number: 2
      title: "Create Payment Aggregate"
      description: "Payment.create() builds Payment in PENDING state with validation"
      icon: "domain"
    - number: 3
      title: "Create Stripe PaymentIntent"
      description: "StripeGatewayAdapter.createPaymentIntent() - ⚠️ STUB, returns null!"
      icon: "gateway"
    - number: 4
      title: "Return clientSecret"
      description: "PaymentResponse with clientSecret sent to frontend"
      icon: "response"
    - number: 5
      title: "Frontend Confirms Payment"
      description: "Frontend calls stripe.confirmPayment(clientSecret) which calls Stripe"
      icon: "stripe"
    - number: 6
      title: "Stripe Sends Webhook"
      description: "Stripe calls POST /api/v1/webhooks/stripe with event data"
      icon: "webhook"
    - number: 7
      title: "Verify & Process Webhook"
      description: "StripeWebhookVerifier validates signature, PaymentApplicationService handles event"
      icon: "security"
    - number: 8
      title: "Complete Payment & Create Sale"
      description: "Payment.complete() → PaymentCompletedEvent → Sale.fromPayment()"
      icon: "success"
    # product-service
    - number: 1
      title: "Client → Nginx"
      description: "product-nginx terminates TLS; forwards HTTP to product-service (upstream product_backend)"
      icon: "nginx"
    # store-service
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
    # user-service
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
    # address-service
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
    # auth-service
    - number: 1
      title: "User Registered"
      description: "UserRegisteredEvent → Kafka → user-registered topic → other services create user profile"
      icon: "event"
    - number: 2
      title: "User Created in User-Service"
      description: "UserCreatedEvent → Kafka → user.created topic → notification-service sends welcome email"
      icon: "event"
    - number: 3
      title: "Password Changed"
      description: "PasswordChangedEvent → Kafka → auth.password-changed topic → audit log"
      icon: "event"
    - number: 4
      title: "2FA Enabled/Disabled"
      description: "TwoFactorEnabledEvent/TwoFactorDisabledEvent → Kafka → auth.two-factor-* topics"
      icon: "event"

# TechDecisionsModel
    # cart-service
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
    # employee-service
    - number: 1
      title: "Employee Domain Events"
      description: "Service may emit/consume events via Kafka depending on integration flow"
      icon: "kafka"

# TechDecisionsModel
    # inventory-service
    - number: 1
      title: "Inventory Created"
      description: "When new inventory created, event published to RabbitMQ"
      icon: "event"
    - number: 2
      title: "Stock Adjusted"
      description: "When stock adjusted, InventoryMovement tracked, event published"
      icon: "event"
    - number: 3
      title: "Low-Stock Alert"
      description: "When stock falls below reorder level, InventoryAlert created"
      icon: "alert"

# TechDecisionsModel
    # notification-service
    - number: 1
      title: ""
      description: ""
      icon: ""

# TechDecisionsModel
    # order-service
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
    # payment-service
    - number: 1
      title: "PaymentCompletedEvent Published"
      description: "SpringEventPublisherAdapter publishes event via Spring ApplicationEventPublisher"
      icon: "event"
    - number: 2
      title: "Sale Created"
      description: "Application layer listens to event and creates Sale aggregate from completed Payment"
      icon: "sale"
    - number: 3
      title: "Other Services Notified"
      description: "Order Service can listen to event to update order status to CONFIRMED"
      icon: "notification"

# TechDecisionsModel
    # product-service
    - number: 1
      title: "Product Service → Kafka"
      description: "Create/update/delete operations publish product lifecycle events for downstream services."
      icon: "kafka"

# TechDecisionsModel
    # store-service
    - number: 1
      title: "Status change (intended)"
      description: "Domain emits StoreStatusChangedEvent → **StoreEventPublisherImpl logs only**; no broker produce yet."
      icon: "alert"

    # user-service
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
    # employee-service
    - title: "Adopt Nginx Edge Proxy"
      problem: "Need HTTPS endpoint and scalable routing without changing service internals"
      solution: "Add Nginx in docker-compose with TLS termination and least_conn upstream"
      outcome: "Stable HTTPS ingress and horizontal scaling path for employee-service"
      icon: "nginx"
      alternatives:
        - "Direct service port exposure without load balancing"
    # notification-service
    - title: ""
      problem: ""
      solution: ""
      outcome: ""
      icon: ""
      alternatives:
        - ""
    # order-service
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
    # payment-service
    - title: "Dual Aggregate Roots (Payment + Sale)"
      problem: "Need to separate payment processing lifecycle from confirmed business transaction representation"
      solution: "Payment aggregate handles payment lifecycle; Sale aggregate auto-generated from completed payments"
      alternatives: ["Single Payment entity with sale flag", "Only Payment, no Sale", "Separate microservices"]
      outcome: "Clear separation of concerns, Sale represents confirmed business transaction, easier auditing"
      icon: "domain"

    - title: "Java 23 with Records and Pattern Matching"
      problem: "Need modern language features for concise, type-safe DTOs and value objects"
      solution: "Used Java 23 with records for DTOs and modern language features"
      alternatives: ["Java 17 (LTS)", "Kotlin", "Scala"]
      outcome: "Concise code, immutable DTOs, pattern matching for type-safe operations"
      icon: "language"

    - title: "Spring Boot 3.3.2 with Spring Cloud"
      problem: "Need a mature framework for building microservices with cloud-native features"
      solution: "Chose Spring Boot 3.3.2 with Spring Cloud 2023.0.3 for dependency management"
      alternatives: ["Quarkus", "Micronaut", "Dropwizard"]
      outcome: "Rich ecosystem, easy integration with Spring Cloud services, extensive community support"
      icon: "spring"

    - title: "PostgreSQL with Flyway Migrations"
      problem: "Need reliable relational storage with versioned schema management"
      solution: "PostgreSQL as primary database with Flyway for migrations, JPA ddl-auto: validate"
      alternatives: ["MySQL", "H2 (for tests)", "MongoDB"]
      outcome: "ACID transactions, JSON support, reliable schema versioning, Hibernate only validates"
      icon: "database"

    - title: "Stripe Gateway Adapter (STUB)"
      problem: "Need to integrate with Stripe for payment processing but implementation pending"
      solution: "Created StripeGatewayAdapter implementing StripeGatewayPort - currently a STUB returning null/empty"
      alternatives: ["PayPal", "Square", "Implement later"]
      outcome: "⚠️ STUB - needs actual Stripe SDK integration, all methods return null!"
      icon: "payment"

    - title: "Spring Events for Domain Events"
      problem: "Need to publish domain events for in-process handling (Sale creation from Payment)"
      solution: "SpringEventPublisherAdapter implements PaymentEventPublisher port using Spring ApplicationEventPublisher"
      alternatives: ["Kafka (added as dependency but not implemented)", "RabbitMQ", "Direct method calls"]
      outcome: "Synchronous in-process events, easy to migrate to Kafka later (only adapter changes)"
      icon: "events"

    - title: "Strongly-Typed Value Objects"
      problem: "Need to prevent ID confusion and encapsulate monetary calculations with currency"
      solution: "Created PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef, RefundInfo value objects"
      alternatives: ["Use String/BigDecimal everywhere", "Use primitives", "Lighter approach"]
      outcome: "Type safety, encapsulates validation, prevents ID mix-ups, clearer domain model"
      icon: "value"

    - title: "Kafka Dependency (Not Implemented)"
      problem: "Future need for event streaming and async communication between microservices"
      solution: "Added spring-kafka dependency in build.gradle but NO implementation yet"
      alternatives: ["RabbitMQ", "Wait until needed", "Implement now"]
      outcome: "⚠️ Dependency added but no code - compile-time dependency with no runtime usage"
      icon: "queue"
    # product-service
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
    # store-service
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
    # user-service
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

# Project Architecture

> Auto-generated by `scripts/merge_service_sources.py`. Edit service-level `docs/project/source/*.md` files, then regenerate.

<!-- BEGIN address-service -->
<!-- Source: address-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

> Well-structured layered architecture with good separation of concerns. Potential improvements: add Kafka integration for events (address created/updated/deleted), implement caching for frequently accessed addresses (Redis), add Micrometer metrics for observability, and create Circuit Breaker for resilience.

<!-- END address-service -->

<!-- BEGIN auth-service -->
<!-- Source: auth-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

> Well-structured DDD auth service with clear bounded context. **Integration tests** exercise Redis, Kafka, and an in-process UserService implementation. Further improvements: Circuit Breaker (Resilience4j) for gRPC to user-service, Micrometer metrics, Kubernetes manifests, CI/CD, refresh token rotation with reuse detection.

<!-- END auth-service -->

<!-- BEGIN cart-service -->
<!-- Source: cart-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

> Well-structured DDD cart service with aggregate root pattern. Has 11 unit tests for domain layer. Potential issues: No @RateLimit on REST endpoints (unlike address-service), CartPurchasedEvent defined but not published to Kafka, Java 23 compatibility issues. Improvements needed: Add integration tests, Kubernetes manifests, CI/CD pipeline, publish cart events to Kafka.

<!-- END cart-service -->

<!-- BEGIN employee-service -->
<!-- Source: employee-service/docs/project/source/ProjectArchitecture.md -->
# Architecture
> Employee-service now runs behind an Nginx ingress layer in Docker deployments. Nginx handles HTTPS and balances traffic to service replicas on internal port 8081.

<!-- END employee-service -->

<!-- BEGIN inventory-service -->
<!-- Source: inventory-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

## Integration testing

- **Profile:** `test` with `application-test.yml` (H2 `MODE=PostgreSQL`, Redis/Kafka excluded, rate limiting off).
- **Security slice:** Requests include real JWTs; filters validate signatures against `jwt.secret` in test YAML (`IntegrationTestJwtSupport`).
- **Entry points:** `InventoryApiIntegrationTest` (REST + MockMvc), `InventoryItemServiceImplApplicationTests` (context load).

> Well-structured inventory service with batch tracking, reservations, and movements. 
> 
> **Critical Issues & Inconsistencies:**
> - Uses RabbitMQ (AMQP) while other services use Kafka - MAJOR INCONSISTENCY
> - Dockerfile uses openjdk:17-jdk-slim (Java 17) while build.gradle specifies Java 23 - VERSION MISMATCH
> - docker-compose.yml exists with nginx + observability — reconcile Dockerfile/port/Flyway with local runtime
> - Broader **unit** test coverage beyond integration/API smoke paths is still an improvement area
> - PLACEHOLDER: Security config not scanned (JWT filter assumed)
> - PLACEHOLDER: RabbitMQ configuration needs verification
> 
> **Missing:**
> - Kubernetes manifests
> - CI/CD pipeline
> - Micrometer metrics for inventory operations
> - Circuit Breaker for external calls
> - @Cacheable annotations on repository methods

<!-- END inventory-service -->

<!-- BEGIN notification-service -->
<!-- Source: notification-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

> Edge TLS is **out of scope** for this module’s repo; pair with platform ingress when exposing HTTP endpoints.

<!-- END notification-service -->

<!-- BEGIN order-service -->
<!-- Source: order-service/docs/project/source/ProjectArchitecture.md -->
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

<!-- END order-service -->

<!-- BEGIN payment-service -->
<!-- Source: payment-service/docs/project/source/ProjectArchitecture.md -->
# Architecture
> Payment Service follows hexagonal architecture with DDD principles. Two aggregate roots (Payment and Sale) with rich business logic. Ports define contracts; adapters implement them. Design patterns include Factory Method, State Machine (enum-based), Port-Adapter, Domain Events, and Value Object. StripeGatewayAdapter is a STUB - needs implementation. Kafka dependency added but not implemented.

<!--
  OBSERVATIONS FOR ProjectArchitecture:
  ✅ POSITIVE:
    - Clean hexagonal architecture with 5 well-defined layers
    - 8 design patterns identified and documented (DDD, GoF, and custom)
    - Dual aggregate roots with separate state machines
    - State machines implemented in PaymentStatus (6 states) and SaleStatus (4 states) enums
    - Domain events (PaymentCompletedEvent, PaymentFailedEvent) for loose coupling
    - Architecture diagram with 8 nodes and 7 connections defined
    - Data flow documented for both request/response and event flows
    - 8 tech decisions documented with alternatives and outcomes
    - Strongly-typed value objects for type safety
    - Factory methods (create, fromPayment, reconstruct) for object creation

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - StripeGatewayAdapter IS A STUB - all methods return null or empty strings!
    - PaymentApplicationServiceImpl NOT FOUND - only interface exists!
    - Kafka dependency in build.gradle but NO Kafka code implemented
    - NO SecurityConfig - "Security placeholder" in controllers, no JWT/auth configured
    - API Gateway shown in architecture diagram but NOT IMPLEMENTED (marked as "Planned")
    - Stripe API connection (conn-4) marked as INACTIVE - adapter is a stub!
    - Spring Boot Actuator NOT in build.gradle dependencies (only Admin Client)
    - No circuit breaker pattern implemented for resilience
    - No API versioning strategy documented beyond "/api/v1" path
    - Shared kernel library (io.github.alexistrejo11:shared-kernel:1.0.0) dependency not versioned with project
    - SaleController says "Sales are read-only" but no enforcement at API level
    - No CORS configuration documented
    - Service runs on HTTP (port 8085) - NO SSL/HTTPS configured (unlike order-service)
    - HikariCP max-lifetime: 1200000ms (20 minutes) - verify this is correct (typo? Should be 120000?)
-->

<!-- END payment-service -->

<!-- BEGIN product-service -->
<!-- Source: product-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

- Docker Compose adds **product-nginx** for HTTPS clients; Spring listens on HTTP **8080** behind it.

## Notes

- There is a package naming mismatch between `ratelimit` and `rate_limit` modules that can confuse contributors.
- Audit metadata appears to report the wrong service name (`address-service`) in runtime logs.

<!-- END product-service -->

<!-- BEGIN store-service -->
<!-- Source: store-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

**store-nginx** terminates TLS and forwards to **store-service** (see `docker-compose.yml`). The **application core** follows **ports & adapters**: REST controllers are driving adapters; JPA repository and the messaging adapter implement driven ports.

> [!warning] Documentation vs runtime security  
> Controllers carry `@SecurityRequirement(name = "bearerAuth")` for Swagger, but **`SecurityConfig` permits all GETs under `/api/v2/stores/**` without authentication**. Treat OpenAPI security scheme as “optional for reads” unless you align code and docs.

> [!danger] Cache key & Redis typing  
> Search cache key uses `query.toString()`. Redis `ObjectMapper` activates **default typing** — review before exposing Redis outside a trusted VPC.

<!-- END store-service -->

<!-- BEGIN user-service -->
<!-- Source: user-service/docs/project/source/ProjectArchitecture.md -->
# Architecture

## Critical gaps

- **gRPC server not bootstrapped**: `UserGrpcServer` never registered on a `NettyServerBuilder`; external callers cannot dial this process for RPC today.
- **Configuration cross-contamination**: `application-docker.yml` and SpringDoc scan packages still reference **store** / **products** strings — classify as defect, not stylistic drift.
- **Inter-service identity**: JWT issuer/secret coupling with whoever mints tokens; document contract (placeholder audience: `drugstore-api`).

<!-- END user-service -->
