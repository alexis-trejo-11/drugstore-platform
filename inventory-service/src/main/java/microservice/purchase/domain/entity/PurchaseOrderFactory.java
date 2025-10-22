package microservice.purchase.domain.entity;

import microservice.inventory.domain.entity.valueobject.id.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderFactory {

    public static PurchaseOrder create(String supplierId, String supplierName,
                                      List<PurchaseOrderItem> items,
                                      LocalDateTime expectedDeliveryDate,
                                      String deliveryLocation, UserId createdBy) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Purchase order must have at least one item");
        }

        BigDecimal totalAmount = items.stream()
            .map(PurchaseOrderItem::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        String orderNumber = generateOrderNumber();

        return PurchaseOrder.reconstructor()
            .id(PurchaseOrderId.generate())
            .orderNumber(orderNumber)
            .supplierId(supplierId)
            .supplierName(supplierName)
            .items(items)
            .totalAmount(totalAmount)
            .status(PurchaseOrderStatus.DRAFT)
            .orderDate(LocalDateTime.now())
            .expectedDeliveryDate(expectedDeliveryDate)
            .deliveryLocation(deliveryLocation)
            .createdBy(createdBy)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .reconstruct();
    }

    private static String generateOrderNumber() {
        return "PO-" + System.currentTimeMillis();
    }
}