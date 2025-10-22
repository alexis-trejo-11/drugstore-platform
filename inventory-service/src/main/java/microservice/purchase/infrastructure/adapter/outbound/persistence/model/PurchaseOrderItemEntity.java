package microservice.purchase.infrastructure.adapter.outbound.persistence.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "purchase_order_id", length = 36)
    private String purchaseOrderId;

    @Column(name = "medicine_id", nullable = false, length = 36)
    private String medicineId;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity;

    @Column(name = "unit_cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalCost;

    @Column(name = "batch_number")
    private String batchNumber;
}