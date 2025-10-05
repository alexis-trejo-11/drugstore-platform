package microservice.order_service.application.queries.response;

import lombok.Builder;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerID;
import microservice.order_service.domain.models.valueobjects.OrderID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderQueryDetailResult(
    OrderID orderId,
    CustomerID customerId,
    List<OrderItemQueryResult> items,
    BigDecimal totalAmount,
    String currency,
    DeliveryMethod deliveryMethod,
    //DeliveryAddressQueryResponse deliveryAddress,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime estimatedDeliveryDate,
    String notes
) {}
