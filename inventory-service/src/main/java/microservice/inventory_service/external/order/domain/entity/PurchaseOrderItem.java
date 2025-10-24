package microservice.inventory_service.external.order.domain.entity;

import java.math.BigDecimal;

import lombok.Getter;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;


@Getter
public class PurchaseOrderItem {
    private String id;
    private MedicineId medicineId;
    private String medicineName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String batchNumber;

    private PurchaseOrderItem(String id, MedicineId medicineId, String medicineName, Integer orderedQuantity, Integer receivedQuantity, BigDecimal unitCost, BigDecimal totalCost, String batchNumber) {
        this.id = id;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.batchNumber = batchNumber;
    }

    public static PurchaseOrderItem create(MedicineId medicineId, String medicineName,
                                           Integer quantity, BigDecimal unitCost) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitCost.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit cost must be positive");
        }

        BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));

        return PurchaseOrderItem.reconstructor()
                .medicineId(medicineId)
                .medicineName(medicineName)
                .orderedQuantity(quantity)
                .receivedQuantity(0)
                .unitCost(unitCost)
                .totalCost(totalCost)
                .reconstruct();
    }

    public void receiveQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Received quantity must be positive");
        }
        if (this.receivedQuantity + quantity > this.orderedQuantity) {
            throw new IllegalStateException("Cannot receive more than ordered quantity");
        }
        this.receivedQuantity += quantity;
    }

    public void assignBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public boolean isFullyReceived() {
        return this.receivedQuantity.equals(this.orderedQuantity);
    }

    public Integer getRemainingQuantity() {
        return this.orderedQuantity - this.receivedQuantity;
    }

    public static PurchaseOrderItemReconstructor reconstructor() {
        return new PurchaseOrderItemReconstructor();
    }

    public static class PurchaseOrderItemReconstructor {
        private String id;
        private MedicineId medicineId;
        private String medicineName;
        private Integer orderedQuantity;
        private Integer receivedQuantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private String batchNumber;

        public PurchaseOrderItemReconstructor id(String id) {
            this.id = id;
            return this;
        }

        public PurchaseOrderItemReconstructor medicineId(MedicineId medicineId) {
            this.medicineId = medicineId;
            return this;
        }

        public PurchaseOrderItemReconstructor medicineName(String medicineName) {
            this.medicineName = medicineName;
            return this;
        }

        public PurchaseOrderItemReconstructor orderedQuantity(Integer orderedQuantity) {
            this.orderedQuantity = orderedQuantity;
            return this;
        }

        public PurchaseOrderItemReconstructor receivedQuantity(Integer receivedQuantity) {
            this.receivedQuantity = receivedQuantity;
            return this;
        }

        public PurchaseOrderItemReconstructor unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public PurchaseOrderItemReconstructor totalCost(BigDecimal totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public PurchaseOrderItemReconstructor batchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
            return this;
        }

        public PurchaseOrderItem reconstruct() {
            return new PurchaseOrderItem(id, medicineId, medicineName, orderedQuantity, receivedQuantity, unitCost, totalCost, batchNumber);
        }
    }
}

