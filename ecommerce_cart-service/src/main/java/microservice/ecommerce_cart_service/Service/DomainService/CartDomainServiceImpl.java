package microservice.ecommerce_cart_service.Service.DomainService;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import microservice.ecommerce_cart_service.Mappers.CartDtoMapper;
import microservice.ecommerce_cart_service.Mappers.CartItemMapper;
import microservice.ecommerce_cart_service.Mappers.CartMapper;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartDomainServiceImpl implements CartDomainService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartDtoMapper cartDtoMapper;

    @Autowired
    public CartDomainServiceImpl(CartMapper cartMapper,
                                 CartItemMapper cartItemMapper,
                                 CartItemRepository cartItemRepository,
                                 CartRepository cartRepository,
                                 CartDtoMapper cartDtoMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.cartDtoMapper = cartDtoMapper;
    }

    public Cart calculateCartNumbers(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        calculateSubTotal(cart);
        calculateItemsSubtotal(cart);

        return cart;
    }

    public CartDTO purchaseAllItems(Cart cart) {
        // Calculate cart numbers before creating DTOs
        Cart CartProcessed = calculateCartNumbers(cart);

        // Create CartDTO with updated cart information
        var purchaseData = cartMapper.entityToDTO(CartProcessed);

        // Map cart items to DTOs
        List<CartItemDTO> productsToPurchase = CartProcessed.getCartItems().stream()
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
        calculateCartNumbers(cart);

        return purchaseData;
    }

    private void calculateSubTotal(Cart cart) {
        BigDecimal subtotal = cart.getCartItems().stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        cart.setSubtotal(subtotal);
    }

    private void calculateItemsSubtotal(Cart cart) {
        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem : cartItems) {
            BigDecimal productPrice = cartItem.getProductPrice();
            int quantity = cartItem.getQuantity();
            BigDecimal itemTotal = productPrice.multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);
            cartItem.setItemTotal(itemTotal);
        }
    }

    public Cart addOrUpdateCartItem(Cart cart, ProductDTO productDTO, int quantity) {
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productDTO.getId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
            return cart;
        } else {
            CartItem newCartItem = cartDtoMapper.createCartItem(productDTO, quantity, cart);
            cartItemRepository.save(newCartItem);
            cart.getCartItems().add(newCartItem);
            return cart;
        }
    }

    public void removeCartItem(Cart cart, Long productId, int quantity) {
        Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            if (cartItem.getQuantity() <= quantity) {
                cart.getCartItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() - quantity);
                cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                cartItemRepository.save(cartItem);
            }
        }
    }

}
