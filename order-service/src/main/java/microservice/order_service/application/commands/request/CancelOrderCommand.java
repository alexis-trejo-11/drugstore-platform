package microservice.order_service.application.commands.request;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CancelOrderCommand {
    private UUID customerId;
    private UUID orderId;
    private String cancellationReason;
}
