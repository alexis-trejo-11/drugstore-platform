package microservice.order_service.orders.infrastructure.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.orders.application.commands.request.status.ConfirmOrderCommand;
import  microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PaymentID;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmOrderRequest {

    @NotNull(message = "Estimated delivery date cannot be null")
    @NotBlank(message = "Estimated delivery date cannot be blank")
    @Future(message = "Estimated delivery date must be in the future")
    private LocalDateTime estimatedDeliveryDate;

    @NotNull(message = "Payment ID cannot be null")
    @NotBlank(message = "Payment ID cannot be blank")
    private String paymentID;


    public ConfirmOrderCommand toCommand(String orderID) {
        return new ConfirmOrderCommand(
               OrderID.of(orderID), PaymentID.of(paymentID), estimatedDeliveryDate
        );
    }
}
