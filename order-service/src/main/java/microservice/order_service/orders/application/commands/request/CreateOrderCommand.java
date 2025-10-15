package microservice.order_service.orders.application.commands.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Builder
public record CreateOrderCommand(
        @NotNull UserID userID,
        @PositiveOrZero BigDecimal serviceFee,
        @NotNull @PositiveOrZero BigDecimal taxAmount,
        @NotEmpty List<@NotNull CreateOrderItemCommand> items,
        @NotEmpty DeliveryMethod deliveryMethod,
        @NotNull Currency currency,
        @NotNull AddressID addressID,
        String notes
) {
    public Money moneyShippingCost() {
        return Money.of(serviceFee, currency);
    }

    public Money moneyTaxAmount() {
        return Money.of(taxAmount, currency);
    }
}