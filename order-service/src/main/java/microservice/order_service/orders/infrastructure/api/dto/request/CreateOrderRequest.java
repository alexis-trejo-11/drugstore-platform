package microservice.order_service.orders.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import microservice.order_service.orders.application.commands.request.CreateOrderItemCommand;
import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
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

        @PositiveOrZero @NotNull
        BigDecimal shippingCost,

        @NotNull @PositiveOrZero
        BigDecimal taxAmount,

        @NotNull @NotBlank @Length(min = 3, max = 3)
        String currency,

        @NotNull
        String notes,

        @NotNull @Size(min = 1, max = 50)
        List<OrderItemRequest> items
) {
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

    public CreateOrderCommand toCommand() {
        Currency currencyObj = Currency.getInstance(currency);
        return CreateOrderCommand.builder()

                .userID(userID != null ? new UserID(userID) : null)
                .addressID(addressID != null ? new AddressID(addressID) : null)
                .deliveryMethod(deliveryMethod)
                .shippingCost(shippingCost)
                .taxAmount(taxAmount)
                .currency(currencyObj)
                .notes(notes)
                .items(items != null ? items.stream().map(item -> new CreateOrderItemCommand(
                       item.productID !=  null ? new ProductID(item.productID) : null,
                        item.productName != null ? item.productName : null,
                        item.subtotal != null ? item.subtotal : BigDecimal.ZERO,
                        item.quantity,
                        currencyObj,
                        item.isPrescriptionRequired
                )).toList() : List.of())
                .build();
    }
}