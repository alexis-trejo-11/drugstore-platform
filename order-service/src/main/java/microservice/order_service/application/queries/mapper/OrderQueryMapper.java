package microservice.order_service.application.queries.mapper;

import microservice.order_service.application.queries.response.DeliveryAddressQueryResponse;
import microservice.order_service.application.queries.response.OrderItemQueryResult;

import microservice.order_service.application.queries.response.OrderQueryDetailResult;
import microservice.order_service.application.queries.response.OrderQueryResult;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.valueobjects.DeliveryAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQueryMapper {

    public OrderQueryDetailResult toOrderDetailResponse(Order domain) {
        if (domain == null) return null;

        return OrderQueryDetailResult.builder()
                .orderId(domain.getId())
                .customerId(domain.getCustomerId())
                .items(toOrderItemResponseList(domain.getItems()))
                .totalAmount(domain.getTotalAmount().amount())
                .currency(domain.getTotalAmount().currency().toString())
                .deliveryMethod(domain.getDeliveryMethod())
                //.deliveryAddress(toDeliveryAddressResponse(domain.getDeliveryAddress()))
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .estimatedDeliveryDate(domain.getEstimatedDeliveryDate())
                .notes(domain.getNotes())
                .build();
    }

    public OrderQueryResult toOrderSummaryResponse(Order domain) {
        if (domain == null) return null;

        int totalItems = domain.getItems() != null ?
                domain.getItems().stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum() : 0;

        return OrderQueryResult.builder()
                .orderId(domain.getId())
                .totalAmount(domain.getTotalAmount().amount())
                .currency(domain.getTotalAmount().currency().toString())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .estimatedDeliveryDate(domain.getEstimatedDeliveryDate())
                .deliveredMethod(domain.getDeliveryMethod())
                .totalItems(totalItems)
                .build();
    }

    private List<OrderItemQueryResult> toOrderItemResponseList(List<OrderItem> items) {
        if (items == null) return null;

        return items.stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

    private OrderItemQueryResult toOrderItemResponse(OrderItem item) {
        if (item == null) return null;
        return OrderItemQueryResult.builder()
                .productID(item.getProductId())
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