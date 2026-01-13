package microservice.inventory_service.order.sales.core.domain.entity;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.shared.domain.order.BaseOrderItemDomain;

import java.time.LocalDateTime;

public class SalesOrderItem extends BaseOrderItemDomain<String> {
    public Integer orderedQuantity;
    public Integer receivedQuantity;

    private SalesOrderItem(Reconstructor reconstructor) {
        super(reconstructor.id, reconstructor.createdAt, reconstructor.updatedAt, reconstructor.productId, reconstructor.productName);
        this.orderedQuantity = reconstructor.orderedQuantity;
        this.receivedQuantity = reconstructor.receivedQuantity;

    }


    public SalesOrderItem(String id, ProductId productId, String productName, Integer orderedQuantity, Integer receivedQuantity) {
        super(id, productId, productName);
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
    }

    public static SalesOrderItem create(String id, ProductId productId, String productName, Integer orderedQuantity) {
        return new SalesOrderItem(
                id,
                productId,
                productName,
                orderedQuantity,
                0
        );
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }

    public static Reconstructor reconstructor() {
        return new Reconstructor();
    }


    public static class Reconstructor {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;
        private Integer version;
        private ProductId productId;
        private String productName;
        private Integer orderedQuantity;
        private Integer receivedQuantity;

        public Reconstructor id(String id) {
            this.id = id;
            return this;
        }

        public Reconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Reconstructor updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Reconstructor deletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public Reconstructor version(Integer version) {
            this.version = version;
            return this;
        }

        public Reconstructor productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Reconstructor productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Reconstructor orderedQuantity(Integer orderedQuantity) {
            this.orderedQuantity = orderedQuantity;
            return this;
        }

        public Reconstructor receivedQuantity(Integer receivedQuantity) {
            this.receivedQuantity = receivedQuantity;
            return this;
        }

        public SalesOrderItem reconstruct() {
            return new SalesOrderItem(this);
        }

        public Reconstructor() {
        }
    }
}
