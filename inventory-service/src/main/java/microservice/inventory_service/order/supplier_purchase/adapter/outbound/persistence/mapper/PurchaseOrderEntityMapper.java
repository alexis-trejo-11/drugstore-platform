package microservice.inventory_service.order.supplier_purchase.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory_service.order.supplier_purchase.adapter.outbound.persistence.model.PurchaseOrderEntityOrder;
import microservice.inventory_service.order.supplier_purchase.adapter.outbound.persistence.model.PurchaseOrderItemEntityOrder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrderItem;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.ReconstructPurchaseOrderParams;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PurchaseOrderEntityMapper implements EntityMapper<PurchaseOrderEntityOrder, PurchaseOrder> {
    @Override
    public PurchaseOrderEntityOrder fromDomain(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        var entity = PurchaseOrderEntityOrder.builder()
                .id(purchaseOrder.getId() == null ? null : purchaseOrder.getId().value())
                .supplierId(purchaseOrder.getSupplierId())
                .supplierName(purchaseOrder.getSupplierName())
                .status(purchaseOrder.getStatus())
                .orderDate(purchaseOrder.getOrderDate())
                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                .actualDeliveryDate(purchaseOrder.getActualDeliveryDate())
                .deliveryLocation(purchaseOrder.getDeliveryLocation())
                .createdBy(purchaseOrder.getCreatedBy() == null ? null : purchaseOrder.getCreatedBy().value())
                .approvedBy(purchaseOrder.getApprovedBy() == null ? null : purchaseOrder.getApprovedBy().value())
                .createdAt(purchaseOrder.getCreatedAt())
                .updatedAt(purchaseOrder.getUpdatedAt())
                .deletedAt(purchaseOrder.getDeletedAt())
                .version(purchaseOrder.getVersion())
                .build();

        List<PurchaseOrderItemEntityOrder> itemEntities = mapItems(purchaseOrder.getItems(), entity);
        entity.setItems(itemEntities);

        return entity;    }

    @Override
    public PurchaseOrder toDomain(PurchaseOrderEntityOrder model) {
        if (model == null) {
            return null;
        }

        return PurchaseOrder.reconstruct(
                ReconstructPurchaseOrderParams.builder()
                        .id(model.getId() == null ? null : new PurchaseOrderId(model.getId()))
                        .supplierId(model.getSupplierId())
                        .supplierName(model.getSupplierName())
                        .status(model.getStatus())
                        .orderDate(model.getOrderDate())
                        .expectedDeliveryDate(model.getExpectedDeliveryDate())
                        .actualDeliveryDate(model.getActualDeliveryDate())
                        .deliveryLocation(model.getDeliveryLocation())
                        .items(model.getItems() != null ? mapItemsToDomain((model.getItems())) : null)
                        .createdBy(model.getCreatedBy() == null ? null : new UserId(model.getCreatedBy()))
                        .approvedBy(model.getApprovedBy() == null ? null : new UserId(model.getApprovedBy()))
                        .createdAt(model.getCreatedAt())
                        .updatedAt(model.getUpdatedAt())
                        .deletedAt(model.getDeletedAt())
                        .version(model.getVersion())
                        .build()
                );
    }

    @Override
    public List<PurchaseOrderEntityOrder> fromDomains(List<PurchaseOrder> purchaseOrders) {
        if (purchaseOrders == null) {
            return List.of();
        }
        return purchaseOrders.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<PurchaseOrder> toDomains(List<PurchaseOrderEntityOrder> orderEntities) {
        if (orderEntities == null) {
            return List.of();
        }
        return orderEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<PurchaseOrder> toDomainPage(Page<PurchaseOrderEntityOrder> modelPage) {
        return modelPage.map(this::toDomain);
    }

    private List<PurchaseOrderItemEntityOrder> mapItems(List<PurchaseOrderItem> items, PurchaseOrderEntityOrder order) {
        if (items == null) {
            return List.of();
        }

        List<PurchaseOrderItemEntityOrder> entityOrders = new ArrayList<>();
        for (var item : items) {
            var itemEntity = PurchaseOrderItemEntityOrder.builder()
                    .id(item.getId())
                    .productId(item.getProductId() == null ? null : item.getProductId().value())
                    .productName(item.getProductName())
                    .purchaseOrder(order)
                    .orderedQuantity(item.getOrderedQuantity())
                    .receivedQuantity(item.getReceivedQuantity())
                    .batchNumber(item.getBatchNumber())
                    .version(item.getVersion())
                    .build();
            entityOrders.add(itemEntity);
        }
        return entityOrders;
    }

    private List<PurchaseOrderItem> mapItemsToDomain(List<PurchaseOrderItemEntityOrder> itemEntities) {
        if (itemEntities == null || itemEntities.isEmpty()) return List.of();

        return itemEntities.stream()
                .map(entity -> PurchaseOrderItem.builder()
                        .id(entity.getId())
                        .productId(entity.getProductId() == null ? null : new ProductId(entity.getProductId()))
                        .productName(entity.getProductName())
                        .orderedQuantity(entity.getOrderedQuantity())
                        .receivedQuantity(entity.getReceivedQuantity())
                        .batchNumber(entity.getBatchNumber())
                        .createdAt(entity.getCreatedAt())
                        .updatedAt(entity.getUpdatedAt())
                        .deletedAt(entity.getDeletedAt())
                        .version(entity.getVersion())
                        .build())
                .toList();
    }
}