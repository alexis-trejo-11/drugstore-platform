package microservice.order_service.infrastructure.api.controller.mapper;

import libs_kernel.page.PageResponse;
import libs_kernel.page.PaginationMetadata;
import microservice.order_service.application.queries.response.OrderQueryDetailResult;
import microservice.order_service.application.queries.response.OrderQueryResult;
import microservice.order_service.infrastructure.api.controller.dto.OrderDetailResponse;
import microservice.order_service.infrastructure.api.controller.dto.OrderResponse;
import microservice.order_service.infrastructure.api.controller.dto.PagedOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMapper {

    public OrderResponse toResponse(OrderQueryResult result) {
        return OrderResponse.builder()
                .orderId(result.orderId().value())
                .createdAt(result.createdAt())
                .estimatedDeliveryDate(result.estimatedDeliveryDate())
                .status(result.status().name())
                .deliveredMethod(result.deliveredMethod().name())
                .totalAmount(result.totalAmount())
                .build();
    }

    public OrderDetailResponse toDetailResponse(OrderQueryDetailResult result) {
        return OrderDetailResponse.builder()
                .orderId(result.orderId().value())
                .createdAt(result.createdAt())
                .estimatedDeliveryDate(result.estimatedDeliveryDate())
                .status(result.status().name())
                .deliveryMethod(result.deliveryMethod().name())
                .totalAmount(result.totalAmount())
                //.items(result.items().stream().map(this::toItemResponse).toList())
                .build();
    }


    public PagedOrderResponse toPagedResponse(PageResponse<OrderQueryResult> page) {
        var orders = page.getContent().stream()
                .map(this::toResponse)
                .toArray(OrderResponse[]::new);

        var metadata = PaginationMetadata.from(page);

        return new PagedOrderResponse(metadata, orders);
    }
}
