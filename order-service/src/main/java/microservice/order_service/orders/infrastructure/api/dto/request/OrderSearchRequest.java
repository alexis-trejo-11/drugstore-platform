package microservice.order_service.orders.infrastructure.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Order search criteria")
public record OrderSearchRequest(
        @Schema(description = "User ID to filter by", example = "userID-123")
        String userId,

        @Schema(description = "Order status to filter by", example = "PENDING")
        OrderStatus status,

        @Schema(description = "Start date for order creation range")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @Schema(description = "End date for order creation range")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate,

        @Schema(description = "Delivery method", example = "EXPRESS")
        String deliveryMethod,

        @Schema(description = "Minimum shipping cost")
        Double minShippingCost,

        @Schema(description = "Maximum shipping cost")
        Double maxShippingCost,

        @Schema(description = "Page number (0-based)", example = "0")
        Integer page,

        @Schema(description = "Page size", example = "20")
        Integer size,

        @Schema(description = "Sort field", example = "createdAt")
        String sortBy,

        @Schema(description = "Sort direction", example = "DESC")
        String sortDirection
) {}
