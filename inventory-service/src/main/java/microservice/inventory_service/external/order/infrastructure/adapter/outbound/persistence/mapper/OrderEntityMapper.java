package microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.model.OrderEntity;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.entity.OrderItem;
import microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.model.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

@Component
public class OrderEntityMapper implements EntityMapper<OrderEntity, Order> {

    @Override
    public OrderEntity fromDomain(Order Order) {
        if (Order == null) {
            return null;
        }

        return OrderEntity.builder()
                .id(Order.getId() == null ? null : Order.getId().value())
                .orderNumber(Order.getOrderNumber())
                .supplierId(Order.getSupplierId())
                .supplierName(Order.getSupplierName())
                .items(mapItems(Order.getItems(), Order.getId()))
                .totalAmount(Order.getTotalAmount())
                .status(Order.getStatus())
                .orderDate(Order.getOrderDate())
                .currency(Order.getCurrency() == null ? null : Order.getCurrency().getCurrencyCode())
                .expectedDeliveryDate(Order.getExpectedDeliveryDate())
                .actualDeliveryDate(Order.getActualDeliveryDate())
                .deliveryLocation(Order.getDeliveryLocation())
                .createdBy(Order.getCreatedBy() == null ? null : Order.getCreatedBy().value())
                .approvedBy(Order.getApprovedBy() == null ? null : Order.getApprovedBy().value())
                .createdAt(Order.getCreatedAt())
                .updatedAt(Order.getUpdatedAt())
                .build();
    }

    @Override
    public Order toDomain(OrderEntity model) {
        if (model == null) {
            return null;
        }

        return Order.builder()
                .id(model.getId() == null ? null : new OrderId(model.getId()))
                .orderNumber(model.getOrderNumber())
                .supplierId(model.getSupplierId())
                .supplierName(model.getSupplierName())
                .totalAmount(model.getTotalAmount())
                .status(model.getStatus())
                .currency(model.getCurrency() == null ? null : Currency.getInstance(model.getCurrency()))
                .orderDate(model.getOrderDate())
                .expectedDeliveryDate(model.getExpectedDeliveryDate())
                .actualDeliveryDate(model.getActualDeliveryDate())
                .deliveryLocation(model.getDeliveryLocation())
                .items(mapItemsToDomain(model.getItems()))
                .createdBy(model.getCreatedBy() == null ? null : new UserId(model.getCreatedBy()))
                .approvedBy(model.getApprovedBy() == null ? null : new UserId(model.getApprovedBy()))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    @Override
    public List<OrderEntity> fromDomains(List<Order> orders) {
        if (orders == null) {
            return List.of();
        }
        return orders.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<Order> toDomains(List<OrderEntity> orderEntities) {
        if (orderEntities == null) {
            return List.of();
        }
        return orderEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Order> toDomainPage(Page<OrderEntity> modelPage) {
        return modelPage.map(this::toDomain);
    }

    private List<OrderItemEntity> mapItems(List<OrderItem> items, OrderId orderId) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> OrderItemEntity.builder()
                        .id(item.getId())
                        .orderId(orderId == null ? null : orderId.value())
                        .productId(item.getProductId() == null ? null : item.getProductId().value())
                        .productName(item.getProductName())
                        .orderedQuantity(item.getOrderedQuantity())
                        .receivedQuantity(item.getReceivedQuantity())
                        .receivedQuantity(item.getReceivedQuantity())
                        .unitCost(item.getUnitCost())
                        .totalCost(item.getTotalCost())
                        .batchNumber(item.getBatchNumber())
                        .build())
                .toList();
    }

    private List<OrderItem> mapItemsToDomain(List<OrderItemEntity> itemEntities) {
        if (itemEntities == null || itemEntities.isEmpty()) {
            return List.of();
        }

        return itemEntities.stream()
                .map(entity -> OrderItem.builder()
                        .id(entity.getId())
                        .productId(entity.getProductId() == null ? null : new ProductId(entity.getProductId()))
                        .productName(entity.getProductName())
                        .orderedQuantity(entity.getOrderedQuantity())
                        .receivedQuantity(entity.getReceivedQuantity() )
                        .unitCost(entity.getUnitCost())
                        .totalCost(entity.getTotalCost())
                        .batchNumber(entity.getBatchNumber())
                        .build())
                .toList();
    }
}
