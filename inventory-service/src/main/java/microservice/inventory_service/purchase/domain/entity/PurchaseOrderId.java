package microservice.inventory_service.purchase.domain.entity;

public record PurchaseOrderId(String value) {
    public static PurchaseOrderId of(String value) {
        return new PurchaseOrderId(value);
    }
    public static PurchaseOrderId generate() {
        return new PurchaseOrderId(java.util.UUID.randomUUID().toString());
    }
}
