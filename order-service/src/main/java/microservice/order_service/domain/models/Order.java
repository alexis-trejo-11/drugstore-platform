package microservice.order_service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private List<OrderItem> items;
    private Money totalAmount;
    private DeliveryMethod deliveryMethod;
    private DeliveryAddress deliveryAddress;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
    private List<Object> domainEvents;


    public static Order create(CustomerId customerId, List<OrderItem> items,
                               DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        OrderId orderId = OrderId.generate();
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order();
        order.id = orderId;
        order.customerId = customerId;
        order.items = new ArrayList<>(items);
        order.totalAmount = order.calculateTotalAmount();
        order.deliveryMethod = deliveryMethod;
        order.deliveryAddress = deliveryAddress;
        order.status = OrderStatus.PENDING;
        order.createdAt = now;
        order.updatedAt = now;
        order.notes = notes;
        order.validateItems();
        order.domainEvents = new ArrayList<>();
        // Raise domain event
        order.domainEvents.add(new OrderCreatedEvent(orderId, customerId,order.totalAmount, now));
        return order;
    }

    public static Order createWithId(OrderId orderId, CustomerId customerId, List<OrderItem> items,
                                     DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order();
        order.id = orderId;
        order.customerId = customerId;
        order.items = new ArrayList<>(items);
        order.totalAmount = order.calculateTotalAmount();
        order.deliveryMethod = deliveryMethod;
        order.deliveryAddress = deliveryAddress;
        order.status = OrderStatus.PENDING;
        order.createdAt = now;
        order.updatedAt = now;
        order.notes = notes;
        order.validateItems();
        order.domainEvents = new ArrayList<>();
        // Raise domain event
        order.domainEvents.add(new OrderCreatedEvent(orderId, customerId, order.totalAmount, now));
        return order;
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
        if (deliveryMethod != DeliveryMethod.STORE_PICKUP) {
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
        return deliveryMethod == DeliveryMethod.STANDARD_DELIVERY;
    }
    public boolean isPickup() {
        return deliveryMethod == DeliveryMethod.STORE_PICKUP;
    }
    public boolean canBeCancelled() {
        return !status.isTerminal();
    }
    public boolean canBeReturned() {
        return status == OrderStatus.DELIVERED || status == OrderStatus.PICKED_UP;
    }

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

    public void updateStatus(OrderStatus newOrderStatus) { // Fixed method name capitalization
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