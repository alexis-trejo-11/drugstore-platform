package microservice.ecommerce_cart_service.Service.DomainService;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import microservice.ecommerce_cart_service.Model.Cart;


public interface CartDomainService {
    void calculateCartNumbers(Cart cart);
    void removeItems(Cart cart);
    Cart addOrUpdateCartItem(Cart cart, ProductDTO productDTO, int quantity);
    void removeCartItem(Cart cart, Long productId, int quantity);
}
