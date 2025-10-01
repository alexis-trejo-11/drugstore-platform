package microservice.order_service.application.queries.mapper;

import microservice.order_service.application.queries.response.DeliveryAddressQueryResponse;
import microservice.order_service.application.queries.response.OrderItemQueryResponse;
import microservice.order_service.application.queries.response.OrderQueryResponse;
import microservice.order_service.application.queries.response.OrderSummaryQueryResponse;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.valueobjects.DeliveryAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQueryMapper {

    public OrderQueryResponse toOrderResponse(Order domain) {
        if (domain == null) return null;

        return OrderQueryResponse.builder()
                .orderId(domain.getId().value())
                .customerId(domain.getCustomerId().value())
                .items(toOrderItemResponseList(domain.getItems()))
                .totalAmount(domain.getTotalAmount().amount())
                .currency(domain.getTotalAmount().currency().toString())
                .deliveryMethod(domain.getDeliveryMethod().name())
                .deliveryAddress(toDeliveryAddressResponse(domain.getDeliveryAddress()))
                .status(domain.getStatus().name())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .estimatedDeliveryDate(domain.getEstimatedDeliveryDate())
                .notes(domain.getNotes())
                .build();
    }

    public OrderSummaryQueryResponse toOrderSummaryResponse(Order domain) {
        if (domain == null) return null;

        int totalItems = domain.getItems() != null ?
                domain.getItems().stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum() : 0;

        return OrderSummaryQueryResponse.builder()
                .orderId(domain.getId())
                .totalAmount(domain.getTotalAmount().amount())
                .currency(domain.getTotalAmount().currency().toString())
                .status(domain.getStatus().name())
                .createdAt(domain.getCreatedAt())
                .estimatedDeliveryDate(domain.getEstimatedDeliveryDate())
                .totalItems(totalItems)
                .build();
    }

    private List<OrderItemQueryResponse> toOrderItemResponseList(List<OrderItem> items) {
        if (items == null) return null;

        return items.stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

    private OrderItemQueryResponse toOrderItemResponse(OrderItem item) {
        if (item == null) return null;

        return OrderItemQueryResponse.builder()
                .productId(item.getProductId().value())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice().amount())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal().amount())
                .prescriptionRequired(item.isPrescriptionRequired())
                .build();
    }

    private DeliveryAddressQueryResponse toDeliveryAddressResponse(DeliveryAddress address) {
        if (address == null) return null;

        return DeliveryAddressQueryResponse.builder()
                .street(address.street())
                .city(address.city())
                .state(address.state())
                .postalCode(address.zipCode())
                .country(address.country())
                .additionalInfo(address.additionalInfo())
                .build();
    }
}