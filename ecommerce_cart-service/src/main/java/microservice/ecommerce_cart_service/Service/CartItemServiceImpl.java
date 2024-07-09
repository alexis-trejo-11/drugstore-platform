package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductServiceImpl;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mapper.CartItemMapper;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ExternalProductService productService;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartItemServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ExternalProductServiceImpl productService, CartItemMapper cartItemMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
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

        List<CartItem> cartItems = cart.getCartItems();

        Optional<CartItem> existingCartItem = cartItems.stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update quantity if item exists
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        } else  {
            // Create a new cart item if it doesn't exist
            CartItem newCartItem = cartItemMapper.productDtoToCartItem(productDTO, quantity, cart);
            cartItems.add(newCartItem);
        }

        // Calculate New Total
        cart.setTotalPrice(calculateCartTotal(cart));

        cartRepository.saveAndFlush(cart);

        return Result.success(null);
    }

    @Override
    @Async
    @Transactional
    public Result<Void> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);

        // Remove Product From Cart
        Cart cart = cartOptional.get();
        List<CartItem> cartItems = cart.getCartItems();

        // Find the Cart Item with the given product ID
        Optional<CartItem> optionalCartItem = cartItems.stream()
                .filter(cartItem -> cartItem.getProductId().equals(cartItemInsertDTO.getProductId()))
                .findFirst();

        if (optionalCartItem.isEmpty()) {
            return Result.error("Product With Id: " + cartItemInsertDTO.getProductId() + " Not Found In Cart");
        }

        CartItem cartItem = optionalCartItem.get();

        // Remove item or reduce quantity
        if (cartItem.getQuantity() <= cartItemInsertDTO.getQuantity()) {
            cartItems.remove(cartItem); // Remove from the cart's list of items
            cartItemRepository.deleteById(cartItem.getId()); // Remove from the database
        } else {
            // Reduce quantity
            cartItem.setQuantity(cartItem.getQuantity() - cartItemInsertDTO.getQuantity());
            cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            cartItemRepository.saveAndFlush(cartItem);
        }

        // Calculate new total price
        cart.setTotalPrice(calculateCartTotal(cart));

        cartRepository.saveAndFlush(cart);

        return Result.success();
    }

    @Override
    @Async
    @Transactional
    public Result<List<CartItemDTO>> processCartAndGetItems(ClientEcommerceDataDTO clientEcommerceDataDTO) {
        Long clientId = clientEcommerceDataDTO.clientDTO.getId();

        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        if (!cartOptional.isPresent()) {
            return new Result<>(false, null, "Cart not found for client id: " + clientId, HttpStatus.NOT_FOUND);
        }

        Cart cart = cartOptional.get();
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()) {
            return new Result<>(false, null, "No products to purchase in the cart", HttpStatus.BAD_REQUEST);
        }

        List<CartItemDTO> cartItemDTOS = mapCartItemsToDTOs(cartItems);
        updateCartTotal(cart);
        clearCartItems(cart, cartItems);

        return Result.success(cartItemDTOS);
    }


    private List<CartItemDTO> mapCartItemsToDTOs(List<CartItem> cartItems) {
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            CartItemDTO cartItemDTO = cartItemMapper.entityToDTO(cartItem);
            cartItemDTOS.add(cartItemDTO);
        }
        return cartItemDTOS;
    }

    private void updateCartTotal(Cart cart) {
        cart.setTotalPrice(calculateCartTotal(cart));
        cartRepository.saveAndFlush(cart);
    }

    private void clearCartItems(Cart cart, List<CartItem> cartItems) {
        cartItemRepository.deleteAll(cartItems);
        cart.getCartItems().clear();
        cartRepository.saveAndFlush(cart);
    }

    private BigDecimal calculateCartTotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
