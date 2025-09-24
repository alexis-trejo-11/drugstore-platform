package microservice.order_service.domain.models;

import lombok.Builder;
import lombok.Getter;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.events.OrderCreatedEvent;
import microservice.order_service.domain.models.events.OrderStatusChangedEvent;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.DeliveryAddress;
import microservice.order_service.domain.models.valueobjects.Money;
import microservice.order_service.domain.models.valueobjects.OrderId;
import microservice.order_service.domain.models.valueobjects.ProductId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Builder
@Getter
public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private final DeliveryMethod deliveryMethod;
    private final DeliveryAddress deliveryAddress;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
    
    // Domain events
    private final List<Object> domainEvents;

    private Order(OrderId id, CustomerId customerId, List<OrderItem> items, 
                 DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "Items cannot be null"));
        this.deliveryMethod = Objects.requireNonNull(deliveryMethod, "Delivery method cannot be null");
        this.deliveryAddress = deliveryMethod.requiresAddress() ? 
            Objects.requireNonNull(deliveryAddress, "Delivery address is required for delivery orders") : 
            deliveryAddress;
        this.notes = notes;
        
        validateItems();
        this.totalAmount = calculateTotalAmount();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents = new ArrayList<>();
        
        // Raise domain event
        this.domainEvents.add(new OrderCreatedEvent(this.id, this.customerId, this.totalAmount, this.createdAt));
    }

    public static Order create(CustomerId customerId, List<OrderItem> items, 
                              DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        return new Order(OrderId.generate(), customerId, items, deliveryMethod, deliveryAddress, notes);
    }

    public static Order createWithId(OrderId orderId, CustomerId customerId, List<OrderItem> items, 
                                    DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        return new Order(orderId, customerId, items, deliveryMethod, deliveryAddress, notes);
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus));
        }
        
        OrderStatus oldStatus = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        // Raise domain event
        this.domainEvents.add(new OrderStatusChangedEvent(this.id, oldStatus, newStatus, this.updatedAt));
    }

    public void confirm() {
        changeStatus(OrderStatus.CONFIRMED);
    }

    public void startPreparing() {
        changeStatus(OrderStatus.PREPARING);
    }

    public void markReadyForPickup() {
        if (deliveryMethod != DeliveryMethod.PICKUP) {
            throw new IllegalStateException("Only pickup orders can be marked as ready for pickup");
        }
        changeStatus(OrderStatus.READY_FOR_PICKUP);
    }

    public void markOutForDelivery() {
        if (deliveryMethod != DeliveryMethod.EXPRESS_DELIVERY && deliveryMethod != DeliveryMethod.STANDARD_DELIVERY ) {
            throw new IllegalStateException("Only delivery orders can be marked as out for delivery");
        }
        changeStatus(OrderStatus.OUT_FOR_DELIVERY);
    }

    public void markAsDelivered() {
        changeStatus(OrderStatus.DELIVERED);
    }

    public void markAsPickedUp() {
        changeStatus(OrderStatus.PICKED_UP);
    }

    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel a completed order");
        }

        changeStatus(OrderStatus.CANCELLED);
    }

    public void returnOrder() {
        if (status != OrderStatus.DELIVERED && status != OrderStatus.PICKED_UP) {
            throw new IllegalStateException("Only delivered or picked up orders can be returned");
        }
        changeStatus(OrderStatus.RETURNED);
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDate) {
        if (deliveryMethod == DeliveryMethod.STORE_PICKUP) {
            throw new IllegalStateException("Pickup orders don't have delivery dates");
        }
        this.estimatedDeliveryDate = estimatedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateNotes(String newNotes) {
        this.notes = newNotes;
        this.updatedAt = LocalDateTime.now();
    }

    public Optional<OrderItem> findItem(ProductId productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getTotalItemsCount() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    public boolean isDelivery() {
        return deliveryMethod == DeliveryMethod.DELIVERY;
    }

    public boolean isPickup() {
        return deliveryMethod == DeliveryMethod.PICKUP;
    }

    public boolean canBeCancelled() {
        return !status.isTerminal();
    }

    public boolean canBeReturned() {
        return status == OrderStatus.DELIVERED || status == OrderStatus.PICKED_UP;
    }

    // Domain events handling
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    private void validateItems() {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        // Check for duplicate products
        long uniqueProducts = items.stream()
                .map(OrderItem::getProductId)
                .distinct()
                .count();
        
        if (uniqueProducts != items.size()) {
            throw new IllegalArgumentException("Order cannot have duplicate products");
        }
    }

    public void UpdateStatus(OrderStatus newOrderStatus) {
        this.status = newOrderStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private Money calculateTotalAmount() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money::add)
                .orElseThrow(() -> new IllegalStateException("Cannot calculate total for empty order"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Order{id=%s, customerId=%s, status=%s, totalAmount=%s, itemCount=%d, createdAt=%s}",
                id, customerId, status, totalAmount, items.size(), createdAt);
    }
}
