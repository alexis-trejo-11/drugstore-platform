package microservice.order_service.orders.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
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
    private DeliveryMethod deliveryMethod;
    private OrderStatus status;
    private String notes;

    private String deliveryTrackingNumber;
    private Integer deliveryAttempt;
    private Integer daysSinceReadyForPickup;

    // Numeric Values
    private Money shippingCost;
    private Money taxAmount;
    private Currency orderCurrency;

    // Relationships
    private User user;
    private DeliveryAddress deliveryAddress;
    private PaymentID paymentID;

    // TimeStamps
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItem> items;

    public static Order create(DeliveryMethod deliveryMethod, String notes,
                               Money shippingCost, Money taxAmount,
                               User user, DeliveryAddress address,
                               Currency orderCurrency) {
        Order order = new Order();
        OrderID orderID = OrderID.generate();
        Currency currency = validateAndGetCurrency(shippingCost, taxAmount, orderCurrency);

        order.id = orderID;
        order.deliveryMethod = deliveryMethod;
        order.status = OrderStatus.PENDING;
        order.notes = notes;
        order.orderCurrency = currency;
        order.taxAmount = taxAmount;
        order.shippingCost = shippingCost;
        order.user = user;
        order.deliveryAddress = address;
        order.deliveryAttempt = 0;
        order.daysSinceReadyForPickup = 0;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        return order;
    }


    private static Currency validateAndGetCurrency(Money shippingCost, Money taxAmount, Currency defaultCurrency) {
        Currency currency = defaultCurrency;

        if (shippingCost != null) {
            currency = shippingCost.currency();
        } else if (taxAmount != null) {
            currency = taxAmount.currency();
        }

        if (shippingCost != null && !shippingCost.currency().equals(currency)) {
            throw new CurrencyMismatchException("Shipping cost currency mismatch");
        }
        if (taxAmount != null && !taxAmount.currency().equals(currency)) {
            throw new CurrencyMismatchException("Tax amount currency mismatch");
        }

        return currency;
    }

    public void assignItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            item.assignOrder(this.id);
        }

        this.items = new ArrayList<>(items);
        validateItems();

        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus));
        }

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

    }

    public void confirm(PaymentID paymentID) {
        this.paymentID = paymentID;
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

    public void markOutForDelivery(String deliveryTrackingNumber) {
        if (deliveryMethod != DeliveryMethod.EXPRESS_DELIVERY && deliveryMethod != DeliveryMethod.STANDARD_DELIVERY ) {
            throw new IllegalStateException("Only delivery orders can be marked as out for delivery");
        }
        changeStatus(OrderStatus.OUT_FOR_DELIVERY);

        this.deliveryTrackingNumber = deliveryTrackingNumber;
    }

    public void complete() {
        if (deliveryMethod == DeliveryMethod.STORE_PICKUP) {
            markAsPickedUp();
        } else {
            markAsDelivered();
        }
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

    public void returnOrder(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Return reason must be provided");
        }

        if (reason.equals("Customer Not Attended")) {
            deliveryAttempt++;
        }

        // Cancel order if more than 3 delivery attempts. TODO: Notify customer
        if (deliveryAttempt > 3) {
            cancel("Max delivery attempts reached");
            return;
        }

        if (status != OrderStatus.DELIVERED && status != OrderStatus.PICKED_UP) {
            throw new IllegalStateException("Only delivered or picked up orders can be returned");
        }

        changeStatus(OrderStatus.RETURNED);
        deliveryTrackingNumber = null;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDate) {
        if (deliveryMethod == DeliveryMethod.STORE_PICKUP) {
            throw new IllegalStateException("Pickup orders don't have delivery dates");
        }
        this.estimatedDeliveryDate = estimatedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public Optional<OrderItem> findItem(ProductID productID) {
        return items.stream()
                .filter(item -> item.getProductID().equals(productID))
                .findFirst();
    }

    public void updateDeliveryAddress(DeliveryAddress newAddress) {
        if (deliveryMethod != DeliveryMethod.STANDARD_DELIVERY && deliveryMethod != DeliveryMethod.EXPRESS_DELIVERY) {
            throw new IllegalStateException("Cannot update address for non-delivery orders");
        }

        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Can only update address for PENDING or CONFIRMED orders");
        }

        if (newAddress == null) {
            throw new IllegalArgumentException("New address cannot be null");
        }

        this.deliveryAddress = newAddress;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDeliveryMethod(DeliveryMethod newMethod) {
        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Can only update delivery method for PENDING or CONFIRMED orders");
        }

        if (newMethod == null) {
            throw new IllegalArgumentException("New delivery method cannot be null");
        }

        if (newMethod == this.deliveryMethod) {
            throw new IllegalArgumentException("New delivery method must be different from current method");
        }

        // If changing to a delivery method, ensure address is set
        if (newMethod.requiresAddress() && this.deliveryAddress == null) {
            throw new IllegalStateException("Cannot change to a delivery method without a valid address");
        }

        this.deliveryMethod = newMethod;
        this.updatedAt = LocalDateTime.now();
    }

    public void readyForPickup() {
        if (deliveryMethod != DeliveryMethod.STORE_PICKUP) {
            throw new IllegalStateException("Only pickup orders can be marked as ready for pickup");
        }
        changeStatus(OrderStatus.READY_FOR_PICKUP);
        this.daysSinceReadyForPickup = 0;
    }

    public List<OrderItem> getItems() {
        if (items == null)  return Collections.emptyList();

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

    private void validateItems() {
        if (items == null || items.isEmpty()) {
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
        if (items == null || items.isEmpty()) return Money.zero(orderCurrency);

        Money itemsTotal = items.stream()
                .filter(Objects::nonNull)
                .map(OrderItem::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(Money.zero(orderCurrency), Money::add);

        return itemsTotal
                .add(shippingCost != null ? shippingCost : Money.zero(orderCurrency))
                .add(taxAmount != null ? taxAmount : Money.zero(orderCurrency));
    }

    public Money getTotalAmount() {
        if (items.isEmpty()) {
            return Money.zero();
        }
        return calculateTotalAmount();
    }

    public AddressID getAddressID() {
        return deliveryAddress != null ? deliveryAddress.getId() : null;
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

    public boolean isOngoing() {
        return status.isActive();
    }
}