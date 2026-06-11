package io.github.alexisTrejo11.drugstore.order.orders.presentation.mapper;

import libs_kernel.mapper.EntityDetailMapper;
import io.github.alexisTrejo11.drugstore.order.external.user.infra.dto.UserResponse;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderDetailResult;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.DeliveryInfoResponse;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.OrderDetailResponse;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.OrderItemResponse;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.PickupInfoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDetailResponseMapper implements EntityDetailMapper<OrderDetailResult, OrderDetailResponse> {

    @Override
    public OrderDetailResponse toDetail(OrderDetailResult result) {
        if (result == null) return null;
        List<OrderItemResponse> items = result.items() != null ? result.items()
                .stream()
                .map(OrderItemResponse::from).toList() : List.of();

        UserResponse userResponse = result.user() != null ? UserResponse.from(result.user()) : null;

        return OrderDetailResponse.builder()
                .id(result.id() != null ? result.id().value() : null)
                .deliveryMethod(result.deliveryMethod() != null ? result.deliveryMethod() : null)
                .notes(result.notes())
                .taxAmount(result.taxAmount() != null ? result.taxAmount().toFormattedString() : null)
                .deliveryInfo(DeliveryInfoResponse.from(result.deliveryInfo()))
                .pickupInfo(PickupInfoResponse.from(result.pickupInfo()))
                .items(items)
                .userResponse(userResponse)
                .paymentID(result.paymentID() != null ? result.paymentID().value() : null)
                .status(result.status() != null ? result.status() : null)
                .totalAmount(result.totalAmount().toFormattedString())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .build();

    }
}
