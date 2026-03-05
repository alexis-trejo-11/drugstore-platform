package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.grpc.mapper;

import io.alexisTrejo11.drugstore.grpc.cart.AfterwardItemDetail;
import io.alexisTrejo11.drugstore.grpc.cart.CartItemDetail;
import io.alexisTrejo11.drugstore.grpc.cart.CartResponse;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.AfterwardsItem;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CartItem;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartGrpcMapper {

  private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public CartResponse toGrpcResponse(Cart cart) {
    if (cart == null) {
      return CartResponse.getDefaultInstance();
    }

    CartResponse.Builder builder = CartResponse.newBuilder()
        .setCartId(cart.getId().value())
        .setCustomerId(cart.getCustomerId().value())
        .setSubtotal(cart.calculateSubtotal().value().toString())
        .setDiscount(cart.calculateDiscount().toString())
        .setTotal(cart.calculateTotal().value().toString())
        .setUpdatedAt(cart.getTimeStamps().updatedAt().format(ISO_FORMATTER));

    // Map cart items
    if (cart.getItems() != null && !cart.getItems().isEmpty()) {
      List<CartItemDetail> itemDetails = cart.getItems().stream()
          .map(this::toCartItemDetail)
          .collect(Collectors.toList());
      builder.addAllItems(itemDetails);
    }

    // Map afterwards items
    if (cart.getAfterwardsItems() != null && !cart.getAfterwardsItems().isEmpty()) {
      List<AfterwardItemDetail> afterwardsDetails = cart.getAfterwardsItems().stream()
          .map(this::toAfterwardItemDetail)
          .collect(Collectors.toList());
      builder.addAllAfterwards(afterwardsDetails);
    }

    return builder.build();
  }

  private CartItemDetail toCartItemDetail(CartItem item) {
    return CartItemDetail.newBuilder()
        .setProductId(item.getProductId().value())
        .setProductName(item.getProductName())
        .setQuantity(item.getQuantityValue())
        .setUnitPrice(item.getUnitPrice().value().toString())
        .setDiscountPerUnit(item.getDiscountPerUnit().toString())
        .setSubtotal(item.calculateSubtotal().value().toString())
        .setAddedAt(item.getTimeStamps().createdAt().format(ISO_FORMATTER))
        .setUpdatedAt(item.getTimeStamps().updatedAt().format(ISO_FORMATTER))
        .build();
  }

  private AfterwardItemDetail toAfterwardItemDetail(AfterwardsItem item) {
    return AfterwardItemDetail.newBuilder()
        .setProductId(item.getProductId().value())
        .setProductName(item.getProductName())
        .setQuantity(item.getQuantityValue())
        .setUnitPrice(item.getUnitPrice().value().toString())
        .setDiscountPerUnit(item.getDiscountPerUnit().toString())
        .setSubtotal(item.calculateSubtotal().value().toString())
        .setMovedAt(item.getTimeStamps().createdAt().format(ISO_FORMATTER))
        .setUpdatedAt(item.getTimeStamps().updatedAt().format(ISO_FORMATTER))
        .build();
  }
}
