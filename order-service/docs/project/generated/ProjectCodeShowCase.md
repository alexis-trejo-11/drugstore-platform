# CodeShowCase
> Code examples showcasing DDD, hexagonal architecture, state machine pattern, REST API design with OpenAPI, Decorator pattern for caching, and domain events for event-driven architecture. All examples are from actual production code in the Order Service.

<!--
  OBSERVATIONS FOR CodeShowCase:
  ✅ POSITIVE:
    - 6 comprehensive code examples covering key architectural concepts
    - Real production code from the actual codebase
    - Examples show modern Java 23 features (records, enhanced switch, Lombok)
    - Covers DDD patterns (Aggregate Root, Value Objects, Domain Events)
    - Shows design patterns (Decorator, Facade, State, Builder)
    - Code examples have explanations and highlighted sections
    - OpenAPI annotation usage demonstrated

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - "views: 0" is placeholder - no actual view tracking implemented
    - CachingUserServiceDecorator example shows manual cache management - could use @Cacheable annotation instead
    - No unit test code examples included - test coverage unknown
    - Order.create() factory method doesn't show OrderCreatedEvent being published (should happen after persistence)
    - Domain events (OrderCreatedEvent, OrderStatusChangedEvent) are defined but no example of EventPublisher usage
    - codeExamples don't include error handling or edge case examples
    - No example of OrderSpecifications usage for dynamic queries
    - Record classes used for DTOs but Java 23 is required - limits compatibility
    - Could add examples of: OrderItem, Money value object, OrderTimestamps
-->
