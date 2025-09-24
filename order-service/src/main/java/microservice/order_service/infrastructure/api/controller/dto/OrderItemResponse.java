package microservice.order_service.infrastructure.api.controller.dto;


import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal
) {}
