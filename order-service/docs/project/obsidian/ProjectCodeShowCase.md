---
codeExamples:
  - id: "order-domain-model"
    title: "Order Aggregate Root - Rich Domain Model"
    description: "The Order class serves as the aggregate root, encapsulating all business logic, state transitions, and validation rules following DDD principles."
    category: "Domain Model"
    duration: "10 min read"
    views: 0
    tags:
      - "DDD"
      - "Aggregate Root"
      - "Business Logic"
      - "Java 23"
    files:
      - name: "Order.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/Order.java"
        language: "java"
        content: |
          @Builder
          @Getter
          @NoArgsConstructor
          @AllArgsConstructor
          public class Order {
              private OrderID id;
              private DeliveryMethod deliveryMethod;
              private OrderStatus status;
              private String notes;
              private Money taxFee;
              private Money serviceFee;
              private PickupInfo pickupInfo;
              private DeliveryInfo deliveryInfo;
              private List<OrderItem> items;
              private UserID userID;
              private PaymentID paymentID;
              private OrderTimestamps orderTimestamps;
              private Currency orderCurrency;
              public final static Currency DEFAULT_CURRENCY = Currency.getInstance("MXN");

              public static Order create(UserID userID, DeliveryMethod deliveryMethod,
                  String notes, Money serviceFee, Money taxAmount, List<OrderItem> items) {
                  validateCreateParameters(userID, deliveryMethod, serviceFee, taxAmount, items);
                  Currency validatedCurrency = validateAndGetCurrency(serviceFee, taxAmount, DEFAULT_CURRENCY);

                  Order order = new Order();
                  order.id = OrderID.generate();
                  order.userID = userID;
                  order.deliveryMethod = deliveryMethod;
                  order.status = OrderStatus.PENDING;
                  order.notes = notes;
                  order.orderCurrency = validatedCurrency;
                  order.taxFee = taxAmount;
                  order.serviceFee = serviceFee;
                  order.items = new ArrayList<>();
                  order.orderTimestamps = OrderTimestamps.create();
                  order.items = new ArrayList<>(items);
                  order.assignItems(items);
                  return order;
              }

              public void confirm(PaymentID paymentID, LocalDateTime estimatedDeliveryDate) {
                  if (estimatedDeliveryDate != null && estimatedDeliveryDate.isBefore(LocalDateTime.now())) {
                      throw new InvalidOrderDataException("Estimated delivery date cannot be in the past");
                  }
                  if (paymentID == null) {
                      throw new InvalidOrderDataException("Payment ID cannot be null when confirming");
                  }
                  this.paymentID = paymentID;
                  changeStatus(OrderStatus.CONFIRMED);
              }

              public void changeStatus(OrderStatus newStatus) {
                  if (!this.status.canTransitionTo(newStatus)) {
                      throw new IllegalStateException(String.format("Cannot transition from %s to %s", this.status, newStatus));
                  }
                  this.status = newStatus;
                  this.orderTimestamps.orderUpdated();
              }

              public void complete() {
                  if (deliveryMethod == DeliveryMethod.STORE_PICKUP) {
                      markAsPickedUp();
                  } else {
                      markAsDelivered();
                  }
              }
          }
        highlighted: true
        explanation: "Rich domain model with factory method, state transitions, and business rule validation"

  - id: "order-status-state-machine"
    title: "Order Status State Machine"
    description: "Enum-based state machine defining valid order status transitions with clear business rules."
    category: "State Machine"
    duration: "5 min read"
    views: 0
    tags:
      - "State Pattern"
      - "Enum"
      - "Business Rules"
      - "Java 23"
    files:
      - name: "OrderStatus.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/enums/OrderStatus.java"
        language: "java"
        content: |
          @Getter
          public enum OrderStatus {
              PENDING("pending", "Order has been placed but not yet processed"),
              CONFIRMED("confirmed", "Order has been confirmed and is being prepared"),
              PREPARING("preparing", "Order is being prepared for delivery/pickup"),
              READY_FOR_PICKUP("ready_for_pickup", "Order is ready to be picked up"),
              OUT_FOR_DELIVERY("out_for_delivery", "Order is out for delivery"),
              DELIVERED("delivered", "Order has been delivered"),
              PICKED_UP("picked_up", "Order has been picked up by customer"),
              CANCELLED("cancelled", "Order has been cancelled"),
              RETURNED("returned", "Order has been returned");

              private final String code;
              private final String description;

              public boolean canTransitionTo(OrderStatus newStatus) {
                  return switch (this) {
                      case PENDING -> Arrays.asList(CONFIRMED, CANCELLED).contains(newStatus);
                      case CONFIRMED -> Arrays.asList(PREPARING, CANCELLED).contains(newStatus);
                      case PREPARING -> Arrays.asList(READY_FOR_PICKUP, OUT_FOR_DELIVERY, CANCELLED).contains(newStatus);
                      case READY_FOR_PICKUP -> Arrays.asList(PICKED_UP, CANCELLED).contains(newStatus);
                      case OUT_FOR_DELIVERY -> Arrays.asList(DELIVERED, CANCELLED, RETURNED).contains(newStatus);
                      case RETURNED -> Arrays.asList(OUT_FOR_DELIVERY, CANCELLED).contains(newStatus);
                      case DELIVERED, PICKED_UP -> Objects.equals(RETURNED, newStatus);
                      case CANCELLED -> false;
                  };
              }

              public boolean isTerminal() {
                  return this == DELIVERED || this == PICKED_UP || this == CANCELLED || this == RETURNED;
              }
          }
        highlighted: true
        explanation: "Switch expression with enhanced enum pattern for type-safe state transitions"

  - id: "hexagonal-architecture-ports"
    title: "Hexagonal Architecture - Ports Definition"
    description: "Domain ports (interfaces) define contracts for input and output adapters, following hexagonal architecture principles."
    category: "Architecture"
    duration: "8 min read"
    views: 0
    tags:
      - "Hexagonal Architecture"
      - "Ports and Adapters"
      - "Interfaces"
      - "DDD"
    files:
      - name: "OrderApplicationFacade.java"
        path: "src/main/java/microservice/order_service/orders/domain/ports/input/OrderApplicationFacade.java"
        language: "java"
        content: |
          package microservice.order_service.orders.core.ports.input;

          public interface OrderApplicationFacade extends OrderCommandService, OrderQueryService {
          }
        highlighted: false
        explanation: "Facade interface combines command and query service ports"

      - name: "OrderCommandService.java"
        path: "src/main/java/microservice/order_service/orders/domain/ports/input/OrderCommandService.java"
        language: "java"
        content: |
          public interface OrderCommandService {
              CreateOrderCommandResponse createDeliveryOrder(CreateDeliveryOrderCommand command);
              CreateOrderCommandResponse createPickupOrder(CreatePickupOrderCommand command);
              void updateDeliveryAddress(UpdateOrderAddressCommand command);
              void updateDeliverMethod(UpdateOrderDeliverMethodCommand command);
              void deleteOrder(DeleteOrderCommand command);

              // Common Status Updates
              UpdateOrderStatusCommandResult confirmOrder(ConfirmOrderCommand command);
              UpdateOrderStatusCommandResult startPreparingOrder(PrepareOrderCommand command);
              UpdateOrderStatusCommandResult completeOrder(CompleteOrderCommand command);

              // Shipping and Delivery
              UpdateOrderStatusCommandResult shipOrder(ShipOrderCommand command);
              UpdateOrderStatusCommandResult returnOrder(OrderDeliverFailCommand command);
              CancelOrderCommandResponse cancelOrder(CancelOrderCommand command);

              // Pickup and In-Store Orders
              UpdateOrderStatusCommandResult readyForPickupOrder(OrderReadyToPickupCommand command);
          }
        highlighted: true
        explanation: "Input port defining all command operations the application supports"

      - name: "EventPublisher.java"
        path: "src/main/java/microservice/order_service/orders/domain/ports/output/EventPublisher.java"
        language: "java"
        content: |
          package microservice.order_service.orders.core.ports.output;

          public interface EventPublisher {
              void publish(Object event);
          }
        highlighted: false
        explanation: "Output port for publishing domain events to event bus"

  - id: "rest-controller-example"
    title: "REST Controller with OpenAPI Annotations"
    description: "SaleOrderController demonstrates REST API design with OpenAPI annotations, role-based security, and request/response mapping."
    category: "API Design"
    duration: "7 min read"
    views: 0
    tags:
      - "REST API"
      - "Spring Boot"
      - "OpenAPI"
      - "Swagger"
    files:
      - name: "SaleOrderController.java"
        path: "src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
        language: "java"
        content: |
          @Tag(name = "Orders", description = "Endpoints for complete purchaseOrder lifecycle management")
          @SecurityRequirement(name = "bearerAuth")
          @RestController
          @RequiredArgsConstructor
          @RequestMapping(value = "/api/v2/sale-orders", produces = "application/json")
          public class SaleOrderController {

              private final OrderApplicationFacade orderService;
              private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;
              private final EntityDetailMapper<OrderDetailResult, OrderDetailResponse> detailMapper;

              @SearchOrdersOperation
              @GetMapping("/search")
              public ResponseWrapper<PageResponse<OrderResponse>> searchOrders(
                  @Valid OrderSearchRequest request) {
                  SearchOrdersQuery query = SearchOrdersQuery.fromRequest(request);
                  Page<OrderQueryResult> resultPage = orderService.searchOrders(query);
                  PageResponse<OrderResponse> response = mapper.toResponsePage(resultPage);
                  return ResponseWrapper.success(response, "Orders found successfully");
              }

              @CreateOrderOperation
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
          }
        highlighted: true
        explanation: "Clean controller with custom OpenAPI annotations and unified response wrapper"

  - id: "decorator-pattern-caching"
    title: "Decorator Pattern for Cross-Cutting Concerns"
    description: "CachingUserServiceDecorator demonstrates the Decorator pattern to add caching behavior transparently."
    category: "Design Patterns"
    duration: "6 min read"
    views: 0
    tags:
      - "Decorator Pattern"
      - "Caching"
      - "Spring Cache"
      - "Redis"
    files:
      - name: "CachingUserServiceDecorator.java"
        path: "src/main/java/microservice/order_service/external/users/application/service/decorator/CachingUserServiceDecorator.java"
        language: "java"
        content: |
          @RequiredArgsConstructor
          @Service
          public class CachingUserServiceDecorator implements UserService {

              private final UserService delegate;
              private final CacheManager cacheManager;

              @Override
              public User getUserByID(String userID) {
                  Cache cache = cacheManager.getCache("users");
                  if (cache != null) {
                      Cache.ValueWrapper wrapper = cache.get(userID);
                      if (wrapper != null) {
                          return (User) wrapper.get();
                      }
                  }

                  User user = delegate.getUserByID(userID);

                  if (cache != null && user != null) {
                      cache.put(userID, user);
                  }
                  return user;
              }

              @Override
              public void clearCache(String userID) {
                  Cache cache = cacheManager.getCache("users");
                  if (cache != null) {
                      cache.evict(userID);
                  }
              }
          }
        highlighted: true
        explanation: "Decorator adds caching transparently without modifying the original UserService"

  - id: "domain-events"
    title: "Domain Events for Event-Driven Architecture"
    description: "Domain events (OrderCreatedEvent, OrderStatusChangedEvent) enable loose coupling and async processing."
    category: "Event-Driven"
    duration: "5 min read"
    views: 0
    tags:
      - "Domain Events"
      - "DDD"
      - "Event-Driven"
      - "Decoupling"
    files:
      - name: "OrderCreatedEvent.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/events/OrderCreatedEvent.java"
        language: "java"
        content: |
          public record OrderCreatedEvent(
              OrderID orderId,
              UserID userID,
              Money totalAmount,
              LocalDateTime createdAt
          ) {
              public OrderCreatedEvent {
                  if (orderId == null) throw new IllegalArgumentException("Order ID cannot be null");
                  if (userID == null) throw new IllegalArgumentException("UserID cannot be null");
                  if (totalAmount == null) throw new IllegalArgumentException("Total amount cannot be null");
                  if (createdAt == null) throw new IllegalArgumentException("Created at cannot be null");
              }
          }
        highlighted: true
        explanation: "Java 23 record with validation in canonical constructor for immutable domain event"

      - name: "OrderStatusChangedEvent.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/events/OrderStatusChangedEvent.java"
        language: "java"
        content: |
          public record OrderStatusChangedEvent(
              OrderID orderID,
              OrderStatus oldStatus,
              OrderStatus newStatus,
              LocalDateTime changedAt
          ) {
              public OrderStatusChangedEvent {
                  if (orderID == null) throw new IllegalArgumentException("Order ID cannot be null");
                  if (oldStatus == null) throw new IllegalArgumentException("Old status cannot be null");
                  if (newStatus == null) throw new IllegalArgumentException("New status cannot be null");
                  if (changedAt == null) throw new IllegalArgumentException("Changed at cannot be null");
              }
          }
        highlighted: false
        explanation: "Event capturing status transition for async notification and audit trail"
---
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
