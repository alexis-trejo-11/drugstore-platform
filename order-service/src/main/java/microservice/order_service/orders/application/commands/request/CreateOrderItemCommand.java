package microservice.order_service.orders.application.commands.request;

import microservice.order_service.orders.domain.models.valueobjects.ProductID;
import java.math.BigDecimal;

public record CreateOrderItemCommand(
    ProductID productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    Boolean prescriptionRequired
) {}
