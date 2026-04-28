package microservice.order_service.orders.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.application.commands.request.CreateDeliveryOrderCommand;
import microservice.order_service.orders.application.commands.request.CreateOrderItemCommand;
import microservice.order_service.orders.application.commands.request.CreatePickupOrderCommand;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

import java.util.Currency;
import java.util.List;

public record CreateOrderRequest(
        @NotNull @NotBlank
        String userID,

        @NotNull
        DeliveryMethod deliveryMethod,

        @NotNull
        String notes,

        @NotNull
        microservice.order_service.orders.domain.models.enums.Currency currency,

        @NotNull @Size(min = 1, max = 50)
        List<OrderItemRequest> items,

        DeliveryInfoCreateRequest deliveryInfo,
        PickupInfoCreateRequest pickupInfo
) {

    public record DeliveryInfoCreateRequest(
            @NotNull @NotBlank @Length(max = 36)
            String addressID
    ) {}

    public record PickupInfoCreateRequest(
            @NotNull @NotBlank @Length(max = 36)
            String storeID,

            @NotNull @NotBlank @Length(max = 100)
            String storeName,

            @NotNull @NotBlank @Length(max = 100)
            String storeAddress
    ) {}

    public CreateDeliveryOrderCommand toDeliveryOrderCommand() {
        if (deliveryInfo == null) {
            throw new IllegalArgumentException("Delivery info must be provided for delivery orders.");
        }

        Currency currencyObj = Currency.getInstance(currency.getCode());
        List<CreateOrderItemCommand> itemCommands = items.stream()
                .map(item -> CreateOrderItemCommand.builder()
                        .productID(new ProductID(item.productID()))
                        .productName(item.productName())
                        .subtotal(Money.of(item.subtotal(), currencyObj))
                        .quantity(item.quantity())
                        .isPrescriptionRequired(item.isPrescriptionRequired())
                        .build())
                .toList();

        return new CreateDeliveryOrderCommand(
                new UserID(userID),
                deliveryMethod,
                notes,
                itemCommands,
                new AddressID(deliveryInfo.addressID())
        );
    }

    public CreatePickupOrderCommand toPickupOrderCommand() {
        if (pickupInfo == null) {
            throw new IllegalArgumentException("Pickup info must be provided for pickup orders.");
        }

        Currency currencyObj = Currency.getInstance(currency.getCode());
        List<CreateOrderItemCommand> itemCommands = items.stream()
                .map(item -> CreateOrderItemCommand.builder()
                        .productID(new ProductID(item.productID()))
                        .productName(item.productName())
                        .subtotal(Money.of(item.subtotal(), currencyObj))
                        .quantity(item.quantity())
                        .isPrescriptionRequired(item.isPrescriptionRequired())
                        .build())
                .toList();

        return new CreatePickupOrderCommand(
                new UserID(userID),
                deliveryMethod,
                notes,
                itemCommands,
                pickupInfo.storeID(),
                pickupInfo.storeName(),
                pickupInfo.storeAddress()
        );
    }
}

