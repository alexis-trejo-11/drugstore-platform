package microservice.ecommerce_cart_service.Model;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Table(name = "cart_items")
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "item_total")
    private BigDecimal itemTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void calculateItemTotal() {
        this.itemTotal = this.productPrice.multiply(new BigDecimal(this.quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(productId, cartItem.productId);
    }

    public CartItem createCartItem(ProductDTO productDTO, int quantity, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(productDTO.getId());
        cartItem.setProductName(productDTO.getName());
        cartItem.setProductPrice(productDTO.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setItemTotal(productDTO.getPrice().multiply(new BigDecimal(quantity)));
        cartItem.setCart(cart);
        return cartItem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
