package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderResponseMapper implements ResponseMapper<OrderSummaryResponse, Order> {
    @Override
    public OrderSummaryResponse toResponse(Order Order) {
        if (Order == null) {
            return null;
        }

        return OrderSummaryResponse.builder()
                .id(Order.getId().value())
                .orderNumber(Order.getOrderNumber())
                .supplierId(Order.getSupplierId())
                .status(Order.getStatus())
                .orderDate(Order.getOrderDate())
                .expectedDeliveryDate(Order.getExpectedDeliveryDate())
                .itemCount(Order.getItems() != null ? Order.getItems().size() : 0)
                .build();
    }

    @Override
    public List<OrderSummaryResponse> toResponses(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<OrderSummaryResponse> toResponsePage(Page<Order> orders) {
        if (orders == null) {
            return null;
        }

        Page<OrderSummaryResponse> content = orders.map(this::toResponse);
        return PageResponse.from(content);
    }
}
