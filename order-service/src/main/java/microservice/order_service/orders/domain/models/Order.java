package microservice.order_service.orders.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.application.exceptions.CurrencyMismatchException;
import microservice.order_service.orders.domain.models.exceptions.*;
import microservice.order_service.orders.domain.models.valueobjects.*;

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
    private UserID userID;
    private AddressID addressID;
    private PaymentID paymentID;

    // TimeStamps
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItem> items = new ArrayList<>();

    public static Order create(
            UserID userID,
            DeliveryMethod deliveryMethod,
            AddressID addressID,
            String notes,
            Money shippingCost,
            Money taxAmount,
            Currency orderCurrency
    ) {

        validateCreateParameters(userID, deliveryMethod, addressID, shippingCost, taxAmount, orderCurrency);
        Currency validatedCurrency = validateAndGetCurrency(shippingCost, taxAmount, orderCurrency);

        Order order = new Order();
        order.id = OrderID.generate();
        order.userID = userID;
        order.deliveryMethod = deliveryMethod;
        order.addressID = addressID;
        order.status = OrderStatus.PENDING;
        order.notes = notes;
        order.orderCurrency = validatedCurrency;
        order.taxAmount = taxAmount;
        order.shippingCost = shippingCost;
        order.deliveryAttempt = 0;
        order.daysSinceReadyForPickup = 0;
        order.items = new ArrayList<>();
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

    private static void validateCreateParameters(
            UserID userID,
            DeliveryMethod deliveryMethod,
            AddressID deliveryAddressID,
            Money shippingCost,
            Money taxAmount,
            Currency orderCurrency) {

        if (userID == null) {
            throw new InvalidOrderDataException("User ID cannot be null");
        }

        if (deliveryMethod == null) {
            throw new InvalidOrderDataException("Delivery method cannot be null");
        }

        if (deliveryMethod.requiresAddress() && deliveryAddressID == null) {
            throw new MissingDeliveryAddressException();
        }

        if (orderCurrency == null && shippingCost == null && taxAmount == null) {
            throw new InvalidOrderDataException("At least one currency reference must be provided");
        }
    }

    public void assignItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new EmptyOrderException();
        }

        validateNoDuplicateProducts(items);

        for (OrderItem item : items) {
            item.assignOrder(this.id);
        }

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

    public void confirm(PaymentID paymentID, LocalDateTime estimatedDeliveryDate) {
        this.paymentID = paymentID;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
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

        if (reason.equals("CustomerNotAvailable") || reason.equals("TimeoutWindowMissed")) {
            deliveryAttempt++;
        }

        // Cancel order if more than 3 delivery attempts. TODO: Notify customer
        if (deliveryAttempt > 3) {
            cancel("Max delivery attempts reached");
            return;
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

    public void updateDeliveryAddress(AddressID addressID) {
        if (deliveryMethod != DeliveryMethod.STANDARD_DELIVERY && deliveryMethod != DeliveryMethod.EXPRESS_DELIVERY) {
            throw new IllegalStateException("Cannot update address for non-delivery orders");
        }

        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Can only update address for PENDING or CONFIRMED orders");
        }

        if (addressID == null) {
            throw new IllegalArgumentException("New address ID cannot be null");
        }

        this.addressID = addressID;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDeliveryMethod(DeliveryMethod newMethod, AddressID newAddressID) {
        if (!canModifyDeliveryMethod()) {
            throw new InvalidOrderStateTransitionException(status, status);
        }

        if (newMethod == null) {
            throw new InvalidOrderDataException("New delivery method cannot be null");
        }

        if (newMethod == this.deliveryMethod) {
            throw new DeliveryMethodMismatchException("New delivery method must be different from current method");
        }

        // If changing to a delivery method, ensure address is provided
        if (newMethod.requiresAddress() && newAddressID == null) {
            throw new MissingDeliveryAddressException();
        }

        this.deliveryMethod = newMethod;
        this.addressID = newAddressID;
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

    public boolean canModifyDeliveryMethod() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    private void validateNoDuplicateProducts(List<OrderItem> items) {
        Set<ProductID> productIds = new HashSet<>();
        for (OrderItem item : items) {
            if (!productIds.add(item.getProductID())) {
                throw new DuplicateProductInOrderException(item.getProductID());
            }
        }
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
        if (!hasItems()) {
            return Money.zero(orderCurrency);
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
                id, userID, status, calculateTotalAmount(), items.size(), createdAt);
    }

    public boolean isOngoing() {
        return status.isActive();
    }
}