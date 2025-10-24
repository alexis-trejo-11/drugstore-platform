package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderResponseMapper implements ResponseMapper<OrderSummaryResponse, PurchaseOrder> {
    @Override
    public OrderSummaryResponse toResponse(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        return OrderSummaryResponse.builder()
                .id(purchaseOrder.getId().value())
                .orderNumber(purchaseOrder.getOrderNumber())
                .supplierId(purchaseOrder.getSupplierId())
                .status(purchaseOrder.getStatus())
                .orderDate(purchaseOrder.getOrderDate())
                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                .itemCount(purchaseOrder.getItems() != null ? purchaseOrder.getItems().size() : 0)
                .build();
    }

    @Override
    public List<OrderSummaryResponse> toResponses(List<PurchaseOrder> purchaseOrders) {
        if (purchaseOrders == null) {
            return null;
        }

        return purchaseOrders.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<OrderSummaryResponse> toResponsePage(Page<PurchaseOrder> purchaseOrders) {
        if (purchaseOrders == null) {
            return null;
        }

        Page<OrderSummaryResponse> content = purchaseOrders.map(this::toResponse);
        return PageResponse.from(content);
    }
}
