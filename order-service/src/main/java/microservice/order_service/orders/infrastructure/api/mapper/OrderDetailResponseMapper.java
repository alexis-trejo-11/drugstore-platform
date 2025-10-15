package microservice.order_service.orders.infrastructure.api.mapper;

import libs_kernel.mapper.EntityDetailMapper;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.external.users.infrastructure.api.dto.UserResponse;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderDetailResponse;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderItemResponse;
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

        DeliveryAddressResponse deliveryAddress = result.deliveryAddress() != null ? DeliveryAddressResponse.from(result.deliveryAddress()) : null;
        UserResponse userResponse = result.user() != null ? UserResponse.from(result.user()) : null;

        return OrderDetailResponse.builder()
                .id(result.id() != null ? result.id().value() : null)
                .deliveryAddress(deliveryAddress)
                .deliveryMethod(result.deliveryMethod() != null ? result.deliveryMethod() : null)
                .notes(result.notes())
                .currency(result.taxAmount() != null ? result.taxAmount().currency() : null)
                .taxAmount(result.taxAmount() != null ? result.taxAmount().amount() : null)
                .items(items)
                .userResponse(userResponse)
                .paymentID(result.paymentID() != null ? result.paymentID().value() : null)
                .status(result.status() != null ? result.status() : null)
                .totalAmount(result.totalAmount().amount())

                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .build();

    }
}
