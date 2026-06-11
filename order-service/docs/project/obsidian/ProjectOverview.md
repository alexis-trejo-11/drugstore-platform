---
problemStatement:
  problemTitle: "Order Management Complexity in Multi-Channel Drugstore Platform"
  problemDescription: "A drugstore platform needs a robust order management system that can handle multiple delivery methods (store pickup, express delivery, standard delivery), complex state transitions, item validation, and integration with external services like user management and address validation. The system must enforce business rules, handle events for asynchronous processing, and provide comprehensive API for both customers and administrators."
  problemList:
    - "Managing complex order state transitions with validation rules"
    - "Supporting multiple delivery methods with different business logic"
    - "Handling order items with prescription drug validation"
    - "Integrating with external user and address services"
    - "Providing role-based API access for customers, employees, and admins"
    - "Implementing event-driven architecture for order lifecycle events"
    - "Caching frequently accessed data for performance"

solution:
  solutionTitle: "Domain-Driven Design with Hexagonal Architecture"
  solutionList:
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

coverImage:
  url: "/assets/projects/order-service-cover.png"
  alt: "Order Service Architecture Diagram"
  credit: "Drugstore Platform Team"

links:
  github: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service"
  demo: null
  documentation: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/docs"
  dockerHub: null

mediaGallery:
  title: "Order Service Screenshots and Diagrams"
  description: "Visual representations of the order service architecture and API"
  items:
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

mediaItems:
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

metrics:
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
---
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
