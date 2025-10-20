package microservice.purchase.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.inventory.domain.entity.valueobject.id.UserID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    private String id;
    private String orderNumber;
    private String supplierId;
    private String supplierName;
    private List<PurchaseOrderItem> items;
    private BigDecimal totalAmount;
    private PurchaseOrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String deliveryLocation;
    private UserID createdBy;
    private UserID approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
