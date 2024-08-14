package microservice.ecommerce_cart_service.Service.DomainService;

import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.AfterwardMapper;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.AfterwardsRepository;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AfterwardsDomainServiceImpl implements AfterwardsDomainService {

    private final AfterwardsRepository afterwardsRepository;
    private final AfterwardMapper afterwardMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartDomainService cartDomainService;

    public AfterwardsDomainServiceImpl(AfterwardsRepository afterwardsRepository,
                                       AfterwardMapper afterwardMapper,
                                       CartRepository cartRepository,
                                       CartItemRepository cartItemRepository,
                                       CartDomainService cartDomainService) {
        this.afterwardsRepository = afterwardsRepository;
        this.afterwardMapper = afterwardMapper;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartDomainService = cartDomainService;
    }


    @Override
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

                cartDomainService.calculateCartNumbers(cart);
                return Result.success();
            } else {
                return Result.error("Product not found in cart");
            }
        });
    }


    @Override
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
