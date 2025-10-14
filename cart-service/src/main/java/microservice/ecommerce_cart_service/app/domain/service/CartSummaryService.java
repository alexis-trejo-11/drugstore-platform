package microservice.ecommerce_cart_service.app.domain.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.application.dto.CartItemDetails;
import microservice.ecommerce_cart_service.app.application.dto.CartSummary;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.Product;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.ProductFacadeService;
import microservice.ecommerce_cart_service.app.shared.PageMetadata;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class CartSummaryService {
    private final ProductFacadeService productFacadeService;

    public CartSummary createCartSummary(Cart cart) {
        return getEmptySummary(cart);
    }

    public CartSummary createCartSummary(Cart cart, Page<CartItem> cartItemPage) {
         CartSummary cartSummary = getEmptySummary(cart);

         appendItemDetails(cartSummary, cartItemPage);
         appendProductData(cartSummary, cartItemPage);
         cartSummary.calculatePrices();

         return cartSummary;
    }

    public void appendItemDetails(CartSummary cartSummary, Page<CartItem> cartItem) {
        if (cartSummary.getItemDetails() == null) {
            cartSummary.setItemDetails(List.of());
        }

        cartSummary.setItemDetails( cartItem
                .map(item -> new CartItemDetails(
                        item.getQuantity(),
                        item.getCreatedAt(),
                        item.getUpdatedAt()
                )).toList()
        );

        PageMetadata metadata = PageMetadata.fromPage(cartItem);
        cartSummary.setItemsMetadata(metadata);
    }

    public void appendProductData(CartSummary cartSummary, Page<CartItem> cartItem) {
        List<Product> products = productFacadeService.findByIdIn(cartItem.map(CartItem::getProductId).toList());
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(product -> product.productId().toString(), Function.identity()));

        for (var item : cartSummary.getItemDetails()) {
            Product product = productMap.get(item.getProductDetails().getProductId());
            if (product != null) {
                item.addProductData(product);
                item.calculatePrice();
            } else {
                item.setItemTotalPrice(BigDecimal.ZERO);
            }
        }
    }

    public CartSummary getEmptySummary(Cart cart) {
        return CartSummary.builder()
                .id(cart.getId().toString())
                .customerId(cart.getCustomerId().toString())
                .subtotal(BigDecimal.ZERO)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
