package microservice.order_service.orders.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.application.exceptions.OrderValidationException;
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
    private Money taxFee;

    private PickupInfo pickupInfo;
    private DeliveryInfo deliveryInfo;
    private List<OrderItem> items;

    private UserID userID;
    private PaymentID paymentID;

    private OrderTimestamps orderTimestamps;

    private Currency orderCurrency;
    public final static Currency DEFAULT_CURRENCY = Currency.getInstance("USD");

    public static Order create(
            UserID userID,
            DeliveryMethod deliveryMethod,
            AddressID addressID,
            String notes,
            Money shippingCost,
            Money taxAmount
    ) {
        validateCreateParameters(userID, deliveryMethod, addressID, shippingCost, taxAmount);
        Currency validatedCurrency = validateAndGetCurrency(shippingCost, taxAmount, DEFAULT_CURRENCY);

        Order order = new Order();
        order.id = OrderID.generate();
        order.userID = userID;
        order.deliveryMethod = deliveryMethod;
        order.status = OrderStatus.PENDING;
        order.notes = notes;
        order.orderCurrency = validatedCurrency;
        order.taxFee = taxAmount;
        order.items = new ArrayList<>();
        order.orderTimestamps = OrderTimestamps.create();

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
            Money taxAmount) {

        if (userID == null) {
            throw new InvalidOrderDataException("User ID cannot be null");
        }

        if (deliveryMethod == null) {
            throw new InvalidOrderDataException("Delivery method cannot be null");
        }

        if (deliveryMethod.requiresAddress() && deliveryAddressID == null) {
            throw new MissingDeliveryAddressException();
        }
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDate) {
        if (deliveryMethod == DeliveryMethod.STORE_PICKUP) {
            throw new IllegalStateException("Pickup orders don't have delivery dates");
        }
        this.deliveryInfo.setEstimatedDeliveryDate(estimatedDate);
        orderTimestamps.orderUpdated();
    }

    public void updateDeliveryAddress(AddressID addressID) {
        if (deliveryMethod != DeliveryMethod.STANDARD_DELIVERY && deliveryMethod != DeliveryMethod.EXPRESS_DELIVERY) {
            throw new OrderValidationException("Cannot update address for non-delivery orders");
        }

        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new OrderValidationException("Can only update address for PENDING or CONFIRMED orders");
        }

        if (addressID == null) {
            throw new OrderValidationException("New address ID cannot be null");
        }

        if (this.deliveryInfo == null) {
            throw new OrderValidationException("Delivery info must be set to update address");

        }

        if (addressID.equals(this.deliveryInfo.getAddressID())) {
            throw new OrderValidationException("New address must be different from current address");
        }

        this.deliveryInfo.setAddressID(addressID);
        this.orderTimestamps.orderUpdated();
    }

    public void updateDeliveryMethod(
            DeliveryMethod newMethod,
            AddressID newAddressID,
            LocalDateTime estimatedDeliveryDate,
            Money shippingCost
    ) {
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

        this.deliveryInfo = DeliveryInfo.create(
                estimatedDeliveryDate,
                shippingCost != null ? shippingCost : Money.zero(orderCurrency)
        );

        this.orderTimestamps.orderUpdated();
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(String.format("Cannot transition from %s to %s", this.status, newStatus));
        }

        this.status = newStatus;
        this.orderTimestamps.orderUpdated();
    }

    public void confirm(PaymentID paymentID, LocalDateTime estimatedDeliveryDate) {
        if (estimatedDeliveryDate != null && estimatedDeliveryDate.isBefore(LocalDateTime.now())) {
            throw new InvalidOrderDataException("Estimated delivery date cannot be in the past");
        }
        if (paymentID == null) {
            throw new InvalidOrderDataException("Payment ID cannot be null when confirming an order");
        }
        if (estimatedDeliveryDate != null && deliveryMethod == DeliveryMethod.STORE_PICKUP) {
            throw new InvalidOrderDataException("Pickup orders don't have estimated delivery dates");
        }

        this.deliveryInfo.setEstimatedDeliveryDate(estimatedDeliveryDate);
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
        this.deliveryInfo.setTrackingNumber(deliveryTrackingNumber);
    }

    public void markAsDelivered() {
        changeStatus(OrderStatus.DELIVERED);
    }

    public void markAsPickedUp() {
        if (pickupInfo == null) {
            throw new InvalidOrderDataException("Pickup info must be set before marking order as picked up");
        }

        pickupInfo.complete();
        changeStatus(OrderStatus.PICKED_UP);
    }

    public void complete() {
        if (deliveryMethod == DeliveryMethod.STORE_PICKUP) {
            markAsPickedUp();
        } else {
            markAsDelivered();
        }
    }

    public void returnOrder(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Return reason must be provided");
        }

        if (reason.equals("CustomerNotAvailable") || reason.equals("TimeoutWindowMissed")) {
            deliveryInfo.incrementDeliveryAttempt();
        }

        // Cancel order if more than 3 delivery attempts. TODO: Notify customer
        if (deliveryInfo.getDeliveryAttempt() > 3) {
            cancel("Max delivery attempts reached");
            return;
        }

        changeStatus(OrderStatus.RETURNED);
        deliveryInfo.removeTrackingNumber();
    }

    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel a completed order");
        }

        changeStatus(OrderStatus.CANCELLED);
    }

    public void readyForPickup() {
        if (deliveryMethod != DeliveryMethod.STORE_PICKUP) {
            throw new IllegalStateException("Only pickup orders can be marked as ready for pickup");
        }
        if (this.pickupInfo == null) {
            throw new InvalidOrderDataException("Pickup info must be set before marking order as ready for pickup");
        }

        changeStatus(OrderStatus.READY_FOR_PICKUP);
        this.pickupInfo.ready();
    }

    public List<OrderItem> getItems() {
        if (items == null)  return Collections.emptyList();

        return Collections.unmodifiableList(items);
    }

    public AddressID getAddressID() {
        if (deliveryInfo == null) return null;
        return deliveryInfo.getAddressID();
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

    public void assignItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) throw new EmptyOrderException();

        validateNoDuplicateProducts(items);

        for (OrderItem item : items) {
            item.assignOrder(this.id);
        }

        this.orderTimestamps.orderUpdated();
    }

    public Optional<OrderItem> findItem(ProductID productID) {
        return items.stream()
                .filter(item -> item.getProductID().equals(productID))
                .findFirst();
    }

    private Money calculateTotalAmount() {
        if (items == null || items.isEmpty()) return Money.zero(orderCurrency);

        Money itemsTotal = items.stream()
                .filter(Objects::nonNull)
                .map(OrderItem::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(Money.zero(orderCurrency), Money::add);

        return itemsTotal
                .add(deliveryInfo != null ? deliveryInfo.getShippingCost() : Money.zero(orderCurrency))
                .add(taxFee != null ? taxFee : Money.zero(orderCurrency));
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
                id, userID, status, calculateTotalAmount(), items.size(), orderTimestamps.getCreatedAt());
    }

    public boolean isOngoing() {
        return status.isActive();
    }
}