---
problemStatement:
  problemTitle: "Drugstore Platform Service Problem Statements"
  problemDescription: |-
    Combined problem descriptions from all Drugstore Platform services.
    
    # address-service
    E-commerce platforms require robust address management supporting multiple countries with different postal code formats, role-based access (customers vs employees), default address handling, and limits per user type. Without a dedicated service, address logic couples to user management and lacks validation.
    
    # auth-service
    In a microservices e-commerce platform, authentication must be centralized to avoid duplication across services. Requirements include JWT token management, refresh token rotation, two-factor authentication, OAuth2 social login, password reset flows, and publishing auth events to other services via Kafka.
    
    # cart-service
    In a microservices e-commerce platform, shopping cart functionality must be isolated from other services while providing real-time cart management, product availability checks, save-for-later (afterwards) feature, and efficient inter-service communication via gRPC.
    
    # employee-service
    A drugstore pharmacy needs comprehensive employee management including multiple roles (pharmacists, technicians, managers), employment types (full-time, part-time, contractor), status tracking (active, on-leave, suspended, terminated), certifications tracking, compensation management, and workday schedule storage.
    
    # inventory-service
    A drugstore pharmacy needs comprehensive inventory management including batch tracking (lot numbers, expiration dates), stock reservations for order processing, inventory movements (adjustments, transfers between warehouses), low-stock alerts, and event publishing for inventory changes.
    
    # order-service
    A drugstore platform needs a robust order management system that can handle multiple delivery methods (store pickup, express delivery, standard delivery), complex state transitions, item validation, and integration with external services like user management and address validation. The system must enforce business rules, handle events for asynchronous processing, and provide comprehensive API for both customers and administrators.
    
    # payment-service
    A drugstore platform needs a reliable payment processing system that can handle multiple payment methods, integrate with external payment gateways (Stripe), manage the complete payment lifecycle including refunds, and automatically generate sales records. The system must handle webhooks from payment providers, maintain idempotency, and provide traceability between payments and orders.
    
    # product-service
    Drugstore platform needs a single source of truth for product data with validation, fast reads, and cross-service event propagation.
    
    # store-service
    Product and order services need a single source of truth for store identity, address, schedule, geolocation, and operational status across channels.
    
    # user-service
    Order, cart, and store services need a single source of truth for user records, credentials validation, lifecycle (activate/ban/delete), and customer profile data without duplicating persistence rules.
  problemList:
    # address-service
    - "No centralized address validation for multiple countries (US, MX, CA, ES, UK postal codes)"
    - "Lack of role-based address limits (customers can have multiple, employees limited to one)"
    - "No default address management mechanism"
    - "Missing rate limiting and security for address operations"
    - "Address data scattered across services without proper abstraction"

    # auth-service
    - "No centralized auth service across microservices"
    - "JWT access + refresh token management needed"
    - "Two-factor authentication (TOTP) for enhanced security"
    - "OAuth2 social login (Google, GitHub, etc.)"
    - "Password reset flow with email tokens"
    - "Kafka event publishing for user lifecycle events"
    - "gRPC communication with user-service for user data"

    # cart-service
    - "Need isolated cart aggregate with business logic encapsulation"
    - "Support for cart items with quantity management"
    - "Save-for-later (afterwards) feature for items not ready for checkout"
    - "gRPC endpoints for order-service to access carts during checkout"
    - "Kafka event consumption for product updates (price changes, availability)"
    - "Redis caching for frequently accessed cart data"
    - "Pagination and search capabilities for admin management"

    # employee-service
    - "Need to manage diverse employee roles with specific permissions"
    - "Track employment status changes (active → suspended → terminated)"
    - "Store complex workday schedules per employee"
    - "Manage employee certifications (expirations, renewals)"
    - "Track compensation (hourly rate, weekly hours)"
    - "Soft delete support for employee termination"
    - "Audit fields for created/updated/deleted tracking"

    # inventory-service
    - "Track inventory per product with total/available/reserved quantities"
    - "Manage inventory batches with lot numbers, manufacturing/expiration dates"
    - "Support stock reservations for pending orders (prevent overselling)"
    - "Track inventory movements (adjustments, transfers) for audit trail"
    - "Generate low-stock alerts based on reorder levels"
    - "Publish inventory events to message queue for other services"
    - "Warehouse location tracking for multi-location support"

    # notification-service
    - ""

    # order-service
    - "Managing complex order state transitions with validation rules"
    - "Supporting multiple delivery methods with different business logic"
    - "Handling order items with prescription drug validation"
    - "Integrating with external user and address services"
    - "Providing role-based API access for customers, employees, and admins"
    - "Implementing event-driven architecture for order lifecycle events"
    - "Caching frequently accessed data for performance"

    # payment-service
    - "Managing complex payment state transitions (PENDING → PROCESSING → COMPLETED → REFUNDED)"
    - "Integrating with external payment gateway (Stripe) for PaymentIntents and refunds"
    - "Handling asynchronous webhook events from Stripe with signature verification"
    - "Managing two aggregate roots (Payment and Sale) with different lifecycles"
    - "Supporting multiple payment methods (Credit Card, Debit, Cash, Bank Transfer, Digital Wallet)"
    - "Processing full and partial refunds with gateway synchronization"
    - "Maintaining idempotency for webhook event processing"
    - "Providing traceability between Payment, Sale, and Order entities"

    # product-service
    - "Inconsistent product data across services without a canonical owner."
    - "Need controlled write access with JWT roles (ADMIN/MANAGER)."
    - "Read-heavy catalog lookups need caching to reduce DB load."
    - "Other domains require product change events in near real-time."

    # store-service
    - "Store data must stay consistent with address and business rules (status transitions, soft delete)."
    - "Read-heavy discovery flows need low latency under concurrent traffic."
    - "Operators need secured mutation APIs while storefronts may read catalog data."

    # user-service
    - "REST API for authenticated reads and manager/admin mutations"
    - "Kafka consumption for user.created / user.updated / user.deleted side effects"
    - "Inter-service checks (uniqueness, credentials) intended via gRPC (see architecture notes)"
    - "Redis-backed caching and rate limiting hooks (shared-kernel)"

solution:
  solutionTitle: "Drugstore Platform Service Solutions"
  solutionList:
    # address-service
    - title: "Multi-Country Postal Code Validation"
      description: "Factory pattern implementation supporting country-specific postal code validators (US ZIP, MX CP, CA Postal Code, ES Código Postal, UK Postcode) with regex-based format verification"
    - title: "Role-Based Address Management"
      description: "Distinguishes between CUSTOMER (max 5 addresses) and EMPLOYEE (max 1 address) with configurable limits via AddressServiceProperties"
    - title: "Default Address Handling"
      description: "Automatic default address assignment for first address, with ability to set/unset defaults per user with database-level reset"
    - title: "Dual Controller Architecture"
      description: "Separate UserAddressController (for end-users) and AddressAdminController (for admins) with appropriate permission checks and audit logging"
    - title: "Redis-Backed Rate Limiting"
      description: "Configurable rate limiting profiles (STANDARD, SENSITIVE) using Redis with token bucket algorithm via AOP aspect"

    # auth-service
    - title: "DDD Orchestrator Pattern"
      description: "UseCasesOrquestrator coordinates all authentication use cases following Domain-Driven Design principles"
    - title: "JWT Token Management"
      description: "TokenFactory creates ACCESS (short-lived) and REFRESH (long-lived) JWT tokens with JJWT library"
    - title: "Multi-Channel Event Publishing"
      description: "UserEventProducer publishes to Kafka topics: user.created, user.updated, user.deleted, auth.password-changed, auth.account-activated, auth.two-factor-enabled/disabled"
    - title: "Redis Session Management"
      description: "RedisSessionRepository manages refresh token sessions with blacklisting support"
    - title: "gRPC User Service Client"
      description: "UserServiceGrpcClient communicates with user-service for user CRUD operations via Protobuf"

    # cart-service
    - title: "Cart Aggregate Root"
      description: "Cart.java is the aggregate root with items and afterwardsItems, encapsulating all business logic for add/update/remove/clear operations"
    - title: "gRPC Service Interface"
      description: "CartGrpcService exposes GetUserCart and ClearCart endpoints for order-service integration during checkout flow"
    - title: "Afterwards (Save-for-Later)"
      description: "Separate list for items saved for later, with move-to-afterwards and restore-from-afterwards operations"
    - title: "Kafka Product Events"
      description: "ProductEventConsumer listens to product-events topic and updates cart items via ProductEventHandler"
    - title: "Value Objects"
      description: "CartId, CustomerId, ProductId, Quantity, ItemPrice as strongly-typed value objects with validation"
    - title: "Redis Caching"
      description: "Spring Cache with Redis for cart lookups, configured via RedisCacheConfig"

    # employee-service
    - title: "Rich JPA Entity Model"
      description: "EmployeeEntity with embedded objects (ContactInfo, Address), certifications list, workday schedule as JSONB, and comprehensive enums"
    - title: "Command/Query Separation"
      description: "EmployeeCommandController (write) and EmployeeQueryController (read) with dedicated use cases"
    - title: "Rate Limiting"
      description: "Uses @RateLimit annotations from libs-kernel with STANDARD, SENSITIVE, PUBLIC profiles"
    - title: "Soft Delete Pattern"
      description: "deletedAt timestamp for soft deletes, with restore functionality"
    - title: "Audit Trail"
      description: "createdAt, updatedAt, createdBy, lastModifiedBy fields with @PrePersist/@PreUpdate"

    # inventory-service
    - title: "Inventory Entity with Batch Tracking"
      description: "InventoryEntity tracks total/available/reserved quantities, batches with lot numbers and expiration dates"
    - title: "Stock Reservation System"
      description: "Reservation system prevents overselling during order processing with automatic release on timeout"
    - title: "Inventory Movements Tracking"
      description: "All stock adjustments and transfers tracked via InventoryMovementEntity with audit trail"
    - title: "Low-Stock Alerts"
      description: "InventoryAlertEntity generates alerts when stock falls below reorder level"
    - title: "RabbitMQ Messaging"
      description: "Publishes inventory events (created, updated, low-stock) to RabbitMQ for other services"
    - title: "Redis Caching"
      description: "Spring Cache with Redis for frequently accessed inventory data"
    - title: "Integration testing (profile test)"
      description: "H2 in-memory, MockMvc REST tests, real JWT headers (JwtAuthenticationFilter); see docs/project/generated/ProjectFeature.md"

    # notification-service
    - title: ""
      description: ""

    # order-service
    - title: "Hexagonal Architecture Implementation"
      description: "Ports and adapters pattern separates domain logic from infrastructure concerns, allowing easy testing and technology swaps"
    - title: "Rich Domain Model"
      description: "Order aggregate root encapsulates business rules, state transitions, and validation logic with methods like confirm(), startPreparing(), ship(), complete(), cancel(), and returnOrder()"
    - title: "Multiple Delivery Methods"
      description: "Supports STORE_PICKUP, EXPRESS_DELIVERY, and STANDARD_DELIVERY with method-specific logic for address validation and pickup info"
    - title: "Event-Driven Domain Events"
      description: "Publishes OrderCreatedEvent and OrderStatusChangedEvent for asynchronous processing and system integration"
    - title: "Role-Based API Controllers"
      description: "Separate controllers for admin operations (SaleOrderController, SaleOrderStatusController) and customer operations (UserOrderController)"
    - title: "Caching Layer with Redis"
      description: "Redis caching for frequently accessed order data with decorator pattern (CachingUserServiceDecorator)"
    - title: "Comprehensive Validation"
      description: "Bean Validation annotations, custom domain validation, and duplicate product detection in orders"

    # payment-service
    - title: "Dual Aggregate Roots"
      description: "Payment aggregate manages payment lifecycle; Sale aggregate is auto-generated from completed payments representing confirmed business transactions"
    - title: "Stripe Integration (Planned)"
      description: "StripeGatewayAdapter implements StripeGatewayPort for PaymentIntent creation, charge processing, and refund operations"
    - title: "Webhook Handling"
      description: "Dedicated StripeWebhookController with signature verification, event parsing, and idempotent processing"
    - title: "State Machine Implementation"
      description: "PaymentStatus (6 states) and SaleStatus (4 states) enums with business rule validation for transitions"
    - title: "Domain Events"
      description: "PaymentCompletedEvent and PaymentFailedEvent enable loose coupling and trigger Sale creation"
    - title: "Spring Event Publishing"
      description: "SpringEventPublisherAdapter implements PaymentEventPublisher port for in-process domain event publishing"
    - title: "Value Objects"
      description: "Strongly-typed value objects (PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef, RefundInfo) for type safety"

    # product-service
    - title: "Catalog API"
      description: "REST endpoints for CRUD, search, category listing, and restore of soft-deleted products."
    - title: "Validation + Security"
      description: "Domain value objects/validators plus JWT-based role authorization and request rate limits."
    - title: "Caching + Events"
      description: "Redis-backed cache decorators for query use cases and Kafka publishing for product lifecycle events."

    # store-service
    - title: "Domain-driven core + adapters"
      description: "Commands and queries go through use cases; JPA implements persistence; REST exposes `/api/v2/stores` with OpenAPI."
    - title: "Redis-backed caching + rate limits"
      description: "Query results cached in named regions; method-level rate limits via shared-kernel profiles (global + per-endpoint)."
    - title: "Docker-first operations"
      description: "Compose brings Postgres, Redis, observability stack, and Nginx TLS in front of scalable store-service replicas."

    # user-service
    - title: "Command / query separation"
      description: "Write paths use CommandBus; reads use QueryBus over domain ports and JPA adapters."
    - title: "JWT stateless API"
      description: "libs_kernel JwtAuthenticationFilter; manager routes require ADMIN or MANAGER."
    - title: "Docker-first local stack"
      description: "Compose: PostgreSQL, Redis, optional external Kafka, Nginx TLS edge, Prometheus, Loki, Grafana."

keyMetrics:
  metricsTitle: "Drugstore Platform Service Key Metrics"
  metricsList:
    # address-service
    - "Supports 5+ country-specific postal code validations"
    - "Role-based limits: 5 addresses for customers, 1 for employees"
    - "REST API with 10 endpoints (CRUD + default management)"
    - "Rate limiting with Redis for DDoS protection"
    - "PostgreSQL with indexed queries (idx_user_id, idx_user_default)"

    # auth-service
    - "JWT Access Token expiration: configurable (default 15min)"
    - "JWT Refresh Token expiration: configurable (default 7 days)"
    - "Redis-backed session storage with blacklist support"
    - "Kafka integration with 7+ event topics"
    - "REST API with 12+ endpoints across 4 controllers"
    - "OAuth2 support with custom OAuth2UserService"

    # cart-service
    - "Max 100 unique items per cart (MAX_ITEMS_PER_CART)"
    - "gRPC endpoints: 2 (GetUserCart, ClearCart)"
    - "REST endpoints: 5+ (user + admin controllers)"
    - "Domain events: CartPurchasedEvent (PLACEHOLDER: not published yet)"
    - "Kafka topic: product-events for product updates"
    - "Unit tests: 11 test files in src/test"

    # employee-service
    - "7 Employee Role enums: PHARMACIST, PHARMACY_TECHNICIAN, STORE_MANAGER, ASSISTANT_MANAGER, CASHIER, INVENTORY_CLERK, DELIVERY_DRIVER, CUSTOMER_SERVICE_REP, JANITOR"
    - "4 Employee Type enums: FULL_TIME, PART_TIME, CONTRACTOR, INTERN, SEASONAL"
    - "5 Employee Status enums: ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED, TERMINATED"
    - "REST API with 12+ endpoints (command + query controllers)"
    - "JSONB column for workdaySchedule (flexible schedule storage)"
    - "Pagination and search support with Specification pattern"

    # inventory-service
    - "InventoryEntity with total/available/reserved quantity tracking"
    - "InventoryBatchEntity with lot numbers, expiration dates, supplier info"
    - "StockReservationEntity for order processing (prevent overselling)"
    - "InventoryMovementEntity for adjustments and transfers tracking"
    - "InventoryAlertEntity for low-stock notifications"
    - "REST API with 15+ endpoints (inventory, batches, reservations, movements)"
    - "RabbitMQ integration (AMQP) for event publishing"
    - "Integration tests: InventoryApiIntegrationTest + JWT helper (profile test)"

    # notification-service
    - ""

    # product-service
    - "8 public/protected REST endpoints under /api/v2/products"
    - "4 cache regions (productById, productBySKU, productByBarcode, productSearch)"
    - "1 primary product-events topic with DLT support"
    - "7 passing integration tests in ProductApiIntegrationTest"

    # store-service
    - "p95 read < 200ms at the app (exclude WAN) — TBD"
    - "Cache hit ratio for `stores` / `store_searches` — TBD"

    # user-service
    - "p95 read latency < 150ms at steady state (not measured in-repo)"
    - "Actuator /actuator/health for container orchestration"
    - "Flyway on in docker profile when application-docker.yml is corrected (see risks)"

links:
  github: https://github.com/alexisTrejo11/drugstore-platform
  demo: null
  documentation: docs/project/source
  dockerHub: null
mediaGallery:
  title: "Drugstore Platform Service Gallery"
  description: "Merged media gallery items from all services."
  items:
    # address-service
    - type: "image"
      url: "https://placeholder-drugstore.com/images/address-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/address-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation generated by Springdoc"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/address-architecture.png"
      thumbnail: "https://placeholder-drugstore.com/images/address-architecture-thumb.png"
      title: "Service Architecture"
      description: "Layered architecture with controllers, service, repository, and validators"
      alt: "Architecture diagram"
      category: "architecture"

    # auth-service
    - type: "image"
      url: "https://placeholder-drugstore.com/images/auth-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/auth-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation for auth endpoints"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/auth-architecture.png"
      thumbnail: "https://placeholder-drugstore.com/images/auth-architecture-thumb.png"
      title: "DDD Architecture"
      description: "Domain-Driven Design layered architecture"
      alt: "Architecture diagram"
      category: "architecture"

    # cart-service
    - type: "image"
      url: "https://placeholder-drugstore.com/images/cart-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/cart-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation for cart endpoints"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/cart-domain-model.png"
      thumbnail: "https://placeholder-drugstore.com/images/cart-domain-model-thumb.png"
      title: "Domain Model Diagram"
      description: "Cart aggregate root with items and afterwardsItems"
      alt: "Domain model diagram"
      category: "architecture"

    # employee-service
    - type: "image"
      url: "https://placeholder-drugstore.com/images/employee-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/employee-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation for employee endpoints"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/employee-entity-model.png"
      thumbnail: "https://placeholder-drugstore.com/images/employee-entity-model-thumb.png"
      title: "JPA Entity Model"
      description: "EmployeeEntity with embedded objects and enums"
      alt: "Entity model diagram"
      category: "architecture"

    # inventory-service
    - type: "image"
      url: "https://placeholder-drugstore.com/images/inventory-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/inventory-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation for inventory endpoints"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/inventory-entity-model.png"
      thumbnail: "https://placeholder-drugstore.com/images/inventory-entity-model-thumb.png"
      title: "JPA Entity Model"
      description: "Inventory, Batch, Reservation, Movement, Alert entities"
      alt: "Entity model diagram"
      category: "architecture"

    # notification-service
    - type: "image"
      url: ""
      thumbnail: ""
      title: ""
      description: ""
      alt: ""
      category: "screenshot"

    # order-service
    - type: "image"
      url: "/assets/projects/order-service-api-swagger.png"
      thumbnail: "/assets/projects/order-service-api-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation showing all order management endpoints"
      alt: "Swagger UI for Order Service"
      category: "screenshot"
    - type: "image"
      url: "/assets/projects/order-service-architecture.png"
      thumbnail: "/assets/projects/order-service-architecture-thumb.png"
      title: "Architecture Diagram"
      description: "Hexagonal architecture layers showing domain, ports, and adapters"
      alt: "Order Service Architecture"
      category: "architecture"
    - type: "image"
      url: "/assets/projects/order-service-state-machine.png"
      thumbnail: "/assets/projects/order-service-state-machine-thumb.png"
      title: "Order State Machine"
      description: "Visual representation of order status transitions"
      alt: "Order Status State Machine"
      category: "diagram"

    # payment-service
    - type: "image"
      url: "/assets/projects/payment-service-api-swagger.png"
      thumbnail: "/assets/projects/payment-service-api-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation showing payment and sale endpoints"
      alt: "Swagger UI for Payment Service"
      category: "screenshot"
    - type: "image"
      url: "/assets/projects/payment-service-state-machine.png"
      thumbnail: "/assets/projects/payment-service-state-machine-thumb.png"
      title: "Payment State Machine"
      description: "Visual representation of Payment and Sale status transitions"
      alt: "Payment Status State Machine"
      category: "diagram"
    - type: "image"
      url: "/assets/projects/payment-service-stripe-flow.png"
      thumbnail: "/assets/projects/payment-service-stripe-flow-thumb.png"
      title: "Stripe Integration Flow"
      description: "PaymentIntent creation to webhook confirmation flow"
      alt: "Stripe Payment Flow"
      category: "diagram"

    # product-service
    - type: "image"
      url: "src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/in/web/controller/ProductController.java"
      thumbnail: "https://placehold.co/320x200?text=Controller"
      title: "REST Controller"
      description: "Main API surface for product operations."
      alt: "ProductController path reference"
      category: "code"

    # store-service
    - type: "image"
      url: "https://placehold.co/800x450/e8eaf6/3949ab?text=Swagger+UI+TBD"
      thumbnail: "https://placehold.co/200x112/e8eaf6/3949ab?text=Swagger"
      title: "OpenAPI / Swagger UI"
      description: "Available at /swagger-ui.html when SWAGGER_ENABLED=true"
      alt: "Placeholder Swagger screenshot"
      category: "screenshot"

    # user-service
    - type: "image"
      url: "https://placehold.co/800x450/e0e0e0/333?text=Swagger+UI"
      thumbnail: "https://placehold.co/200x113/e0e0e0/333?text=Thumb"
      title: "API docs (placeholder)"
      description: "Springdoc OpenAPI — fix packages-to-scan for this service first."
      alt: "Placeholder screenshot"
      category: "screenshot"

mediaItems:
  # address-service
  - type: "image"
    url: "https://placeholder-drugstore.com/images/address-postman.png"
    thumbnail: "https://placeholder-drugstore.com/images/address-postman-thumb.png"
    title: "Postman API Tests"
    description: "API endpoint testing with Postman"
    alt: "Postman collection screenshot"
    category: "screenshot"

  # auth-service
  - type: "image"
    url: "https://placeholder-drugstore.com/images/auth-jwt-flow.png"
    thumbnail: "https://placeholder-drugstore.com/images/auth-jwt-flow-thumb.png"
    title: "JWT Token Flow"
    description: "Access + Refresh token rotation flow"
    alt: "JWT flow diagram"
    category: "diagram"

  # cart-service
  - type: "image"
    url: "https://placeholder-drugstore.com/images/cart-grpc-flow.png"
    thumbnail: "https://placeholder-drugstore.com/images/cart-grpc-flow-thumb.png"
    title: "gRPC Checkout Flow"
    description: "How order-service calls cart-service via gRPC during checkout"
    alt: "gRPC flow diagram"
    category: "diagram"

  # employee-service
  - type: "image"
    url: "https://placeholder-drugstore.com/images/employee-status-flow.png"
    thumbnail: "https://placeholder-drugstore.com/images/employee-status-flow-thumb.png"
    title: "Employee Status Flow"
    description: "Status transitions: ACTIVE → SUSPENDED → TERMINATED"
    alt: "Status flow diagram"
    category: "diagram"

  # inventory-service
  - type: "image"
    url: "https://placeholder-drugstore.com/images/inventory-reservation-flow.png"
    thumbnail: "https://placeholder-drugstore.com/images/inventory-reservation-flow-thumb.png"
    title: "Stock Reservation Flow"
    description: "How reservations prevent overselling during order processing"
    alt: "Reservation flow diagram"
    category: "diagram"

  # notification-service
  - type: "image"
    url: ""
    thumbnail: ""
    title: ""
    description: ""
    alt: ""
    category: "screenshot"

  # order-service
  - type: "image"
    url: "/assets/projects/order-service-api-swagger.png"
    thumbnail: "/assets/projects/order-service-api-swagger-thumb.png"
    title: "Swagger API Documentation"
    description: "OpenAPI documentation showing all order management endpoints"
    alt: "Swagger UI for Order Service"
    category: "screenshot"
  - type: "image"
    url: "/assets/projects/order-service-architecture.png"
    thumbnail: "/assets/projects/order-service-architecture-thumb.png"
    title: "Architecture Diagram"
    description: "Hexagonal architecture layers showing domain, ports, and adapters"
    alt: "Order Service Architecture"
    category: "architecture"

  # payment-service
  - type: "image"
    url: "/assets/projects/payment-service-api-swagger.png"
    thumbnail: "/assets/projects/payment-service-api-swagger-thumb.png"
    title: "Swagger API Documentation"
    description: "OpenAPI documentation showing payment and sale endpoints"
    alt: "Swagger UI for Payment Service"
    category: "screenshot"
  - type: "image"
    url: "/assets/projects/payment-service-state-machine.png"
    thumbnail: "/assets/projects/payment-service-state-machine-thumb.png"
    title: "Payment State Machine"
    description: "Visual representation of Payment and Sale status transitions"
    alt: "Payment Status State Machine"
    category: "diagram"

  # product-service
  - type: "image"
    url: "docker-compose.yml"
    thumbnail: "https://placehold.co/320x200?text=Compose"
    title: "Compose topology"
    description: "App + nginx + redis + postgres + observability services."
    alt: "docker compose reference"
    category: "infra"

  # store-service
  - type: "image"
    url: "https://placehold.co/800x450/eeeeee/333333?text=Architecture+Diagram+TBD"
    thumbnail: "https://placehold.co/200x112/eeeeee/333333?text=Arch"
    title: "High-level topology"
    description: "Client → Nginx → store-service → Postgres / Redis"
    alt: "Placeholder architecture diagram"
    category: "diagram"

  # user-service
  - type: "image"
    url: "https://placehold.co/800x450/009688/ffffff?text=Nginx+TLS"
    thumbnail: "https://placehold.co/200x113/009688/ffffff?text=TLS"
    title: "Nginx edge (Compose)"
    description: "TLS termination on :443; upstream to user-service:8080."
    alt: "Nginx placeholder"
    category: "diagram"

metrics:
  # address-service
  - label: "API Endpoints"
    value: "10"
    description: "Total REST endpoints (user + admin)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Countries Supported"
    value: "5+"
    description: "Countries with postal code validation"
    icon: "globe"
    unit: "countries"
    trend: "up"
    threshold: null
  - label: "Response Time"
    value: "<50ms"
    description: "Average API response time (p95)"
    icon: "clock"
    unit: "ms"
    trend: "stable"
    threshold: 100
  # auth-service
  - label: "Integration tests"
    value: "AuthEndpointsIntegrationTest"
    description: "Full-stack HTTP tests with Testcontainers Redis/Kafka + gRPC stub (Docker required)"
    icon: "test-tube"
    unit: "suite"
    trend: "stable"
    threshold: null
  - label: "API Endpoints"
    value: "12+"
    description: "Total REST endpoints across 4 controllers"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Kafka Topics"
    value: "7+"
    description: "Event types published to Kafka"
    icon: "kafka"
    unit: "topics"
    trend: "up"
    threshold: null
  - label: "Token Types"
    value: "4"
    description: "ACCESS, REFRESH, ACTIVATION, TWO_FA"
    icon: "token"
    unit: "types"
    trend: "stable"
    threshold: null
  # cart-service
  - label: "API Endpoints"
    value: "5+"
    description: "REST endpoints (user + admin controllers)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "gRPC Methods"
    value: "2"
    description: "GetUserCart, ClearCart for order-service"
    icon: "grpc"
    unit: "methods"
    trend: "stable"
    threshold: null
  - label: "Max Items/Cart"
    value: "100"
    description: "Maximum unique products per cart"
    icon: "cart"
    unit: "items"
    trend: "stable"
    threshold: 100
  # employee-service
  - label: "API Endpoints"
    value: "12+"
    description: "Total REST endpoints (command + query)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Employee Roles"
    value: "9"
    description: "Role enums in EmployeeRoleEnum"
    icon: "roles"
    unit: "roles"
    trend: "stable"
    threshold: null
  - label: "Response Time"
    value: "<50ms"
    description: "Average API response time (p95)"
    icon: "clock"
    unit: "ms"
    trend: "stable"
    threshold: 100
  # inventory-service
  - label: "API Endpoints"
    value: "15+"
    description: "REST endpoints (inventory, batches, reservations, movements)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Entities"
    value: "5"
    description: "Inventory, Batch, Movement, Reservation, Alert"
    icon: "database"
    unit: "entities"
    trend: "stable"
    threshold: null
  - label: "Messaging"
    value: "RabbitMQ"
    description: "AMQP for inventory events (not Kafka like other services)"
    icon: "rabbitmq"
    unit: "type"
    trend: "stable"
    threshold: null
  # notification-service
  - label: ""
    value: ""
    description: ""
    icon: ""
    unit: ""
    trend: "stable"
    threshold: null
  # order-service
  - label: "Total Endpoints"
    value: "15"
    description: "Number of REST API endpoints for order management"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Order Statuses"
    value: "9"
    description: "Total number of order status states in the state machine"
    icon: "state"
    unit: "statuses"
    trend: "stable"
    threshold: null
  - label: "Delivery Methods"
    value: "3"
    description: "Supported delivery methods: Store Pickup, Express, Standard"
    icon: "shipping"
    unit: "methods"
    trend: "stable"
    threshold: null
  - label: "Domain Events"
    value: "2"
    description: "Domain events: OrderCreatedEvent and OrderStatusChangedEvent"
    icon: "event"
    unit: "events"
    trend: "stable"
    threshold: null
  - label: "API Response Time"
    value: "<200ms"
    description: "Average API response time under normal load"
    icon: "speed"
    unit: "ms"
    trend: "down"
    threshold: 500
  # payment-service
  - label: "Total Endpoints"
    value: "9"
    description: "Number of REST API endpoints (Payments + Sales + Webhooks)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Payment Statuses"
    value: "6"
    description: "Total number of payment status states in the state machine"
    icon: "state"
    unit: "statuses"
    trend: "stable"
    threshold: null
  - label: "Sale Statuses"
    value: "4"
    description: "Total number of sale status states"
    icon: "state"
    unit: "statuses"
    trend: "stable"
    threshold: null
  - label: "Payment Methods"
    value: "6"
    description: "Supported payment methods including Credit Card, Debit, Cash, etc."
    icon: "payment"
    unit: "methods"
    trend: "stable"
    threshold: null
  - label: "Domain Events"
    value: "2+"
    description: "Domain events: PaymentCompletedEvent, PaymentFailedEvent"
    icon: "event"
    unit: "events"
    trend: "stable"
    threshold: null
  - label: "Aggregate Roots"
    value: "2"
    description: "Payment and Sale aggregate roots with separate lifecycles"
    icon: "domain"
    unit: "aggregates"
    trend: "stable"
    threshold: null
  # product-service
  - label: "Endpoints"
    value: "8"
    description: "HTTP endpoints exposed by ProductController."
    icon: "api"
    unit: "routes"
    trend: "stable"
    threshold: "N/A"
  - label: "Cache Regions"
    value: "4"
    description: "Core cache buckets used by caching decorators."
    icon: "database"
    unit: "regions"
    trend: "stable"
    threshold: "N/A"
  - label: "Integration Tests"
    value: "7"
    description: "Current ProductApiIntegrationTest coverage count."
    icon: "test"
    unit: "tests"
    trend: "stable"
    threshold: "N/A"
  # store-service
  - label: "Service version"
    value: "2.0.0"
    description: "Gradle project version"
    icon: "package"
    unit: ""
    trend: "stable"
    threshold: null
  - label: "API base path"
    value: "/api/v2/stores"
    description: "All store REST resources"
    icon: "api"
    unit: ""
    trend: "stable"
    threshold: null
  # user-service
  - label: "Service version (build.gradle)"
    value: "2.0.0"
    description: "Gradle project version string"
    icon: "package"
    unit: ""
    trend: "stable"
    threshold: null
  - label: "Java toolchain"
    value: "23"
    description: "Eclipse Temurin in Dockerfile matches toolchain"
    icon: "java"
    unit: ""
    trend: "stable"
    threshold: null
---

# Project Overview

> Auto-generated by `scripts/merge_service_sources.py`. Edit service-level `docs/project/source/*.md` files, then regenerate.

<!-- BEGIN address-service -->
<!-- Source: address-service/docs/project/source/ProjectOverview.md -->
# Overview

> Production-ready address microservice with comprehensive validation, security, and rate limiting. Potential improvements: add Kafka events for address changes (sync with user-service), implement caching layer for frequent address lookups, add metrics (Micrometer/Prometheus) for observability, and create Kubernetes deployment manifests for cloud-native deploy.

<!-- END address-service -->

<!-- BEGIN auth-service -->
<!-- Source: auth-service/docs/project/source/ProjectOverview.md -->
# Overview

> Production-ready auth service with JWT, 2FA, OAuth2, Kafka, and gRPC. PostgreSQL is not embedded locally—user records live in user-service (accessed via gRPC). **Integration tests** (`integration-test` + `test` profiles) run against Testcontainers Redis/Kafka and an in-process UserService; use Docker locally/CI. Further improvements: more unit tests, Kubernetes manifests, Circuit Breaker on gRPC, Micrometer metrics.

<!-- END auth-service -->

<!-- BEGIN cart-service -->
<!-- Source: cart-service/docs/project/source/ProjectOverview.md -->
# Overview

> Well-structured DDD cart service with aggregate root pattern. Has unit tests for domain layer (11 test files). Potential issues: No @RateLimit annotations on REST endpoints (unlike address-service and auth-service), CartPurchasedEvent defined but not published to Kafka, Java 23 compatibility issues with Gradle 8.11. Improvements needed: Add integration tests, Kubernetes manifests, CI/CD pipeline, publish cart events to Kafka.

<!-- END cart-service -->

<!-- BEGIN employee-service -->
<!-- Source: employee-service/docs/project/source/ProjectOverview.md -->
# Overview

> Comprehensive employee management service with rich JPA entity model. Uses @RateLimit from libs-kernel (unlike cart-service). PLACEHOLDER issues: No Dockerfile, no docker-compose.yml, no unit/integration tests found. Potential improvements: Add Kafka event publishing (employee.created/updated/deleted), implement caching for employee lookups, add Kubernetes manifests, set up CI/CD pipeline.

<!-- END employee-service -->

<!-- BEGIN inventory-service -->
<!-- Source: inventory-service/docs/project/source/ProjectOverview.md -->
# Overview

> Comprehensive inventory service with batch tracking, reservations, and stock movements. Uses RabbitMQ (not Kafka like other services - inconsistency). Dockerfile uses openjdk:17-jdk-slim (not Eclipse Temurin like other services). **Testing:** Spring Boot integration tests run under profile `test` (H2, real JWT headers); see `docs/project/generated/ProjectFeature.md`. **Still missing:** broader unit coverage, migrate to Kafka for consistency, Kubernetes manifests, CI/CD pipeline.

<!-- END inventory-service -->

<!-- BEGIN notification-service -->
<!-- Source: notification-service/docs/project/source/ProjectOverview.md -->
# Overview

>  Notes goes here.

<!-- END notification-service -->

<!-- BEGIN order-service -->
<!-- Source: order-service/docs/project/source/ProjectOverview.md -->
# Overview
> Order Service is a core microservice implementing DDD and hexagonal architecture. It manages the complete order lifecycle with support for multiple delivery methods, complex state transitions, and event-driven architecture. The service uses PostgreSQL for persistence, Redis for caching, and OpenSearch/ELK stack for centralized logging.

<!--
  OBSERVATIONS FOR ProjectOverview:
  ✅ POSITIVE:
    - Clear problem statement with 7 identified sub-problems
    - Solution uses industry best practices (DDD, Hexagonal Architecture)
    - Supports 3 delivery methods with method-specific business logic
    - Event-driven architecture with domain events for loose coupling
    - Role-based API access (CUSTOMER, EMPLOYEE, ADMIN)
    - Rich domain model encapsulates business rules properly

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - mediaGallery items reference placeholder asset paths (/assets/projects/...) - need actual screenshots
    - coverImage URL is placeholder - no actual cover image uploaded
    - API response time metric (<200ms) is estimated - not benchmarked yet
    - No screenshot of Swagger UI or architecture diagram exists yet
    - demo field is null in metadata - no live demo to showcase
-->

<!-- END order-service -->

<!-- BEGIN payment-service -->
<!-- Source: payment-service/docs/project/source/ProjectOverview.md -->
# Overview
> Payment Service is a core microservice implementing DDD with two aggregate roots (Payment and Sale). It manages the complete payment lifecycle with Stripe integration (planned), webhook handling with signature verification, refund processing, and automatic Sale generation. The service uses PostgreSQL for persistence, Redis for caching, and Spring Events for domain event publishing.

<!--
  OBSERVATIONS FOR ProjectOverview:
  ✅ POSITIVE:
    - Clear problem statement with 8 identified sub-problems
    - Solution uses industry best practices (DDD, dual aggregates)
    - Supports 6 payment methods with extensible enum
    - Webhook handling with Stripe signature verification
    - Strongly-typed value objects for type safety
    - Domain events for loose coupling between Payment and Sale
    - Comprehensive lifecycle documentation for both aggregates

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - mediaGallery items reference placeholder asset paths (/assets/projects/...) - need actual screenshots
    - coverImage URL is placeholder - no actual cover image uploaded
    - StripeGatewayAdapter IS A STUB - returns null/empty strings (line 11, 16, 21)!
    - "PLANNED" in description but Stripe dependency not in build.gradle (only commented)
    - No SecurityConfig - controllers have "Security placeholder" comments
    - Payment and Sale timeStamps use now() but no timezone configuration mentioned
    - API response time not benchmarked - no performance metrics yet
    - No screenshot of Swagger UI or architecture diagram exists yet
    - demo field is null - no live demo to showcase
    - Refund flow depends on StripeGatewayAdapter which is not implemented
-->

<!-- END payment-service -->

<!-- BEGIN product-service -->
<!-- Source: product-service/docs/project/source/ProjectOverview.md -->
# Overview

- Product Service is the catalog owner in the Drugstore platform.
- It combines CRUD/search APIs, JWT authn/authz, Redis caching, and Kafka event output.

## Notes

- Configuration has mixed env var names (`product_DB` vs `POSTGRES_DB_NAME`) that can cause misconfiguration.
- Swagger and broad actuator exposure are enabled by default; tighten for non-dev environments.

<!-- END product-service -->

<!-- BEGIN store-service -->
<!-- Source: store-service/docs/project/source/ProjectOverview.md -->
# Overview

Drugstore **store-service** owns **physical store master data**: code, name, contact, postal address, geolocation JSON schedule (`schedule_config`), and lifecycle statuses (`ACTIVE`, `INACTIVE`, `UNDER_MAINTENANCE`, `TEMPORARILY_CLOSED`).

### Highlighted risks & gaps

| Severity | Topic |
|---------|--------|
| **Danger** | **Dev profile YAML** mismatches prod (`product-service`, wrong DB env key `product_DB`) — risk of corrupting wrong schema locally. |
| **Danger** | **Redis serializer** enables Jackson default typing (`NON_FINAL`) — classic ** deserialization gadget** risk if Redis is reachable by an attacker; harden serializer strategy for production. |
| **Danger** | **Swagger + actuator footprint**: dev exposes broad actuator CORS/origins; lock down before any public demo. |
| **Missing** | **Kafka publishing** is stubbed; downstream consumers never receive events. |
| **Missing** | **`PATCH temporary-closure` and `PATCH deactivate`** omit `@RateLimit` — inconsistent abuse surface vs other commands. |

<!-- END store-service -->

<!-- BEGIN user-service -->
<!-- Source: user-service/docs/project/source/ProjectOverview.md -->
# Overview

The **user-service** is a Spring Boot microservice that owns **user persistence**, **profile** data, **JWT-protected REST APIs**, **Kafka consumption** for user lifecycle events, and (in code only) a **gRPC service definition** intended for other services.

## Highlighted risks (read first)

| Severity | Topic | Note |
|----------|--------|------|
| **High** | `application-docker.yml` | File content matches **store-service** (wrong `spring.application.name`, DB name defaults, springdoc scan, log/metrics labels). Docker runs may not match user-service expectations until fixed. |
| **High** | OpenAPI scan | `springdoc.packages-to-scan` points at `io.github.alexisTrejo11.drugstore.**stores**`, not `...users**` — Swagger may omit user controllers. |
| **Medium** | gRPC | `UserGrpcServer` exists but **no gRPC Netty server / Spring gRPC starter** wires it; `GrpcServerConfig` is empty. Port `9090` in config does not expose a working RPC server in this codebase. |
| **Medium** | Kafka consumer | On failure, **no ack** and **no DLQ** — throws `RuntimeException`; can cause tight retry loops (TODOs in code). |
| **Medium** | Dev profile | Default JWT secret placeholder, Flyway **disabled** with `ddl-auto: update`, actuator **`*`** exposure — not production-safe. |

## What is intentionally generic / placeholder

- **Cloud**: no AWS/GCP/Azure resources are defined in-repo; YAML uses dummy provider names below in infrastructure docs.
- **Links**: GitHub/demo URLs are placeholders until you publish the repo.

<!-- END user-service -->
