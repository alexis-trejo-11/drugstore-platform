package microservice.order_service.application.queries.response;

import lombok.Builder;
import microservice.order_service.domain.models.valueobjects.OrderId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public class OrderSummaryQueryResponse {
    private OrderId orderId;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDeliveryDate;
    private Integer totalItems;
}


