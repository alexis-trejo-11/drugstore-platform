package microservice.inventory_service.internal.purachse_order.domain.entity.valueobject;

public record PurchaseOrderId(String value) {
    public static PurchaseOrderId of(String value) {
        return new PurchaseOrderId(value);
    }
    public static PurchaseOrderId generate() {
        return new PurchaseOrderId(java.util.UUID.randomUUID().toString());
    }
}
