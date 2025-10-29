package microservice.inventory_service.order.sales.infrastructure.adapter.inbound.rest.dto;

public record CreateOrderItemRequest(
        String id,
        String productId,
        String productName,
        Integer quantity
) {
}
