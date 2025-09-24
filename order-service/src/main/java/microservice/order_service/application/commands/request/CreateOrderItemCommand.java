package microservice.order_service.application.commands.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateOrderItemCommand {
    private UUID productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private Boolean prescriptionRequired;
}
