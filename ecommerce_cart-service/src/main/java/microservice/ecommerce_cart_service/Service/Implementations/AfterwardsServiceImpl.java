package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.AfterwardMapper;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.AfterwardsRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.AfterwardsService;
import microservice.ecommerce_cart_service.Service.Extensions.AfterwardsProcessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AfterwardsServiceImpl implements AfterwardsService {

    private final AfterwardsRepository afterwardsRepository;
    private final CartRepository cartRepository;
    private final AfterwardMapper afterwardMapper;
    private final AfterwardsProcessor afterwardsProcessor;

    public AfterwardsServiceImpl(AfterwardsRepository afterwardsRepository,
                                 CartRepository cartRepository,
                                 AfterwardMapper afterwardMapper,
                                 AfterwardsProcessor afterwardsProcessor) {
        this.afterwardsRepository = afterwardsRepository;
        this.cartRepository = cartRepository;
        this.afterwardMapper = afterwardMapper;
        this.afterwardsProcessor = afterwardsProcessor;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> moveProductToAfterwards(Long clientId, Long productId) {
        return CompletableFuture.supplyAsync(() -> getCartByClientId(clientId))
                .thenCompose(cart -> {
                    if (cart.getCartItems() == null) {
                        return CompletableFuture.completedFuture(Result.error("Cart Item Not Fetched"));
                    }
                    return afterwardsProcessor.processMoveToAfterwards(cart, productId, clientId);
                });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> returnProductToCart(Long clientId, Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Afterward> afterwards = afterwardsRepository.findByClientId(clientId);
            Optional<Afterward> optionalAfterward = afterwards.stream()
                    .filter(afterward -> afterward.getProductId().equals(productId))
                    .findAny();

            return optionalAfterward.map(afterward -> new Result<>(true, afterward, null))
                    .orElseGet(() -> Result.error("Item requested not found"));
        }).thenCompose(result -> {
            if (!result.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error(result.getErrorMessage()));
            }

            Cart cart = getCartByClientId(clientId);
            return afterwardsProcessor.processReturnToAfterwards(cart, result.getData())
                    .thenApply(voidResult -> Result.success());
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<CartItemDTO>> getAfterwardsByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Afterward> afterwards = afterwardsRepository.findByClientId(clientId);
            if (afterwards.isEmpty()) {
                return new ArrayList<>();
            }
            return afterwards.stream().map(afterwardMapper::entityToCartItemDTO).toList();
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Optional<CartItemDTO>> getAfterwardsBytId(Long afterwardsId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Afterward> optionalAfterward = afterwardsRepository.findById(afterwardsId);
            return optionalAfterward.map(afterwardMapper::entityToCartItemDTO);
        });
    }

    private Cart getCartByClientId(Long clientId) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        return cartOptional.orElseThrow(() -> new RuntimeException("Cart not found for client id: " + clientId));
    }
}
