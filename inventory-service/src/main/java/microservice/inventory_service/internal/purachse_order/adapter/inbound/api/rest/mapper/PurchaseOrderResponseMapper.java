package microservice.inventory_service.internal.purachse_order.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.adapter.inbound.api.rest.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseOrderResponseMapper implements ResponseMapper<OrderSummaryResponse, PurchaseOrder> {
    @Override
    public OrderSummaryResponse toResponse(PurchaseOrder PurchaseOrder) {
        if (PurchaseOrder == null) {
            return null;
        }

        return OrderSummaryResponse.builder()
                .id(PurchaseOrder.getId().value())
                .orderNumber(PurchaseOrder.getOrderNumber())
                .supplierId(PurchaseOrder.getSupplierId())
                .status(PurchaseOrder.getStatus())
                .orderDate(PurchaseOrder.getOrderDate())
                .expectedDeliveryDate(PurchaseOrder.getExpectedDeliveryDate())
                .itemCount(PurchaseOrder.getItems() != null ? PurchaseOrder.getItems().size() : 0)
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
    public PageResponse<OrderSummaryResponse> toResponsePage(Page<PurchaseOrder> orders) {
        if (orders == null) {
            return null;
        }

        Page<OrderSummaryResponse> content = orders.map(this::toResponse);
        return PageResponse.from(content);
    }
}
