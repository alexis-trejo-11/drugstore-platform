package microservice.ecommerce_cart_service.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDetails {
    private ProductDetails productDetails;
    private int quantity;
    private BigDecimal itemTotalPrice;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    public CartItemDetails(int quantity, LocalDateTime addedAt, LocalDateTime updatedAt) {
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.updatedAt = updatedAt;
    }

    public void addProductData(Product product) {
        this.productDetails = new ProductDetails(
                product.productId().toString(),
                product.name(),
                product.price()
        );
    }

    public void calculatePrice() {
        if (this.productDetails == null) {
            this.itemTotalPrice = BigDecimal.ZERO;
        } else  {
            this.itemTotalPrice = this.productDetails.getProductPrice().multiply(new BigDecimal(this.quantity));
        }
    }
}
