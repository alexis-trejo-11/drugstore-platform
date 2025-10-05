package microservice.order_service.infrastructure.persistence.mapper;

import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.*;
import microservice.order_service.infrastructure.persistence.models.*;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

@Component
public class OrderJpaMapper {
    public OrderModel toModel(Order domain) {
        if (domain == null) return null;

        OrderModel entity = OrderModel.builder()
                .id(domain.getId().toString())
                .customerId(domain.getCustomerId().toString())
                .totalAmount(domain.getTotalAmount().amount())
                .currency(domain.getTotalAmount().toString())
                .deliveryMethod(domain.getDeliveryMethod().toString())
                .status(domain.getStatus().toString())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .estimatedDeliveryDate(domain.getEstimatedDeliveryDate())
                .notes(domain.getNotes())
                .build();

        if (domain.getItems() != null) {
            List<OrderItemModel> itemEntities = domain.getItems().stream()
                    .map(item -> toItemModel(item, entity))
                    .toList();
            entity.setItems(itemEntities);
        }

        return entity;
    }

    public OrderItemModel toItemModel(OrderItem domain, OrderModel orderEntity) {
        return OrderItemModel.builder()
                .order(orderEntity)
                .productId(domain.getProductId().toString())
                .productName(domain.getProductName())
                .unitPrice(domain.getUnitPrice().amount())
                .quantity(domain.getQuantity())
                .subtotal(domain.getSubtotal().amount())
                .prescriptionRequired(domain.isPrescriptionRequired())
                .build();
    }

    public DeliveryAddressEmbeddable mapDeliveryAddressToEmbeddable(DeliveryAddress domain) {
        if (domain == null) return null;

        return new DeliveryAddressEmbeddable(
                domain.street(),
                domain.city(),
                domain.state(),
                domain.zipCode(),
                domain.country(),
                domain.additionalInfo()
        );
    }

    public Order toDomain(OrderModel entity) {
        if (entity == null) return null;

        List<OrderItem> items = null;
        if (entity.getItems() != null) {
            items = entity.getItems().stream()
                    .map(this::toItemDomain)
                    .toList();
        }

        return Order.builder()
                .id(OrderID.of(entity.getId()))
                .customerId(CustomerID.of(entity.getCustomerId()))
                .items(items)
                .deliveryMethod(DeliveryMethod.valueOf(entity.getDeliveryMethod()))
                .totalAmount(new Money(entity.getTotalAmount(), Currency.getInstance("MXN")))
                .status(OrderStatus.valueOf(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .estimatedDeliveryDate(entity.getEstimatedDeliveryDate())
                .notes(entity.getNotes())
                .build();
    }

    private OrderItem toItemDomain(OrderItemModel entity) {
        return OrderItem.builder()
                .productId(ProductID.of(entity.getId()))
                .productName(entity.getProductName())
                .unitPrice(new Money(entity.getUnitPrice(), Currency.getInstance("MXN")))
                .quantity(entity.getQuantity())
                .prescriptionRequired(entity.getPrescriptionRequired())
                .build();
    }

    private DeliveryAddress mapDeliveryAddressToDomain(DeliveryAddressEmbeddable entity) {
        if (entity == null) return null;
        return DeliveryAddress.builder()
                .street(entity.getStreet())
                .city(entity.getCity())
                .state(entity.getState())
                .zipCode(entity.getPostalCode())
                .country(entity.getCountry())
                .additionalInfo(entity.getAdditionalInfo())
                .build();
    }

    public List<Order> toDomainList(List<OrderModel> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    public List<OrderModel> toModelList(List<Order> domains) {
        if (domains == null) return null;
        return domains.stream()
                .map(this::toModel)
                .toList();
    }
}