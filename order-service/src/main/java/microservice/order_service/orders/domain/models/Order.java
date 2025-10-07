package microservice.order_service.orders.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.events.OrderCreatedEvent;
import microservice.order_service.orders.domain.models.events.OrderStatusChangedEvent;
import microservice.order_service.orders.domain.models.valueobjects.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private OrderID id;
    private List<OrderItem> items;
    private DeliveryMethod deliveryMethod;
    private AddressID addressID;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
    private DeliveryAddress deliveryAddress;
    private User user;
    private List<Object> domainEvents;

    public static Order create(User user, List<OrderItem> items,
                               DeliveryMethod deliveryMethod, AddressID addressID, String notes) {
        OrderID orderID = OrderID.generate();
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order();
        order.id = orderID;
        order.user = user;
        order.items = new ArrayList<>(items);
        order.deliveryMethod = deliveryMethod;
        order.addressID = addressID;
        order.status = OrderStatus.PENDING;
        order.createdAt = now;
        order.updatedAt = now;
        order.notes = notes;
        order.validateItems();
        order.domainEvents = new ArrayList<>();
        // Raise domain event
        order.domainEvents.add(new OrderCreatedEvent(orderID, user.getId(),order.calculateTotalAmount(), now));
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

    public Optional<OrderItem> findItem(ProductID productID) {
        return items.stream()
                .filter(item -> item.getProductID().equals(productID))
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
                .map(OrderItem::getProductID)
                .distinct()
                .count();

        if (uniqueProducts != items.size()) {
            throw new IllegalArgumentException("Order cannot have duplicate products");
        }
    }

    public void updateStatus(OrderStatus newOrderStatus) {
        this.status = newOrderStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private Money calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return Money.of(BigDecimal.ZERO, Currency.getInstance("MXN"));
        }

        return items.stream()
                .filter(Objects::nonNull)
                .map(OrderItem::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(Money.of(BigDecimal.ZERO, Currency.getInstance("MXN")), Money::add);
    }

    public Money getTotalAmount() {
        if (items.isEmpty()) {
            return Money.of(BigDecimal.ZERO , Currency.getInstance("MXN"));
        }
        return calculateTotalAmount();
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
        return String.format("Order{id=%s, userID=%s, status=%s, totalAmount=%s, itemCount=%d, createdAt=%s}",
                id, user.getId(), status, calculateTotalAmount(), items.size(), createdAt);
    }
}