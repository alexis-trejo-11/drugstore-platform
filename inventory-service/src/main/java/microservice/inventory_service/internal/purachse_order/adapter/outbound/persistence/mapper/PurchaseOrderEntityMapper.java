package microservice.inventory_service.internal.purachse_order.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory_service.internal.purachse_order.adapter.outbound.persistence.model.PurchaseOrderEntity;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrderItem;
import microservice.inventory_service.internal.purachse_order.adapter.outbound.persistence.model.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

@Component
public class PurchaseOrderEntityMapper implements EntityMapper<PurchaseOrderEntity, PurchaseOrder> {

    @Override
    public PurchaseOrderEntity fromDomain(PurchaseOrder PurchaseOrder) {
        if (PurchaseOrder == null) {
            return null;
        }

        return PurchaseOrderEntity.builder()
                .id(PurchaseOrder.getId() == null ? null : PurchaseOrder.getId().value())
                .orderNumber(PurchaseOrder.getOrderNumber())
                .supplierId(PurchaseOrder.getSupplierId())
                .supplierName(PurchaseOrder.getSupplierName())
                .items(mapItems(PurchaseOrder.getItems(), PurchaseOrder.getId()))
                .totalAmount(PurchaseOrder.getTotalAmount())
                .status(PurchaseOrder.getStatus())
                .orderDate(PurchaseOrder.getOrderDate())
                .currency(PurchaseOrder.getCurrency() == null ? null : PurchaseOrder.getCurrency().getCurrencyCode())
                .expectedDeliveryDate(PurchaseOrder.getExpectedDeliveryDate())
                .actualDeliveryDate(PurchaseOrder.getActualDeliveryDate())
                .deliveryLocation(PurchaseOrder.getDeliveryLocation())
                .createdBy(PurchaseOrder.getCreatedBy() == null ? null : PurchaseOrder.getCreatedBy().value())
                .approvedBy(PurchaseOrder.getApprovedBy() == null ? null : PurchaseOrder.getApprovedBy().value())
                .createdAt(PurchaseOrder.getCreatedAt())
                .updatedAt(PurchaseOrder.getUpdatedAt())
                .build();
    }

    @Override
    public PurchaseOrder toDomain(PurchaseOrderEntity model) {
        if (model == null) {
            return null;
        }

        return PurchaseOrder.builder()
                .id(model.getId() == null ? null : new PurchaseOrderId(model.getId()))
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
    public List<PurchaseOrderEntity> fromDomains(List<PurchaseOrder> purchaseOrders) {
        if (purchaseOrders == null) {
            return List.of();
        }
        return purchaseOrders.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<PurchaseOrder> toDomains(List<PurchaseOrderEntity> orderEntities) {
        if (orderEntities == null) {
            return List.of();
        }
        return orderEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<PurchaseOrder> toDomainPage(Page<PurchaseOrderEntity> modelPage) {
        return modelPage.map(this::toDomain);
    }

    private List<OrderItemEntity> mapItems(List<PurchaseOrderItem> items, PurchaseOrderId purchaseOrderId) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> OrderItemEntity.builder()
                        .id(item.getId())
                        .orderId(purchaseOrderId == null ? null : purchaseOrderId.value())
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

    private List<PurchaseOrderItem> mapItemsToDomain(List<OrderItemEntity> itemEntities) {
        if (itemEntities == null || itemEntities.isEmpty()) {
            return List.of();
        }

        return itemEntities.stream()
                .map(entity -> PurchaseOrderItem.builder()
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
