package io.github.alexisTrejo11.drugstore.carts.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.AfterwardsItem;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CartItem;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CreateCartItemParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.ReconstructCartItemParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartItemId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartTimeStamps;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ItemPrice;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;

public class AfterwardsItemTest {

  @Test
  void createFromCartItemSucceeds() {
    // Given
    CartItem originalItem = createTestCartItem();

    // When
    AfterwardsItem afterwardsItem = AfterwardsItem.createFromItem(originalItem);

    // Then
    assertThat(afterwardsItem).isNotNull();
    assertThat(afterwardsItem.getId()).isEqualTo(originalItem.getId());
    assertThat(afterwardsItem.getCartId()).isEqualTo(originalItem.getCartId());
    assertThat(afterwardsItem.getProductId()).isEqualTo(originalItem.getProductId());
    assertThat(afterwardsItem.getProductName()).isEqualTo(originalItem.getProductName());
    assertThat(afterwardsItem.getUnitPrice()).isEqualTo(originalItem.getUnitPrice());
    assertThat(afterwardsItem.getQuantity()).isEqualTo(originalItem.getQuantity());
    assertThat(afterwardsItem.getDiscountPerUnit()).isEqualTo(originalItem.getDiscountPerUnit());
    assertThat(afterwardsItem.getTimeStamps()).isEqualTo(originalItem.getTimeStamps());
    assertThat(afterwardsItem.getMovedAt()).isNotNull();
    assertThat(afterwardsItem.getMovedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
  }

  @Test
  void createFromCartItemPreservesAllProperties() {
    // Given
    CartItem originalItem = createTestCartItemWithFullDetails();

    // When
    AfterwardsItem afterwardsItem = AfterwardsItem.createFromItem(originalItem);

    // Then - Verify all properties are preserved
    assertThat(afterwardsItem.getId()).isEqualTo(originalItem.getId());
    assertThat(afterwardsItem.getCartId()).isEqualTo(originalItem.getCartId());
    assertThat(afterwardsItem.getProductId()).isEqualTo(originalItem.getProductId());
    assertThat(afterwardsItem.getProductName()).isEqualTo(originalItem.getProductName());
    assertThat(afterwardsItem.getUnitPrice()).isEqualTo(originalItem.getUnitPrice());
    assertThat(afterwardsItem.getQuantity()).isEqualTo(originalItem.getQuantity());
    assertThat(afterwardsItem.getDiscountPerUnit()).isEqualTo(originalItem.getDiscountPerUnit());
    assertThat(afterwardsItem.getTimeStamps()).isEqualTo(originalItem.getTimeStamps());
  }

  @Test
  void reconstructAfterwardsItemSucceeds() {
    // Given
    CartItemId itemId = CartItemId.generate();
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-789");
    String productName = "Afterwards Product";
    ItemPrice unitPrice = ItemPrice.from("29.99");
    Quantity quantity = Quantity.of(2);
    BigDecimal discount = new BigDecimal("2.50");
    CartTimeStamps timeStamps = CartTimeStamps.now();
    LocalDateTime movedAt = LocalDateTime.now().minusHours(2);

    ReconstructCartItemParams params = ReconstructCartItemParams.builder()
        .id(itemId)
        .cartId(cartId)
        .productId(productId)
        .productName(productName)
        .unitPrice(unitPrice)
        .quantity(quantity)
        .discountPerUnit(discount)
        .timeStamps(timeStamps)
        .build();

    // When
    AfterwardsItem afterwardsItem = AfterwardsItem.reconstruct(params, movedAt);

    // Then
    assertThat(afterwardsItem).isNotNull();
    assertThat(afterwardsItem.getId()).isEqualTo(itemId);
    assertThat(afterwardsItem.getCartId()).isEqualTo(cartId);
    assertThat(afterwardsItem.getProductId()).isEqualTo(productId);
    assertThat(afterwardsItem.getProductName()).isEqualTo(productName);
    assertThat(afterwardsItem.getUnitPrice()).isEqualTo(unitPrice);
    assertThat(afterwardsItem.getQuantity()).isEqualTo(quantity);
    assertThat(afterwardsItem.getDiscountPerUnit()).isEqualByComparingTo(discount);
    assertThat(afterwardsItem.getTimeStamps()).isEqualTo(timeStamps);
    assertThat(afterwardsItem.getMovedAt()).isEqualTo(movedAt);
  }

  @Test
  void afterwardsItemInheritsCartItemMethods() {
    // Given
    CartItem originalItem = createTestCartItemWithFullDetails();
    AfterwardsItem afterwardsItem = AfterwardsItem.createFromItem(originalItem);

    // When & Then - Test inherited functionality
    assertThat(afterwardsItem.getQuantityValue()).isEqualTo(3);

    // Test calculations still work
    ItemPrice subtotal = afterwardsItem.calculateSubtotal();
    assertThat(subtotal.value()).isEqualByComparingTo(new BigDecimal("59.97")); // 3 * 19.99

    // Test that it can be used in merging logic
    CartItem anotherItem = createTestCartItemWithSameProduct();
    assertThat(afterwardsItem.canMergeWith(anotherItem)).isTrue();
  }

  @Test
  void afterwardsItemCanBeUpdated() {
    // Given
    CartItem originalItem = createTestCartItem();
    AfterwardsItem afterwardsItem = AfterwardsItem.createFromItem(originalItem);

    // When - Update inherited properties
    afterwardsItem.updateQuantity(5);
    afterwardsItem.updatePrice(ItemPrice.from("25.00"));

    // Then - Verify updates work correctly
    assertThat(afterwardsItem.getQuantityValue()).isEqualTo(5);
    assertThat(afterwardsItem.getUnitPrice().value()).isEqualByComparingTo(new BigDecimal("25.00"));

    // MovedAt should remain unchanged
    assertThat(afterwardsItem.getMovedAt()).isNotNull();
  }

  @Test
  void multipleAfterwardsItemsFromSameCartItemHaveDifferentMovedTimes() throws InterruptedException {
    // Given
    CartItem originalItem = createTestCartItem();

    // When - Create two afterwards items with a small delay
    AfterwardsItem afterwardsItem1 = AfterwardsItem.createFromItem(originalItem);
    Thread.sleep(10); // Small delay to ensure different timestamps
    AfterwardsItem afterwardsItem2 = AfterwardsItem.createFromItem(originalItem);

    // Then - MovedAt times should be different
    assertThat(afterwardsItem1.getMovedAt()).isBefore(afterwardsItem2.getMovedAt());
  }

  @Test
  void afterwardsItemSoftDeleteWorks() {
    // Given
    CartItem originalItem = createTestCartItem();
    AfterwardsItem afterwardsItem = AfterwardsItem.createFromItem(originalItem);
    assertThat(afterwardsItem.isDeleted()).isFalse();

    // When
    afterwardsItem.softDelete();

    // Then
    assertThat(afterwardsItem.isDeleted()).isTrue();
    assertThat(afterwardsItem.getTimeStamps().getDeletedAt()).isNotNull();
  }

  @Test
  void afterwardsItemEqualsBasedOnProductId() {
    // Given
    CartItem originalItem = createTestCartItemWithSameProduct();
    AfterwardsItem afterwardsItem1 = AfterwardsItem.createFromItem(originalItem);

    CartItem anotherItem = createTestCartItemWithSameProduct();
    AfterwardsItem afterwardsItem2 = AfterwardsItem.createFromItem(anotherItem);

    // Then - Should be equal because they have the same product ID
    assertThat(afterwardsItem1).isEqualTo(afterwardsItem2);
    assertThat(afterwardsItem1.hashCode()).isEqualTo(afterwardsItem2.hashCode());
  }

  // Helper methods
  private CartItem createTestCartItem() {
    CreateCartItemParams params = new CreateCartItemParams(
        CartId.generate(),
        ProductId.from("test-product-123"),
        Quantity.of(1));
    return CartItem.create(params);
  }

  private CartItem createTestCartItemWithFullDetails() {
    CartItem item = createTestCartItem();
    item.updateQuantity(3);
    item.updatePrice(ItemPrice.from("19.99"));
    return item;
  }

  private CartItem createTestCartItemWithSameProduct() {
    CreateCartItemParams params = new CreateCartItemParams(
        CartId.generate(),
        ProductId.from("test-product-123"), // Same product ID as createTestCartItem
        Quantity.of(2));
    return CartItem.create(params);
  }
}
