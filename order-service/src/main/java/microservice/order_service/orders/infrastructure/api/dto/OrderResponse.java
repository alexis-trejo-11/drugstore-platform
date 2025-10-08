package microservice.order_service.orders.infrastructure.api.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Order response containing order details and summary information")
public record OrderResponse(

        @Schema(
                description = "Unique identifier of the order",
                example = "ORD-123456789",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String orderId,

        @Schema(
                description = "Unique identifier of the user who placed the order",
                example = "USER-987654321",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String userID,

        @Schema(
                description = "Current status of the order",
                example = "PROCESSING",
                allowableValues = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String status,

        @Schema(
                description = "Total amount of the order including taxes and shipping",
                example = "150.75",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String totalAmount,

        @Schema(
                description = "Total number of items in the order",
                example = "5",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer totalItems,

        @Schema(
                description = "Delivery method selected for the order",
                example = "EXPRESS_SHIPPING",
                allowableValues = {"STANDARD_SHIPPING", "EXPRESS_SHIPPING", "IN_STORE_PICKUP"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String deliveryMethod


        ,@Schema(
                description = "Timestamp when the order was created",
                example = "2024-06-15T10:15:30",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime createdAt
) {}