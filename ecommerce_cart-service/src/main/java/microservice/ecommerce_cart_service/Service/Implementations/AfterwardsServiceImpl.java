package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.AfterwardMapper;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.AfterwardsRepository;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.AfterwardsService;
import microservice.ecommerce_cart_service.Service.Extensions.AfterwardsProccesor;
import microservice.ecommerce_cart_service.Service.Extensions.CartCalculator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AfterwardsServiceImpl implements AfterwardsService {

    private final AfterwardsRepository afterwardsRepository;
    private final CartRepository cartRepository;
    private final AfterwardMapper afterwardMapper;
    private final AfterwardsProccesor afterwardsProccesor;

    public AfterwardsServiceImpl(AfterwardsRepository afterwardsRepository,
                                 CartRepository cartRepository,
                                 AfterwardMapper afterwardMapper,
                                 AfterwardsProccesor afterwardsProccesor) {
        this.afterwardsRepository = afterwardsRepository;
        this.cartRepository = cartRepository;
        this.afterwardMapper = afterwardMapper;
        this.afterwardsProccesor = afterwardsProccesor;
    }

    @Override
    @Async
    @Transactional
    public Result<Void> moveProductToAfterwards(Long clientId, Long productId) {
        Cart cart = getCartByClientId(clientId);
        return afterwardsProccesor.processMoveToAfterwards(cart, clientId, productId);
    }

    @Override
    @Async
    @Transactional
    public void returnProductToCart(Long clientId, Long productId) {
        Cart cart = getCartByClientId(clientId);
        afterwardsProccesor.processMoveToAfterwards(cart, clientId, productId);
    }

    @Override
    @Async
    @Transactional
    public List<CartItemDTO> getAfterwardsByClientId(Long clientId) {
        List<Afterward> afterwards = afterwardsRepository.findByClientId(clientId);
        if (afterwards.isEmpty()) {
            return new ArrayList<>();
        }

        return afterwards.stream().map(afterwardMapper::entityToCartItemDTO).toList();
    }

    @Async
    @Transactional
    public Optional<CartItemDTO> getAfterwardsBytId(Long afterwardsId) {
        Optional<Afterward> optionalAfterward = afterwardsRepository.findById(afterwardsId);
        return optionalAfterward.map(afterwardMapper::entityToCartItemDTO);
    }

    private Cart getCartByClientId(Long clientId) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        Cart cart = cartOptional.get();
        List<CartItem> cartItems = cart.getCartItems();
        return cart;
    }
}
