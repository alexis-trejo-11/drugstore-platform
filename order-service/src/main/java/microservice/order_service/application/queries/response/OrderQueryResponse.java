package microservice.order_service.application.queries.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderQueryResponse {
    private UUID orderId;
    private UUID customerId;
    private List<OrderItemQueryResponse> items;
    private BigDecimal totalAmount;
    private String currency;
    private String deliveryMethod;
    private DeliveryAddressQueryResponse deliveryAddress;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
}


