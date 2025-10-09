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
        List<OrderItemResponse> items = result.items() != null ? result.items()
                .stream()
                .map(OrderItemResponse::from).toList() : List.of();

        DeliveryAddressResponse deliveryAddress = result.deliveryAddress() != null ?
                DeliveryAddressResponse.from(result.deliveryAddress()) : null;

        return OrderDetailResponse.builder()
                .orderId(result.id() != null ? result.id().value() : null)
                .deliveryAddress(deliveryAddress)
                .items(items)
                .paymentID(result.paymentID() != null ? result.paymentID().value() : null)
                .userResponse(result.user() != null ? UserResponse.from(result.user()) : null)

                .shippingCost(result.shippingCost() != null ? result.shippingCost().toFormattedString() : null)
                .deliveryAttempt(result.deliveryAttempt())
                .status(result.status() != null ? result.status().name() : null)
                .totalAmount(result.totalAmount().toFormattedString())
                .deliveryMethod(result.deliveryMethod() != null ? result.deliveryMethod().name() : null)
                .daysSinceReadyForPickup(result.daysSinceReadyForPickup())

                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .estimatedDeliveryDate(result.estimatedDeliveryDate())
                .build();

    }
}
