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
                .id(domain.getId().value())
                .customerId(domain.getCustomerId().value())
                .totalAmount(domain.getTotalAmount().amount())
                .currency(domain.getTotalAmount().toString())
                .deliveryMethod(mapDeliveryMethodToEntity(domain.getDeliveryMethod()))
                .deliveryAddress(mapDeliveryAddressToEmbeddable(domain.getDeliveryAddress()))
                .status(mapOrderStatusToEntity(domain.getStatus()))
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
                .productId(domain.getProductId().value())
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

    public DeliveryMethodModel mapDeliveryMethodToEntity(DeliveryMethod domain) {
        return switch (domain) {
            case HOME_DELIVERY -> DeliveryMethodModel.HOME_DELIVERY;
            case STORE_PICKUP -> DeliveryMethodModel.STORE_PICKUP;
            case EXPRESS_DELIVERY -> DeliveryMethodModel.EXPRESS_DELIVERY;
            case STANDARD_DELIVERY -> DeliveryMethodModel.STANDARD_DELIVERY;
        };
    }

    public OrderStatusModel mapOrderStatusToEntity(OrderStatus domain) {
        return switch (domain) {
            case PENDING -> OrderStatusModel.PENDING;
            case CONFIRMED -> OrderStatusModel.CONFIRMED;
            case PREPARING -> OrderStatusModel.PROCESSING;
            case READY_FOR_PICKUP ->OrderStatusModel.READY_FOR_PICKUP;
            case OUT_FOR_DELIVERY-> OrderStatusModel.SHIPPED;
            case DELIVERED -> OrderStatusModel.DELIVERED;
            case PICKED_UP -> OrderStatusModel.PICKED_UP;
            case CANCELLED -> OrderStatusModel.CANCELLED;
            case RETURNED -> OrderStatusModel.RETURNED;
        };
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
                .id(new OrderId(entity.getId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .items(items)
                .deliveryMethod(mapDeliveryMethodToDomain(entity.getDeliveryMethod()))
                .deliveryAddress(mapDeliveryAddressToDomain(entity.getDeliveryAddress()))
                .status(mapOrderStatusToDomain(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .estimatedDeliveryDate(entity.getEstimatedDeliveryDate())
                .notes(entity.getNotes())
                .build();
    }

    private OrderItem toItemDomain(OrderItemModel entity) {
        return OrderItem.builder()
                .productId(new ProductId(entity.getId()))
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

    private DeliveryMethod mapDeliveryMethodToDomain(DeliveryMethodModel entity) {
        return switch (entity) {
            case HOME_DELIVERY -> DeliveryMethod.HOME_DELIVERY;
            case STORE_PICKUP -> DeliveryMethod.STORE_PICKUP;
            case EXPRESS_DELIVERY -> DeliveryMethod.EXPRESS_DELIVERY;
            case STANDARD_DELIVERY -> DeliveryMethod.STANDARD_DELIVERY;
        };
    }

    private OrderStatus mapOrderStatusToDomain(OrderStatusModel entity) {
        return switch (entity) {
            case PENDING -> OrderStatus.PENDING;
            case CONFIRMED -> OrderStatus.CONFIRMED;
            case PROCESSING -> OrderStatus.PREPARING;
            case SHIPPED -> OrderStatus.OUT_FOR_DELIVERY;
            case DELIVERED -> OrderStatus.DELIVERED;
            case CANCELLED -> OrderStatus.CANCELLED;
            case RETURNED -> OrderStatus.RETURNED;
            case READY_FOR_PICKUP -> OrderStatus.READY_FOR_PICKUP;
            case PICKED_UP -> OrderStatus.PICKED_UP;
        };
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