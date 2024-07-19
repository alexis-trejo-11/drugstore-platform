package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.CartItemMapper;
import microservice.ecommerce_cart_service.Mappers.CartMapper;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.Factory.CartCalculator;
import microservice.ecommerce_cart_service.Service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartRepository cartRepository;
    private final ExternalProductService productService;
    private final CartItemManager cartItemManager;
    private final CartCalculator cartCalculator;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartItemServiceImpl(CartRepository cartRepository,
                               ExternalProductService productService,
                               CartItemManager cartItemManager,
                               CartCalculator cartCalculator,
                               CartMapper cartMapper,
                               CartItemMapper cartItemMapper) {
        this.cartRepository = cartRepository;
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

        cartItemManager.addOrUpdateCartItem(cart, productDTO, quantity);

        cartCalculator.calculateCartNumbers(cart);
        cartRepository.save(cart);

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
    public Result<CartDTO> processCartAndGetItems(ClientEcommerceDataDTO clientEcommerceDataDTO) {
        Long clientId = clientEcommerceDataDTO.clientDTO.getId();

        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        if (cartOptional.isEmpty()) {
            return Result.error("Cart not found for client id: " + clientId);
        }

        Cart cart = cartOptional.get();
        if (cart.getCartItems().isEmpty()) {
            return Result.error("No products to purchase in the cart");
        }

        CartDTO cartDTO = cartMapper.entityToDTO(cart);
        cartDTO.setCartItems(cart.getCartItems().stream()
                .map(cartItemMapper::entityToDTO)
                .collect(Collectors.toList()));

        cartCalculator.calculateCartNumbers(cart);
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return Result.success(cartDTO);
    }
}