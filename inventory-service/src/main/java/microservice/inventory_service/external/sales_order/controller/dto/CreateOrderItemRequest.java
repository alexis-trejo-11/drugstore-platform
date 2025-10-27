package microservice.inventory_service.external.sales_order.controller.dto;

public record CreateOrderItemRequest(
        String id,
        String productId,
        String productName,
        Integer quantity
) {
}
