package microservice.order_service.application.commands.request;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateOrderCommand {
    private UUID customerId;
    private List<CreateOrderItemCommand> items;
    private String deliveryMethod;
    private CreateDeliveryAddressCommand deliveryAddress;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
}


