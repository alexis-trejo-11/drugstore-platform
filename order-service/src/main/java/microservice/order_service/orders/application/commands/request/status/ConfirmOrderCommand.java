package microservice.order_service.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PaymentID;

import java.time.LocalDateTime;

public record ConfirmOrderCommand(
       @NotNull OrderID orderID,
       @NotNull PaymentID paymentID,
       LocalDateTime estimatedDeliveryDate
) {}
