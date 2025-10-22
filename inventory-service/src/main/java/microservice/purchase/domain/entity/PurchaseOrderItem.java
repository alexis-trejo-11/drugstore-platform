package microservice.purchase.domain.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.inventory.domain.entity.valueobject.id.MedicineId;


@Getter
public class PurchaseOrderItem {
    private int id;
    private MedicineId medicineId;
    private String medicineName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String batchNumber;

    private PurchaseOrderItem(int id, MedicineId medicineId, String medicineName, Integer orderedQuantity, Integer receivedQuantity, BigDecimal unitCost, BigDecimal totalCost, String batchNumber) {
        this.id = id;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.batchNumber = batchNumber;
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
        private int id;
        private MedicineId medicineId;
        private String medicineName;
        private Integer orderedQuantity;
        private Integer receivedQuantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private String batchNumber;

        public PurchaseOrderItemReconstructor id(int id) {
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

