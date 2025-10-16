package microservice.order_service.orders.application.commands.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.util.Currency;
import java.util.List;

@Builder
public record CreateDeliveryOrderCommand(
        @NotNull UserID userID,
        @NotNull DeliveryMethod deliveryMethod,
        @NotEmpty List<@NotNull CreateOrderItemCommand> items,
        @NotNull AddressID addressID,
        String notes
) {}