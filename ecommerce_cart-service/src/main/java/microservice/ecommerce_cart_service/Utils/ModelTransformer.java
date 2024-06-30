package microservice.ecommerce_cart_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModelTransformer {

    public static CartDTO cartToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getClientId());
        cartDTO.setTotalPrice(cart.getTotalPrice());

        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for(var cartItem : cart.getCartItems()) {
          CartItemDTO cartItemDTO = cartItemToDTO(cartItem);
          cartItemDTOS.add(cartItemDTO);
        }

        cartDTO.setCartItems(cartItemDTOS);

        return cartDTO;
    }

    public static List<CartItemDTO> cartItemsToDTOs(List<CartItem> cartItems) {
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for (var carItem : cartItems) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setProductId(carItem.getProductId());
            cartItemDTO.setProductName(carItem.getProductName());
            cartItemDTO.setQuantity(carItem.getQuantity());
            cartItemDTO.setProductPrice(carItem.getProductPrice());
            cartItemDTO.setItemTotal(carItem.getItemTotal());

            cartItemDTOS.add(cartItemDTO);
        }

        return cartItemDTOS;
    }

    public static CartItemDTO cartItemToDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(cartItem.getProductId());
        cartItemDTO.setProductName(cartItem.getProductName());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setProductPrice(cartItem.getProductPrice());

        return cartItemDTO;
    }

    public static CartItem productDtoToCartItem(ProductDTO productDTO, int quantity, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(productDTO.getId());
        cartItem.setProductName(productDTO.getName());
        cartItem.setProductPrice(productDTO.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItem.setItemTotal(cartItem.getItemTotal());
        cartItem.setCart(cart);


        return cartItem;
    }

    public static OrderInsertDTO CartItemsToOrderInsertDTO(List<CartItemDTO> itemsToPurchase, ClientDTO clientDTO) {
        OrderInsertDTO orderInsertDTO = new OrderInsertDTO();
        orderInsertDTO.setClientDTO(clientDTO);

        List<OrderItemInsertDTO> orderItemInsertDTOS = MakeOrderItemInsertDTO(itemsToPurchase);
        orderInsertDTO.setItems(orderItemInsertDTOS);

        return orderInsertDTO;
    }

    private static List<OrderItemInsertDTO> MakeOrderItemInsertDTO(List<CartItemDTO> cartItemDTOS) {
        List<OrderItemInsertDTO> orderItemInsertDTOS = new ArrayList<>();
        for (CartItemDTO cartItem : cartItemDTOS) {
            OrderItemInsertDTO orderItemInsertDTO = new OrderItemInsertDTO();
            orderItemInsertDTO.setProductId(cartItem.getProductId());
            orderItemInsertDTO.setQuantity(cartItem.getQuantity());

            orderItemInsertDTOS.add(orderItemInsertDTO);
        }

        return orderItemInsertDTOS;
    }
}
