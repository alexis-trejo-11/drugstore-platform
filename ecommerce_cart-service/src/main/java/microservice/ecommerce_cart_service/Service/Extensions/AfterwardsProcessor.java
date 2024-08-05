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
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class AfterwardsProcessor {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AfterwardMapper afterwardMapper;
    private final CartCalculator cartCalculator;
    private final AfterwardsRepository afterwardsRepository;

    @Autowired
    public AfterwardsProcessor(CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               AfterwardMapper afterwardMapper,
                               CartCalculator cartCalculator,
                               AfterwardsRepository afterwardsRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.afterwardMapper = afterwardMapper;
        this.cartCalculator = cartCalculator;
        this.afterwardsRepository = afterwardsRepository;
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> processMoveToAfterwards(Cart cart, Long productId, Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<CartItem> optionalItemToRemove = cart.getCartItems().stream()
                    .filter(cartItem -> cartItem.getProductId().equals(productId))
                    .findFirst();

            // Look At Unique Afterward Item
            List<Afterward> afterwards = afterwardsRepository.findByClientId(clientId);
            Optional<Afterward> optionalAfterward = afterwards.stream().filter(afterward -> afterward.getProductId().equals(productId)).findAny();
            if (optionalAfterward.isPresent()) {
                return Result.error("Product already on Afterwards");
            }

            // Look At Existing Product on Cart
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
                return Result.error("Product not found in cart");
            }
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> processReturnToAfterwards(Cart cart, Afterward afterward) {
        return CompletableFuture.runAsync(() -> {
            CartItem cartItem = afterwardMapper.entityToCartItem(afterward);
            cart.getCartItems().add(cartItem);
            cartRepository.save(cart);
            afterwardsRepository.delete(afterward);
        });
    }
}
