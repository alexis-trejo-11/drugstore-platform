package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.mapper;

import libs_kernel.page.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.output.CartResponse;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;

import java.math.BigDecimal;

@Component
public class CartResponseMapper {
  private final CartItemResponseMapper itemMapper;

  @Autowired
  public CartResponseMapper(CartItemResponseMapper itemMapper) {
    this.itemMapper = itemMapper;
  }

  public CartResponse fromDomain(Cart cart) {
    if (cart == null) {
      return null;
    }

    return CartResponse.builder()
        .cartId(cart.getId() != null ? cart.getId().value() : null)
        .customerId(cart.getCustomerId() != null ? cart.getCustomerId().value() : null)
        .subtotal(cart.calculateSubtotal() != null ? cart.calculateSubtotal().value() : BigDecimal.ZERO)
        .discount(cart.calculateDiscount())
        .total(cart.calculateTotal() != null ? cart.calculateTotal().value() : BigDecimal.ZERO)
        .updatedAt(cart.getTimeStamps() != null ? cart.getTimeStamps().getUpdatedAt() : null)
        .itemDetails(itemMapper.fromDomains(cart.getItems()))
        .afterwards(itemMapper.fromDomainsAfterwards(cart.getAfterwardsItems()))
        .build();
  }

  public PageResponse<CartResponse> fromDomainPage(Page<Cart> cartPage) {
    var cartResponses = cartPage.map(this::fromDomain);
    return PageResponse.from(cartResponses);
  }
}
