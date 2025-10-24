package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.EntityDetailMapper;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.response.OrderDetailResponse;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.response.OrderItemResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailMapper implements EntityDetailMapper<PurchaseOrder, OrderDetailResponse> {
    @Override
    public OrderDetailResponse toDetail(PurchaseOrder entity) {
        if (entity == null) {
            return null;
        }
        return OrderDetailResponse.builder()
                .id(entity.getId().value())
                .orderNumber(entity.getOrderNumber())
                .supplierId(entity.getSupplierId())
                .supplierName(entity.getSupplierName())
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .expectedDeliveryDate(entity.getExpectedDeliveryDate())
                .deliveryLocation(entity.getDeliveryLocation())
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy().value() : null)
                .approvedBy(entity.getApprovedBy() != null ? entity.getApprovedBy().value() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(entity.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .medicineId(item.getMedicineId() != null ? item.getMedicineId().value() : null)
                                .medicineName(item.getMedicineName())
                                .orderedQuantity(item.getOrderedQuantity())
                                .receivedQuantity(item.getReceivedQuantity())
                                .unitCost(item.getUnitCost())
                                .totalCost(item.getTotalCost())
                                .batchNumber(item.getBatchNumber())
                                .build())
                        .toList())
                .build();
    }
}
