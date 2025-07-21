package microservice.ecommerce_cart_service.app.domain.port.out.repositories;

import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartItemId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface CartItemRepository {
    void bulkSave(List<CartItem> cartItem);
    void bulkDelete(List<CartItem> cartItem);
    List<CartItem> listByCartId(CartId cartId, QueryPageData pageData);
    List<CartItem> listByCartId(CartId cartId);
    List<CartItem> listByCartIdAndProductIdIn(CartId cartId, Set<ProductId> productIds);
}