package microservice.order_service.orders.application.commands.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreateOrderCommand(
        @NotNull UserID userID,
        @PositiveOrZero BigDecimal shippingCost,
        @NotNull @PositiveOrZero BigDecimal taxAmount,
        @NotNull String currency,
        @NotEmpty List<@NotNull CreateOrderItemCommand> items,
        @NotEmpty DeliveryMethod deliveryMethod,
        @NotNull AddressID addressID,
        @Future LocalDateTime estimatedDeliveryDate,
        String notes
) {
    public Money moneyShippingCost() {
        return Money.of(shippingCost, java.util.Currency.getInstance(currency));
    }

    public Money moneyTaxAmount() {
        return Money.of(taxAmount, java.util.Currency.getInstance(currency));
    }

    public void validate() {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        if (shippingCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Shipping cost cannot be negative");
        }
        if (taxAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax amount cannot be negative");
        }
        if (deliveryMethod.requiresAddress() && addressID == null) {
            throw new IllegalArgumentException("Address ID is required for delivery method: " + deliveryMethod);
        }
        if (deliveryMethod().requiresAddress()) {
            throw new IllegalArgumentException("Delivery address ID should not be provided for the selected delivery method");
        }
    }

    @Builder
    public record CreateOrderItemCommand(
            ProductID productID,
            String productName,
            BigDecimal subtotal,
            Integer quantity,
            String currency,
            Boolean prescriptionRequired
    ) {
        public OrderItem toEntity() {
            return OrderItem.create(
                    this.productID,
                    this.productName,
                    Money.of(this.subtotal, java.util.Currency.getInstance(this.currency)),
                    this.quantity,
                    this.prescriptionRequired
            );
        }
    }
}