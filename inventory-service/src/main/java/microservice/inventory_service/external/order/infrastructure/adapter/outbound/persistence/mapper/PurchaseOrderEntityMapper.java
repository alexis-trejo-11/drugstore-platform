package microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderItem;
import microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.model.PurchaseOrderEntity;
import microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.model.PurchaseOrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseOrderEntityMapper implements EntityMapper<PurchaseOrderEntity, PurchaseOrder> {

    @Override
    public PurchaseOrderEntity fromDomain(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        return PurchaseOrderEntity.builder()
                .id(purchaseOrder.getId() == null ? null : purchaseOrder.getId().value())
                .orderNumber(purchaseOrder.getOrderNumber())
                .supplierId(purchaseOrder.getSupplierId())
                .supplierName(purchaseOrder.getSupplierName())
                .items(mapItems(purchaseOrder.getItems(), purchaseOrder.getId()))
                .totalAmount(purchaseOrder.getTotalAmount())
                .status(purchaseOrder.getStatus())
                .orderDate(purchaseOrder.getOrderDate())
                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                .actualDeliveryDate(purchaseOrder.getActualDeliveryDate())
                .deliveryLocation(purchaseOrder.getDeliveryLocation())
                .createdBy(purchaseOrder.getCreatedBy() == null ? null : purchaseOrder.getCreatedBy().value())
                .approvedBy(purchaseOrder.getApprovedBy() == null ? null : purchaseOrder.getApprovedBy().value())
                .createdAt(purchaseOrder.getCreatedAt())
                .updatedAt(purchaseOrder.getUpdatedAt())
                .build();
    }

    @Override
    public PurchaseOrder toDomain(PurchaseOrderEntity model) {
        if (model == null) {
            return null;
        }

        return PurchaseOrder.reconstructor()
                .id(model.getId() == null ? null : new PurchaseOrderId(model.getId()))
                .orderNumber(model.getOrderNumber())
                .supplierId(model.getSupplierId())
                .supplierName(model.getSupplierName())
                .totalAmount(model.getTotalAmount())
                .status(model.getStatus())
                .orderDate(model.getOrderDate())
                .expectedDeliveryDate(model.getExpectedDeliveryDate())
                .actualDeliveryDate(model.getActualDeliveryDate())
                .deliveryLocation(model.getDeliveryLocation())
                .items(mapItemsToDomain(model.getItems()))
                .createdBy(model.getCreatedBy() == null ? null : new UserId(model.getCreatedBy()))
                .approvedBy(model.getApprovedBy() == null ? null : new UserId(model.getApprovedBy()))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .reconstruct();
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
    public List<PurchaseOrder> toDomains(List<PurchaseOrderEntity> purchaseOrderEntities) {
        if (purchaseOrderEntities == null) {
            return List.of();
        }
        return purchaseOrderEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<PurchaseOrder> toDomainPage(Page<PurchaseOrderEntity> modelPage) {
        return modelPage.map(this::toDomain);
    }

    private List<PurchaseOrderItemEntity> mapItems(List<PurchaseOrderItem> items, PurchaseOrderId purchaseOrderId) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> PurchaseOrderItemEntity.builder()
                        .id(item.getId())
                        .purchaseOrderId(purchaseOrderId == null ? null : purchaseOrderId.value())
                        .medicineId(item.getMedicineId() == null ? null : item.getMedicineId().value())
                        .medicineName(item.getMedicineName())
                        .orderedQuantity(item.getOrderedQuantity())
                        .receivedQuantity(item.getReceivedQuantity())
                        .receivedQuantity(item.getReceivedQuantity())
                        .unitCost(item.getUnitCost())
                        .totalCost(item.getTotalCost())
                        .batchNumber(item.getBatchNumber())
                        .build())
                .toList();
    }

    private List<PurchaseOrderItem> mapItemsToDomain(List<PurchaseOrderItemEntity> itemEntities) {
        if (itemEntities == null) {
            return List.of();
        }
        return itemEntities.stream()
                .map(entity -> PurchaseOrderItem.reconstructor()
                        .id(entity.getId())
                        .medicineId(entity.getMedicineId() == null ? null : new MedicineId(entity.getMedicineId()))
                        .medicineName(entity.getMedicineName())
                        .orderedQuantity(entity.getOrderedQuantity())
                        .receivedQuantity(entity.getReceivedQuantity())
                        .unitCost(entity.getUnitCost())
                        .totalCost(entity.getTotalCost())
                        .batchNumber(entity.getBatchNumber())
                        .reconstruct())
                .toList();
    }
}
