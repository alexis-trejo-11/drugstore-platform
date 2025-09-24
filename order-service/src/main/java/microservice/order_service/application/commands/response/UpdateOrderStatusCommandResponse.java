package microservice.order_service.application.commands.response;

import lombok.Builder;
import lombok.Data;
import microservice.order_service.domain.models.valueobjects.OrderId;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateOrderStatusCommandResponse {
    private OrderId orderId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime updatedAt;
}

