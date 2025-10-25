package microservice.inventory_service.external.order.domain.entity.valueobject;

public record OrderId(String value) {
    public static OrderId of(String value) {
        return new OrderId(value);
    }
    public static OrderId generate() {
        return new OrderId(java.util.UUID.randomUUID().toString());
    }
}
