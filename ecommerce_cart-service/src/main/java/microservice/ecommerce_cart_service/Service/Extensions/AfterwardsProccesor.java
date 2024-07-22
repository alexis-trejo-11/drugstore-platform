package microservice.ecommerce_cart_service.Service.Extensions;

import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.AfterwardMapper;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.AfterwardsRepository;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import javax.transaction.Transactional;
import java.util.Optional;

public class AfterwardsProccesor {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AfterwardMapper afterwardMapper;
    private final CartCalculator cartCalculator;
    private final AfterwardsRepository afterwardsRepository;

    @Autowired
    public AfterwardsProccesor(CartRepository cartRepository, CartItemRepository cartItemRepository, AfterwardMapper afterwardMapper, CartCalculator cartCalculator, AfterwardsRepository afterwardsRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.afterwardMapper = afterwardMapper;
        this.cartCalculator = cartCalculator;
        this.afterwardsRepository = afterwardsRepository;
    }

    @Transactional
    @Async
    public Result<Void> processMoveToAfterwards(Cart cart, Long productId, Long clientId) {
        Optional<CartItem> optionalItemToRemove = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst();

        if (optionalItemToRemove.isPresent()) {
            CartItem itemToRemove = optionalItemToRemove.get();

            cart.getCartItems().remove(itemToRemove);
            cartRepository.save(cart);

            cartItemRepository.deleteById(itemToRemove.getId());

            Afterward afterward = afterwardMapper.cartItemToEntity(itemToRemove, clientId);
            afterwardsRepository.save(afterward);

            cartCalculator.calculateCartNumbers(cart);
            return Result.success();
        } else {
            return Result.error("Product requested is not in the cart");
        }
    }
}
