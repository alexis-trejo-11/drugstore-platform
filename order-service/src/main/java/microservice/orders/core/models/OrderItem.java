package microservice.orders.core.models;

import lombok.Getter;
import microservice.orders.core.models.valueobjects.Money;
import microservice.orders.core.models.valueobjects.ProductId;

import java.util.Objects;

@Getter
public class OrderItem {
    private final ProductId productId;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;
    private final Money totalPrice;

    public OrderItem(ProductId productId, String productName, Money unitPrice, int quantity) {
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(quantity);
    }

    public static OrderItem create(ProductId productId, String productName, Money unitPrice, int quantity) {
        return new OrderItem(productId, productName, unitPrice, quantity);
    }

    public OrderItem updateQuantity(int newQuantity) {
        return new OrderItem(this.productId, this.productName, this.unitPrice, newQuantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("OrderItem{productId=%s, productName='%s', quantity=%d, unitPrice=%s, totalPrice=%s}",
                productId, productName, quantity, unitPrice, totalPrice);
    }
}
