---
# ProjectFeature[]
features:
  - id: "cart-aggregate-root"
    title: "Cart Aggregate Root"
    description: "Cart.java is the aggregate root encapsulating items and afterwardsItems with business logic for add/update/remove/clear operations. Enforces invariants like max 100 unique items."
    icon: "🏰"
    category: "domain"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/Cart.java"
    highlights:
      - "Cart.create() and Cart.reconstruct() factory methods"
      - "addItem() with quantity merging for existing products"
      - "updateItemQuantity() for changing item quantities"
      - "removeItem() and removeItems() for item removal"
      - "clear() with optional exclusion list for checkout"
      - "moveItemsToAfterwards() and returnItemsFromAfterwards() for save-for-later"
      - "calculateTotal(), calculateSubtotal(), calculateDiscount() for price calculations"
    techStack:
      - "DDD Aggregate Root"
      - "Domain Model"
      - "Business Logic Encapsulation"
    metrics:
      - label: "Max Items/Cart"
        value: "100"
        trend: "stable"
        icon: "cart"
      - label: "Business Methods"
        value: "15+"
        trend: "stable"
        icon: "methods"
    codeSnippet:
      language: "java"
      filename: "Cart.java"
      code: |
        public void addItem(CartItem item) {
            Optional<CartItem> existingItem = findItemByProductId(item.getProductId());
            if (existingItem.isPresent()) {
                existingItem.get().mergeWith(item);
            } else {
                validateCanAddItem();
                items.add(item);
            }
            timeStamps.markAsUpdated();
        }

  - id: "afterwards-save-for-later"
    title: "Afterwards (Save-for-Later)"
    description: "Separate list for items saved for later with move-to-afterwards and restore-from-afterwards operations. Managed within Cart aggregate root."
    icon: "📥"
    category: "feature"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/AfterwardsItem.java"
    highlights:
      - "AfterwardsItem created from CartItem via createFromItem()"
      - "moveItemsToAfterwards() moves items from cart to afterwards"
      - "returnItemsFromAfterwards() restores items back to cart"
      - "Both lists maintained in Cart aggregate root"
      - "REST endpoints: /move-to-afterwards, /restore-from-afterwards"
    techStack:
      - "DDD Value Objects"
      - "Domain Logic"
    metrics:
      - label: "Use Cases"
        value: "2"
        trend: "stable"
        icon: "usecase"
    codeSnippet:
      language: "java"
      filename: "Cart.java"
      code: |
        public void moveItemsToAfterwards(List<ProductId> productIds) {
            List<CartItem> itemsToMove = items.stream()
                .filter(item -> productIds.contains(item.getProductId()))
                .toList();

            List<AfterwardsItem> afterwardsItemsToAdd = itemsToMove.stream()
                .map(AfterwardsItem::createFromItem)
                .toList();

            this.afterwardsItems.addAll(afterwardsItemsToAdd);
            this.items.removeAll(itemsToMove);
            timeStamps.markAsUpdated();
        }

  - id: "grpc-service-interface"
    title: "gRPC Service Interface"
    description: "CartGrpcService exposes GetUserCart and ClearCart endpoints for order-service integration during checkout flow. Uses Protobuf 3.25.1 and gRPC 1.60.0."
    icon: "🔌"
    category: "integration"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/grpc/CartGrpcService.java"
    highlights:
      - "GetUserCart - Returns cart for order-service during checkout"
      - "ClearCart - Clears cart after successful order (with exclusion list)"
      - "Uses CartGrpcMapper for Protobuf ↔ Domain mapping"
      - "Spring Boot gRPC integration with @GrpcService annotation"
    techStack:
      - "gRPC 1.60.0"
      - "Protobuf 3.25.1"
      - "Spring gRPC"
    metrics:
      - label: "gRPC Methods"
        value: "2"
        trend: "stable"
        icon: "grpc"
      - label: "Protocol"
        value: "HTTP/2"
        trend: "stable"
        icon: "http2"
    codeSnippet:
      language: "java"
      filename: "CartGrpcService.java"
      code: |
        @GrpcService
        public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {
            @Override
            public void getUserCart(GetUserCartRequest request,
                                    StreamObserver<CartResponse> responseObserver) {
                GetCartByCustomerIdQuery query =
                    GetCartByCustomerIdQuery.from(request.getUserId());
                Cart cart = cartQueryUseCase.getCartByCustomerId(query);
                CartResponse response = mapper.toGrpcResponse(cart);
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }

  - id: "kafka-product-events"
    title: "Kafka Product Events Consumption"
    description: "ProductEventConsumer listens to product-events topic and updates cart items via ProductEventHandler. Handles price changes and product availability updates."
    icon: "📢"
    category: "messaging"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/product/adapter/input/message/kafka/ProductEventConsumer.java"
    highlights:
      - "Listens to product-events topic with @KafkaListener"
      - "ProductEventHandler updates cart items on price changes"
      - "Handles product deletion (remove from carts)"
      - "Eventually consistent cart data with latest product info"
    techStack:
      - "Spring Kafka"
      - "Kafka"
      - "Event-Driven"
    metrics:
      - label: "Kafka Topic"
        value: "product-events"
        trend: "stable"
        icon: "topic"
      - label: "Consumer Group"
        value: "PLACEHOLDER: ${spring.kafka.consumer.group-id}"
        trend: "stable"
        icon: "group"
    codeSnippet:
      language: "java"
      filename: "ProductEventConsumer.java"
      code: |
        @KafkaListener(topics = "product-events",
                      groupId = "${spring.kafka.consumer.group-id}")
        public void consume(ProductEvent event) {
            log.info("Received product event: type={}, id={}",
                      event.getEventType(), event.getPayload().getId());
            eventHandler.handle(event);
        }

  - id: "value-objects-pattern"
    title: "Value Objects Pattern"
    description: "Strongly-typed values: CartId (UUID), CustomerId, ProductId, Quantity (with validation), ItemPrice (BigDecimal wrapper), CartTimeStamps."
    icon: "🧱"
    category: "domain"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/valueobjects"
    highlights:
      - "CartId - UUID generation via CartId.generate()"
      - "CustomerId - Wraps customer identifier"
      - "ProductId - Wraps product identifier"
      - "Quantity - Validates non-negative, non-zero values"
      - "ItemPrice - BigDecimal wrapper with arithmetic operations"
      - "CartTimeStamps - Created/updated/deleted timestamps with markAsUpdated()"
    techStack:
      - "DDD Value Objects"
      - "Type Safety"
      - "Validation"
    metrics:
      - label: "Value Objects"
        value: "6+"
        trend: "stable"
        icon: "object"
    codeSnippet:
      language: "java"
      filename: "Quantity.java"
      code: |
        public record Quantity(int value) {
            public Quantity {
                if (value < 0) {
                    throw new InvalidQuantityException("Quantity cannot be negative");
                }
                if (value == 0) {
                    throw new InvalidQuantityException("Quantity cannot be zero");
                }
            }
            public static Quantity of(int value) {
                return new Quantity(value);
            }
        }

  - id: "ddd-ports-adapters"
    title: "DDD Ports & Adapters Architecture"
    description: "Clean bounded context with ports (CartRepository, CartCommandUseCase, CartQueryUseCase) and adapters (CartJpaRepository, CartCommandUseCaseImpl)."
    icon: "🔌"
    category: "architecture"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/port"
    highlights:
      - "Ports: CartRepository, CartCommandUseCase, CartQueryUseCase"
      - "Adapters: CartJpaRepository, CartCommandUseCaseImpl, CartQueryUseCaseImpl"
      - "Command Use Cases: CreateCart, UpdateCartItems, MoveToAfterwards, ReturnFromAfterwards, ClearCart"
      - "Query Use Cases: GetCartById, GetCartByCustomerId, SearchCarts"
    techStack:
      - "DDD"
      - "Hexagonal Architecture"
      - "Clean Code"
    metrics:
      - label: "Use Cases"
        value: "8+"
        trend: "up"
        icon: "usecase"
      - label: "Port Interfaces"
        value: "3"
        trend: "stable"
        icon: "interface"
    codeSnippet:
      language: "java"
      filename: "CartCommandUseCase.java"
      code: |
        public interface CartCommandUseCase {
            void createCart(CreateCartCommand command);
            Cart updateCartItems(UpdateCartCommand command);
            void moveItemToAfterwards(CreateAfterwardsCommand command);
            void removeItemFromAfterwards(RemoveAfterwardsCommand command);
            void clearCart(ClearCartCommand command);
        }

  - id: "command-pattern"
    title: "Command Pattern for Cart Operations"
    description: "Command objects: CreateCartCommand, UpdateCartCommand, ClearCartCommand, CreateAfterwardsCommand, RemoveAfterwardsCommand."
    icon: "📋"
    category: "design-pattern"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/application/command"
    highlights:
      - "CreateCartCommand - For creating new cart"
      - "UpdateCartCommand - For updating cart items"
      - "ClearCartCommand - For clearing cart (with reason and exclusions)"
      - "CreateAfterwardsCommand - For moving items to afterwards"
      - "RemoveAfterwardsCommand - For restoring items from afterwards"
    techStack:
      - "Command Pattern"
      - "CQRS-like"
      - "DDD"
    metrics:
      - label: "Command Types"
        value: "5+"
        trend: "stable"
        icon: "command"
    codeSnippet:
      language: "java"
      filename: "UpdateCartCommand.java"
      code: |
        public record UpdateCartCommand(
            CustomerId customerId,
            List<CartItem> items
        ) {
            public static UpdateCartCommand from(String customerId,
                                                  List<CartItemRequest> requests) {
                // Converts requests to domain objects
                return new UpdateCartCommand(...);
            }
        }

  - id: "repository-pattern"
    title: "Repository Pattern with Specifications"
    description: "CartRepository port with CartJpaRepository and CartRepositoryImpl using Specification for dynamic search queries."
    icon: "🗄️"
    category: "persistence"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/output/persistence/repository/CartJpaRepository.java"
    highlights:
      - "CartJpaRepository - Spring Data JPA with entity graph for items/afterwards"
      - "CartRepositoryImpl - Implementation with search using Specification"
      - "CartSpecificationBuilder - Dynamic query building for search"
      - "Entity Graph: CartWithItems, CartWithItemsAndAfterwards for eager loading"
    techStack:
      - "Spring Data JPA"
      - "PostgreSQL 15"
      - "Flyway Migrations"
    metrics:
      - label: "Entity Graphs"
        value: "2"
        trend: "stable"
        icon: "graph"
      - label: "Indexes"
        value: "3"
        trend: "stable"
        icon: "index"
    codeSnippet:
      language: "java"
      filename: "CartJpaRepository.java"
      code: |
        @EntityGraph(attributePaths = {"cartItems", "afterwardItems"})
        Optional<CartModel> findByIdAndCustomerId(String id, String customerId);

        @EntityGraph(value = "CartWithItems")
        Optional<CartModel> findById(String id);

        Page<CartModel> findAll(
            Specification<CartModel> spec, Pageable pageable);

  - id: "redis-caching"
    title: "Redis Caching with Spring Cache"
    description: "Spring Cache abstraction with Redis backend for frequently accessed cart data. Configured via RedisCacheConfig."
    icon: "🗃️"
    category: "cache"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER: Check config/cache/RedisCacheConfig.java"
    highlights:
      - "Spring Cache annotations (@Cacheable, @CacheEvict)"
      - "Redis backend via Spring Data Redis"
      - "Configurable TTL via ADDRESS_SERVICE_CACHE_TTL (default 3600s)"
      - "PLACEHOLDER: @Cacheable annotations needed on repository methods"
    techStack:
      - "Spring Cache"
      - "Redis 7"
      - "Spring Data Redis"
    metrics:
      - label: "Cache TTL"
        value: "3600s"
        trend: "stable"
        icon: "clock"
    codeSnippet:
      language: "java"
      filename: "PLACEHOLDER: RedisCacheConfig.java"
      code: |
        // PLACEHOLDER: Configure Spring Cache with Redis
        // @Bean
        // public RedisCacheManager cacheManager() { ... }

  - id: "rest-controllers"
    title: "REST Controllers (User + Admin)"
    description: "UserCartController for customer operations and CartManagerController for admin operations. Uses ResponseWrapper from libs-kernel."
    icon: "🔌"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/web/controller"
    highlights:
      - "UserCartController - /api/v2/carts/users/* (requires USER role)"
      - "CartManagerController - /api/v2/carts/admin/* (requires ADMIN role)"
      - "Endpoints: getMyCart, updateItems, moveToAfterwards, restoreFromAfterwards"
      - "Admin endpoints: getCustomerCart, getCartById, searchCarts"
      - "PLACEHOLDER: No @RateLimit annotations on controllers"
    techStack:
      - "Spring MVC"
      - "Spring Security"
      - "libs-kernel (ResponseWrapper)"
    metrics:
      - label: "User Endpoints"
        value: "4"
        trend: "stable"
        icon: "user"
      - label: "Admin Endpoints"
        value: "3"
        trend: "stable"
        icon: "admin"
    codeSnippet:
      language: "java"
      filename: "UserCartController.java"
      code: |
        @RestController
        @RequestMapping("/api/v2/carts/users")
        public class UserCartController {
            @GetMapping("/my-cart")
            public ResponseWrapper<CartResponse> getMyCart(
                    @RequestAttribute("userId") String userId) {
                // Returns cart for authenticated user
            }
        }

  - id: "domain-events"
    title: "Domain Events (PLACEHOLDER)"
    description: "CartPurchasedEvent is defined but not published to Kafka yet. Should be published when cart is cleared after successful order."
    icon: "📢"
    category: "events"
    status: "PLACEHOLDER"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/events/CartPurchasedEvent.java"
    highlights:
      - "CartPurchasedEvent - Should be published to cart.purchased topic"
      - "Domain events collected in Cart aggregate via domainEvents list"
      - "PLACEHOLDER: Event publishing not implemented yet"
      - "Would notify other services (e.g., inventory-service) of cart purchase"
    techStack:
      - "Domain Events"
      - "Kafka (PLACEHOLDER)"
    metrics:
      - label: "Event Types"
        value: "1 (PLACEHOLDER)"
        trend: "up"
        icon: "event"
    codeSnippet:
      language: "java"
      filename: "CartPurchasedEvent.java"
      code: |
        // PLACEHOLDER: This event should be published to Kafka
        // when cart is cleared after successful order
        public class CartPurchasedEvent implements DomainEvent {
            // Event data: cartId, customerId, items, total
        }
---
# Project Features

> 12 comprehensive features documented covering DDD, gRPC, Kafka, caching, and design patterns. Has 11 unit test files for domain layer.
> 
> **Potential Issues & Improvements:**
> - No @RateLimit annotations on REST controllers (unlike address-service)
> - CartPurchasedEvent defined but not published to Kafka (PLACEHOLDER)
> - Java 23 may cause compatibility issues with some libraries
> - No integration tests for gRPC endpoints
> - @Cacheable annotations not applied to repository methods
> - Docker Compose missing Kafka dependency for product-events
> - Missing Kubernetes manifests for cloud deployment
> - No CI/CD pipeline (GitHub Actions/Jenkins)
> - Consider adding Circuit Breaker (Resilience4j) for external calls
> - Add Micrometer metrics for cart operations (add/update/clear rates)
