package microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.EntityDetailMapper;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.response.PurchaseOrderResponse;
import microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.response.PurchaseOrderItemResponse;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderDetailMapper implements EntityDetailMapper<PurchaseOrder, PurchaseOrderResponse> {
    @Override
    public PurchaseOrderResponse toDetail(PurchaseOrder entity) {
        if (entity == null) {
            return null;
        }
        return PurchaseOrderResponse.builder()
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
                        .map(item -> PurchaseOrderItemResponse.builder()
                                .productId(item.getProductId() != null ? item.getProductId().value() : null)
                                .productName(item.getProductName())
                                .orderedQuantity(item.getOrderedQuantity())
                                .receivedQuantity(item.getReceivedQuantity())
                                .batchNumber(item.getBatchNumber())
                                .build())
                        .toList())
                .build();
    }
}
