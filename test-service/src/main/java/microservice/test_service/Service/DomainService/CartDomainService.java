package microservice.test_service.Service.DomainService;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import microservice.test_service.Model.Cart;


public interface CartDomainService {
    Cart calculateCartNumbers(Cart cart);
    CartDTO purchaseAllItems(Cart cart);
    Cart addOrUpdateCartItem(Cart cart, ProductDTO productDTO, int quantity);
    void removeCartItem(Cart cart, Long productId, int quantity);
}
