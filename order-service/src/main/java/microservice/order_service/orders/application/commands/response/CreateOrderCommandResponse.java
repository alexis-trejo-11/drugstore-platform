package microservice.order_service.orders.application.commands.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateOrderCommandResponse {
    private OrderID orderId;
    private String status;
    private LocalDateTime createdAt;


    public static CreateOrderCommandResponse from(Order order) {
        return new CreateOrderCommandResponse(
                order.getId(),
                order.getStatus().name(),
                order.getOrderTimestamps() != null ? order.getOrderTimestamps().getCreatedAt() : null
        );
    }
}
