package microservice.order_service.orders.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import microservice.order_service.orders.application.commands.request.CreateOrderItemCommand;
import microservice.order_service.orders.application.commands.request.CreateDeliveryOrderCommand;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public record CreateOrderRequest(
        @NotNull @NotBlank
        String userID,

        @NotNull @NotBlank
        String addressID,

        @NotNull
        DeliveryMethod deliveryMethod,

        @NotNull
        String notes,

        @NotNull @NotBlank @Length(min = 3, max = 3)
        String currency,

        @NotNull @Size(min = 1, max = 50)
        List<OrderItemRequest> items
) {
    @Builder
    public record OrderItemRequest(
            @NotNull @NotBlank
            String productID,

            @NotNull @NotBlank @Length(min = 3, max = 100)
            String productName,

            @PositiveOrZero @NotNull
            BigDecimal subtotal,

            @PositiveOrZero
            int quantity,

            @NotNull
            Boolean isPrescriptionRequired
    ) {}

    public CreateDeliveryOrderCommand toCommand() {
        Currency currencyObj = Currency.getInstance(currency);
        List<CreateOrderItemCommand> itemCommands = items.stream()
                .map(item -> CreateOrderItemCommand.builder()
                        .productID(new ProductID(item.productID()))
                        .productName(item.productName())
                        .subtotal(item.subtotal())
                        .quantity(item.quantity())
                        .isPrescriptionRequired(item.isPrescriptionRequired())
                        .build())
                .toList();

        return CreateDeliveryOrderCommand.builder()
                .userID(userID != null ? new UserID(userID) : null)
                .addressID(addressID != null ? new AddressID(addressID) : null)
                .deliveryMethod(deliveryMethod)
                .notes(notes)
                .items(itemCommands)
                .build();
    }
}