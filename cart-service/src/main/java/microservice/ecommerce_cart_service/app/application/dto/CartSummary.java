package microservice.ecommerce_cart_service.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.Product;
import microservice.ecommerce_cart_service.app.shared.PageMetadata;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartSummary {
    private String id;
    private String customerId;
    private BigDecimal subtotal;
    private LocalDateTime updatedAt;
    private List<CartItemDetails> itemDetails;
    private PageMetadata itemsMetadata;

    public static CartSummary from(Cart cart, Page<CartItem> cartItemPage){
        CartSummary response = CartSummary.builder()
                .id(cart.getId().toString())
                .customerId(cart.getCustomerId().toString())
                .subtotal(BigDecimal.ZERO)
                .updatedAt(cart.getUpdatedAt())
                .build();

        if (!cart.getCartItems().isEmpty()) {
            List<CartItemDetails> cartItemResponses = cartItemPage
                    .map(cartItem -> new CartItemDetails(
                            cart.getTotalItems(),
                            cartItem.getCreatedAt(),
                            cartItem.getUpdatedAt()
                    )
            ).toList();
            response.setItemDetails(cartItemResponses);
        }

        return response;
    }


    public static CartSummary emptyCart(Cart cart){
        return CartSummary.builder()
                .id(cart.getId().toString())
                .customerId(cart.getCustomerId().toString())
                .subtotal(BigDecimal.ZERO)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public void appendProductData(List<Product> products) {
        Map<String, Product> productMap = new HashMap<>();
        products.forEach(product -> productMap.put(product.productId().toString(), product));

        for (var item: this.itemDetails){
            Product product = productMap.get(item.getProductDetails().getProductId());
            item.addProductData(product);
        }
    }


    public void calculatePrices() {
        calculateItemsNumber();
        calculateSubtotal();
    }

    private void calculateItemsNumber() {
        this.itemDetails.stream().toList().forEach(CartItemDetails::calculatePrice);
    }

    private void calculateSubtotal() {
        BigDecimal itemTotalSum = BigDecimal.ZERO;
        for (var item : this.itemDetails) {
            itemTotalSum = itemTotalSum.add(item.getItemTotalPrice());
        }
        this.setSubtotal(itemTotalSum);
    }
}