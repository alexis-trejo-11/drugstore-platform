package microservice.order_service.orders.application.commands.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderCommand(
        @NotNull UserID userID,
        @NotEmpty List<@NotNull CreateOrderItemCommand> items,
        @NotEmpty DeliveryMethod deliveryMethod,
        @NotNull AddressID addressID,
        @Future LocalDateTime estimatedDeliveryDate,
        String notes
) {}