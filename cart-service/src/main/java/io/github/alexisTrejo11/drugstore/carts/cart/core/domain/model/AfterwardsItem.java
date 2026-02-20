package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model;

import java.time.LocalDateTime;

public class AfterwardsItem extends CartItem {

  public LocalDateTime movedAt;

  public static AfterwardsItem createFromItem(CartItem item) {
    AfterwardsItem afterwards = new AfterwardsItem();
    afterwards.id = item.getId();
    afterwards.cartId = item.getCartId();
    afterwards.productId = item.getProductId();
    afterwards.productName = item.getProductName();
    afterwards.unitPrice = item.getUnitPrice();
    afterwards.quantity = item.getQuantity();
    afterwards.discountPerUnit = item.getDiscountPerUnit();
    afterwards.timeStamps = item.getTimeStamps();
    afterwards.movedAt = LocalDateTime.now();
    return afterwards;
  }

  public static AfterwardsItem reconstruct(ReconstructCartItemParams params, LocalDateTime movedAt) {
    AfterwardsItem afterwards = new AfterwardsItem();
    afterwards.id = params.id();
    afterwards.cartId = params.cartId();
    afterwards.productId = params.productId();
    afterwards.productName = params.productName();
    afterwards.unitPrice = params.unitPrice();
    afterwards.quantity = params.quantity();
    afterwards.discountPerUnit = params.discountPerUnit();
    afterwards.timeStamps = params.timeStamps();
    afterwards.movedAt = movedAt;
    return afterwards;
  }

  public LocalDateTime getMovedAt() {
    return movedAt;
  }
}
