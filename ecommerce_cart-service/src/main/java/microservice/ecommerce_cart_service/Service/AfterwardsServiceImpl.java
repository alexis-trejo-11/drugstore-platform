package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.AfterwardMapper;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.AfterwardsRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.DomainService.AfterwardsDomainService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AfterwardsServiceImpl implements AfterwardsService {

    private final AfterwardsRepository afterwardsRepository;
    private final CartRepository cartRepository;
    private final AfterwardMapper afterwardMapper;
    private final AfterwardsDomainService afterwardsDomainService;

    public AfterwardsServiceImpl(AfterwardsRepository afterwardsRepository,
                                 CartRepository cartRepository,
                                 AfterwardMapper afterwardMapper,
                                 AfterwardsDomainService afterwardsDomainService) {
        this.afterwardsRepository = afterwardsRepository;
        this.cartRepository = cartRepository;
        this.afterwardMapper = afterwardMapper;
        this.afterwardsDomainService = afterwardsDomainService;
    }

    @Override
    @Transactional
    public Result<Void> moveProductToAfterwards(Long clientId, Long productId) {
         Cart cart = getCartByClientId(clientId);
        if (cart.getCartItems() == null) {
            return Result.error("Cart Item Not Fetched");
        }
        return afterwardsDomainService.processMoveToAfterwards(cart, productId, clientId);
    }

    @Override
    @Transactional
    public Result<Void> returnProductToCart(Long clientId, Long productId) {

            List<Afterward> afterwards = afterwardsRepository.findByClientId(clientId);
            Optional<Afterward> optionalAfterward = afterwards.stream()
                    .filter(afterward -> afterward.getProductId().equals(productId))
                    .findAny();

            Result<Afterward> result =  optionalAfterward.map(afterward -> new Result<>(true, afterward, null))
                    .orElseGet(() -> Result.error("Item requested not found"));

        if (!result.isSuccess()) {
            return Result.error(result.getErrorMessage());
        }

        Cart cart = getCartByClientId(clientId);
        afterwardsDomainService.processReturnToAfterwards(cart, result.getData());
        return Result.success();
    }

    @Override
    @Transactional
    public List<CartItemDTO> getAfterwardsByClientId(Long clientId) {
            List<Afterward> afterwards = afterwardsRepository.findByClientId(clientId);


            return afterwards.stream().map(afterwardMapper::entityToCartItemDTO).toList();
    }

    @Override
    @Transactional
    public Optional<CartItemDTO> getAfterwardsBytId(Long afterwardsId) {
            Optional<Afterward> optionalAfterward = afterwardsRepository.findById(afterwardsId);
            return optionalAfterward.map(afterwardMapper::entityToCartItemDTO);
    }

    private Cart getCartByClientId(Long clientId) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        return cartOptional.orElseThrow(() -> new RuntimeException("Cart not found for client id: " + clientId));
    }
}
