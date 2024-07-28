package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.*;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.CartItemMapper;
import microservice.ecommerce_cart_service.Mappers.CartMapper;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.AfterwardsRepository;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.Extensions.CartCalculator;
import microservice.ecommerce_cart_service.Service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ExternalProductService productService;
    private final CartItemManager cartItemManager;
    private final CartCalculator cartCalculator;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartItemServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
                               ExternalProductService productService,
                               CartItemManager cartItemManager,
                               CartCalculator cartCalculator,
                               CartMapper cartMapper,
                               CartItemMapper cartItemMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.cartItemManager = cartItemManager;
        this.cartCalculator = cartCalculator;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    @Async
    @Transactional
    public Result<Void> addProductsCart(Long clientId, Long productId, int quantity) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        if (cartOptional.isEmpty()) {
            return Result.error("Cart With Client Id: " + clientId + " Not Found");
        }

        Result<ProductDTO> productResult = productService.getProductById(productId);
        if (!productResult.isSuccess()) {
            return Result.error(productResult.getErrorMessage());
        }

        Cart cart = cartOptional.get();
        ProductDTO productDTO = productResult.getData();

        Cart cartUpdated = cartItemManager.addOrUpdateCartItem(cart, productDTO, quantity);
        if (cart.getCartItems().isEmpty()) {
            return Result.error("No items");
        }

        cartCalculator.calculateCartNumbers(cartUpdated);
        cartRepository.save(cartUpdated);

        return Result.success();
    }

    @Override
    @Async
    @Transactional
    public Result<Void> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        if (cartOptional.isEmpty()) {
            return Result.error("Cart not found for client id: " + clientId);
        }

        Cart cart = cartOptional.get();
        cartItemManager.removeCartItem(cart, cartItemInsertDTO.getProductId(), cartItemInsertDTO.getQuantity());

        cartCalculator.calculateCartNumbers(cart);
        cartRepository.save(cart);

        return Result.success();
    }

    @Override
    @Async
    @Transactional
    public CartDTO processCartAndGePurchaseData(ClientEcommerceDataDTO clientEcommerceDataDTO, PurchaseFromCartDTO purchaseFromCartDTO) {
        Cart cart = getCartByClientId(clientEcommerceDataDTO.getCartDTO().getClientId());

        return purchaseAllItems(cart);
    }

    private Cart getCartByClientId(Long clientId) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        return cartOptional.get();
    }

    private CartDTO purchaseAllItems(Cart cart) {
        // Calculate cart numbers before creating DTO
        Cart CartProccessed = cartCalculator.calculateCartNumbers(cart);

        // Create CartDTO with updated cart information
        var purchaseData = cartMapper.entityToDTO(CartProccessed);

        // Map cart items to DTOs
        List<CartItemDTO> productsToPurchase = CartProccessed.getCartItems().stream()
                .map(cartItemMapper::entityToDTO)
                .collect(Collectors.toList());

        purchaseData.setCartItems(productsToPurchase);

        // Clear cart items after processing
        List<CartItem> itemsToRemove = new ArrayList<>(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // Delete cart items from the database
        cartItemRepository.deleteAll(itemsToRemove);

        // Update numbers
        cartCalculator.calculateCartNumbers(cart);

        return purchaseData;
    }

}