package microservice.inventory.domain.entity.valueobject;

public record StockLevel(
        Integer quantity,
        Integer reorderLevel,
        Integer maximumLevel
) {

    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public boolean isOverstock() {
        return quantity > maximumLevel;
    }
}
