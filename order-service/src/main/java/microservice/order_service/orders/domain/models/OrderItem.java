package microservice.order_service.orders.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    private Long id;
    private OrderID orderID;
    private ProductID productID;
    private String productName;
    private int quantity;
    private Money subtotal;
    private boolean prescriptionRequired;

    public OrderItem(ProductID productID, String productName, Money subtotal, int quantity, boolean prescriptionRequired) {
        this.productID = Objects.requireNonNull(productID, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product firsName cannot be null");
        this.subtotal = Objects.requireNonNull(subtotal, "Subtotal cannot be null");
        this.prescriptionRequired = prescriptionRequired;

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        this.quantity = quantity;
    }

    public static OrderItem create(ProductID productID, String productName, Money unitPrice, int quantity, boolean prescriptionRequired) {
        return new OrderItem(productID, productName, unitPrice, quantity, prescriptionRequired);
    }

    public OrderItem updateQuantity(int newQuantity) {
        return new OrderItem(this.productID, this.productName, this.subtotal, newQuantity, this.prescriptionRequired);
    }

    public void assignOrder(OrderID orderID) {
        this.orderID = orderID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(productID, orderItem.productID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID);
    }

    @Override
    public String toString() {
        return String.format("OrderItem{productID=%s, productName='%s', quantity=%d, subtotal=%s}",
                productID, productName, quantity, subtotal);
    }
}