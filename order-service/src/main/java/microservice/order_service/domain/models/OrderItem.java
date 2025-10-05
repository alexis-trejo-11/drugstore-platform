package microservice.order_service.domain.models;

import lombok.Builder;
import lombok.Getter;
import microservice.order_service.domain.models.valueobjects.Money;
import microservice.order_service.domain.models.valueobjects.ProductID;

import java.util.Objects;

@Getter
public class OrderItem {
    private final ProductID productId;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;
    private final Money subtotal;
    private final boolean prescriptionRequired;

    @Builder
    public OrderItem(ProductID productId, String productName, Money unitPrice, int quantity, boolean prescriptionRequired) {
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        this.prescriptionRequired = prescriptionRequired;

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(quantity);
    }

    public static OrderItem create(ProductID productId, String productName, Money unitPrice, int quantity, boolean prescriptionRequired) {
        return new OrderItem(productId, productName, unitPrice, quantity, prescriptionRequired);
    }

    public OrderItem updateQuantity(int newQuantity) {
        return new OrderItem(this.productId, this.productName, this.unitPrice, newQuantity, this.prescriptionRequired);
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
        return String.format("OrderItem{productId=%s, productName='%s', quantity=%d, unitPrice=%s, subTotal=%s}",
                productId, productName, quantity, unitPrice, subtotal);
    }
}