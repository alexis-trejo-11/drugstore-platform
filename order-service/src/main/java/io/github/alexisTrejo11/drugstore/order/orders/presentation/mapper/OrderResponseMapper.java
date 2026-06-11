package io.github.alexisTrejo11.drugstore.order.orders.presentation.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderQueryResult;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderResponseMapper implements ResponseMapper<OrderResponse, OrderQueryResult> {

    @Override
    public OrderResponse toResponse(OrderQueryResult result) {
        if (result == null) return null;

        return OrderResponse.builder()
                .orderId(result.id() != null ? result.id().value() : null)
                .userID(result.userID() != null ? result.userID().value() : null)
                .status(result.status() != null ? result.status().name() : null)
                .totalAmount(result.totalAmount() != null ? result.totalAmount().toFormattedString() : null)
                .totalItems(result.totalItems())
                .deliveryMethod(result.deliveryMethod() != null ? result.deliveryMethod().name() : null)
                .createdAt(result.createdAt())
                .build();
    }

    @Override
    public List<OrderResponse> toResponses(List<OrderQueryResult> orderQueryResults) {
        if (orderQueryResults == null) return null;

        return orderQueryResults.stream().map(this::toResponse).toList();
    }

    @Override
    public PageResponse<OrderResponse> toResponsePage(Page<OrderQueryResult> orderQueryResults) {
        PageResponse<OrderResponse> pageResponse = new PageResponse<>();
        if (orderQueryResults == null) return pageResponse;

        Page<OrderResponse> responsePage  = orderQueryResults.map(this::toResponse);
        return pageResponse.fromPage(responsePage);
    }
}
