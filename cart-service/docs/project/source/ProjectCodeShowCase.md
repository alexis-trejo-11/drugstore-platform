---
codeExamples:
  - id: "cart-aggregate-root"
    title: "Cart Aggregate Root Pattern"
    description: "Cart.java is the aggregate root encapsulating items and afterwardsItems with business logic for add/update/remove/clear operations"
    category: "Domain"
    duration: "15 min read"
    views: 0
    tags:
      - "DDD"
      - "Aggregate Root"
      - "Domain Model"
      - "Business Logic"
    files:
      - name: "Cart.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/Cart.java"
        language: "java"
        content: |
          /**
           * Cart aggregate root - represents a customer's shopping cart.
           * This is the main domain entity with comprehensive business logic.
           */
          public class Cart {
            private static final int MAX_ITEMS_PER_CART = 100;

            private CartId id;
            private CustomerId customerId;
            private List<CartItem> items;
            private List<AfterwardsItem> afterwardsItems;
            private CartTimeStamps timeStamps;

            /**
             * Creates a new Cart for a customer with generated ID.
             */
            public static Cart create(CreateCartParams params) {
                CartValidation.requireNonNull(params, "CreateCartParams");
                CartValidation.requireNonNull(params.customerId(), "Customer ID");

                Cart cart = new Cart();
                cart.id = CartId.generate();
                cart.customerId = params.customerId();

                log.info("Created new Cart: id={}, customerId={}", cart.id, cart.customerId);
                return cart;
            }

            /**
             * Adds a single item to the cart. If item exists, quantities are merged.
             */
            public void addItem(CartItem item) {
                CartValidation.requireNonNull(item, "Cart item");

                Optional<CartItem> existingItem = findItemByProductId(item.getProductId());

                if (existingItem.isPresent()) {
                    existingItem.get().mergeWith(item);
                } else {
                    validateCanAddItem();
                    items.add(item);
                }

                timeStamps.markAsUpdated();
            }

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

            public ItemPrice calculateTotal() {
                if (items.isEmpty()) return ItemPrice.zero();
                return items.stream()
                    .map(CartItem::calculateTotal)
                    .reduce(ItemPrice.zero(), ItemPrice::add);
            }
          }
        highlighted: true
        explanation: "Aggregate root pattern: Cart encapsulates all business logic for item management, enforces invariants (max 100 items), and handles afterwards feature."

  - id: "value-objects"
    title: "Value Objects Pattern"
    description: "Strongly-typed values: CartId (UUID), CustomerId, ProductId, Quantity (with validation), ItemPrice (BigDecimal wrapper)"
    category: "Domain"
    duration: "8 min read"
    views: 0
    tags:
      - "DDD"
      - "Value Objects"
      - "Type Safety"
      - "Validation"
    files:
      - name: "Quantity.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/valueobjects/Quantity.java"
        language: "java"
        content: |
          public record Quantity(int value) {
            public Quantity {
                if (value < 0) {
                    throw new InvalidQuantityException("Quantity cannot be negative: " + value);
                }
                if (value == 0) {
                    throw new InvalidQuantityException("Quantity cannot be zero");
                }
                this.value = value;
            }

            public static Quantity of(int value) {
                return new Quantity(value);
            }

            public Quantity add(Quantity other) {
                return new Quantity(this.value + other.value);
            }

            public Quantity subtract(Quantity other) {
                return new Quantity(this.value - other.value);
            }
          }
        highlighted: true
        explanation: "Value object with validation in canonical constructor, factory method, and arithmetic operations."

      - name: "CartId.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/valueobjects/CartId.java"
        language: "java"
        content: |
          public class CartId extends AbstractId {
            public CartId(String value) {
                super(value);
            }

            public static CartId generate() {
                return new CartId(UUID.randomUUID().toString());
            }
          }
        highlighted: false
        explanation: "Extends AbstractId, provides factory method for UUID generation."

  - id: "grpc-service"
    title: "gRPC Service for Inter-Service Communication"
    description: "CartGrpcService exposes GetUserCart and ClearCart endpoints for order-service integration during checkout"
    category: "Communication"
    duration: "10 min read"
    views: 0
    tags:
      - "gRPC"
      - "Protobuf"
      - "Microservices"
      - "Checkout Flow"
    files:
      - name: "CartGrpcService.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/grpc/CartGrpcService.java"
        language: "java"
        content: |
          @GrpcService
          public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {

            private final CartQueryUseCase cartQueryUseCase;
            private final CartCommandUseCase cartCommandUseCase;
            private final CartGrpcMapper mapper;

            @Override
            public void getUserCart(GetUserCartRequest request,
                                        StreamObserver<CartResponse> responseObserver) {
                try {
                    log.info("gRPC GetUserCart called for userId: {}", request.getUserId());

                    GetCartByCustomerIdQuery query =
                        GetCartByCustomerIdQuery.from(request.getUserId());
                    Cart cart = cartQueryUseCase.getCartByCustomerId(query);

                    CartResponse response = mapper.toGrpcResponse(cart);

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();

                } catch (Exception e) {
                    log.error("Error in gRPC GetUserCart", e);
                    responseObserver.onError(e);
                }
            }

            @Override
            public void clearCart(ClearCartRequest request,
                                   StreamObserver<ClearCartResponse> responseObserver) {
                // Converts request, creates ClearCartCommand, executes
                // Returns ClearCartResponse with success/failure
            }
          }
        highlighted: true
        explanation: "gRPC service extends generated base class, uses Protobuf messages, calls use cases."

      - name: "CartGrpcMapper.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/grpc/mapper/CartGrpcMapper.java"
        language: "java"
        content: |
          @Component
          public class CartGrpcMapper {
            public CartResponse toGrpcResponse(Cart cart) {
                return CartResponse.newBuilder()
                    .setId(cart.getId().value())
                    .setCustomerId(cart.getCustomerId().value())
                    .addAllItems(toGrpcCartItems(cart.getItems()))
                    .build();
            }
          }
        highlighted: false
        explanation: "Maps between domain objects and Protobuf messages."

  - id: "kafka-consumer"
    title: "Kafka Product Event Consumer"
    description: "ProductEventConsumer listens to product-events topic and updates cart items via ProductEventHandler"
    category: "Messaging"
    duration: "7 min read"
    views: 0
    tags:
      - "Kafka"
      - "Events"
      - "Product Updates"
      - "Event-Driven"
    files:
      - name: "ProductEventConsumer.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/product/adapter/input/message/kafka/ProductEventConsumer.java"
        language: "java"
        content: |
          @Component
          public class ProductEventConsumer {
            private final ProductEventHandler eventHandler;

            @KafkaListener(topics = "product-events",
                          groupId = "${spring.kafka.consumer.group-id}")
            public void consume(ProductEvent event) {
                log.info("Received product event: type={}, id={}",
                          event.getEventType(), event.getPayload().getId());
                eventHandler.handle(event);
            }
          }
        highlighted: true
        explanation: "Listens to product-events topic, delegates to ProductEventHandler for processing."

      - name: "ProductEventHandler.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/product/adapter/input/message/ProductEventHandler.java"
        language: "java"
        content: |
          @Service
          @Slf4j
          public class ProductEventHandler {
            // Handles product update/delete events
            // Updates cart items with new prices or removes unavailable products
          }
        highlighted: false
        explanation: "Handles product events: price changes, availability updates, product deletions."

  - id: "command-pattern"
    title: "Command Pattern for Cart Operations"
    description: "Command objects: CreateCartCommand, UpdateCartCommand, ClearCartCommand, CreateAfterwardsCommand, RemoveAfterwardsCommand"
    category: "Design Patterns"
    duration: "6 min read"
    views: 0
    tags:
      - "Command Pattern"
      - "CQRS-like"
      - "DDD"
    files:
      - name: "UpdateCartCommand.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/application/command/UpdateCartCommand.java"
        language: "java"
        content: |
          public record UpdateCartCommand(
              CustomerId customerId,
              List<CartItem> items
          ) {
            public static UpdateCartCommand from(String customerId,
                                              List<CartItemRequest> requests) {
                List<CartItem> items = requests.stream()
                    .map(req -> CartItem.create(
                        new ReconstructCartItemParams(
                            null, null,
                            new ProductId(req.productId()),
                            req.productName(),
                            new ItemPrice(req.unitPrice()),
                            Quantity.of(req.quantity()),
                            new ItemPrice(req.discountPerUnit())
                        )))
                    .toList();

                return new UpdateCartCommand(
                    new CustomerId(customerId), items);
            }
          }
        highlighted: true
        explanation: "Command object encapsulating update request with domain object conversion."

  - id: "domain-events"
    title: "Domain Events (PLACEHOLDER)"
    description: "CartPurchasedEvent is defined but not published to Kafka yet. Should be published when cart is cleared after successful order."
    category: "Domain"
    duration: "5 min read"
    views: 0
    tags:
      - "Domain Events"
      - "Kafka"
      - "PLACEHOLDER"
    files:
      - name: "CartPurchasedEvent.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/events/CartPurchasedEvent.java"
        language: "java"
        content: |
          // PLACEHOLDER: This event should be published to Kafka
          // when cart is cleared after successful order
          public class CartPurchasedEvent implements DomainEvent {
              // Event data: cartId, customerId, items, total, timestamp
          }
        highlighted: true
        explanation: "Domain event that should be published to Kafka topic (e.g., cart.purchased) after successful order."
---
# CodeShowCase

> 6 comprehensive code examples covering DDD, gRPC, Kafka, and design patterns. Has 11 unit test files for domain layer. PLACEHOLDER issues: CartPurchasedEvent not published to Kafka, @RateLimit not applied to REST endpoints, no integration tests for gRPC. Potential additions: Circuit Breaker for external calls, caching annotations (@Cacheable), event publishing implementation.
