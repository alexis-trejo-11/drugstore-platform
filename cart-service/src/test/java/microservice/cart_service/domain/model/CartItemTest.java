package microservice.cart_service.domain.model;

import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.exception.InvalidQuantityException;
import microservice.cart_service.app.cart.core.domain.model.CartItem;
import microservice.cart_service.app.cart.core.domain.model.CreateCartItemParams;
import microservice.cart_service.app.cart.core.domain.model.ReconstructCartItemParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartItemId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ItemPrice;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartItemTest {

  @Test
  void createCartItemWithValidParamsSucceeds() {
    // Given
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-123");
    Quantity quantity = Quantity.of(2);

    CreateCartItemParams params = new CreateCartItemParams(cartId, productId, quantity);

    // When
    CartItem item = CartItem.create(params);

    // Then
    assertThat(item).isNotNull();
    assertThat(item.getId()).isNotNull();
    assertThat(item.getCartId()).isEqualTo(cartId);
    assertThat(item.getProductId()).isEqualTo(productId);
    assertThat(item.getQuantity()).isEqualTo(quantity);
    assertThat(item.getQuantityValue()).isEqualTo(2);
    assertThat(item.getTimeStamps()).isNotNull();
    assertThat(item.getTimeStamps().getCreatedAt()).isNotNull();
    assertThat(item.isDeleted()).isFalse();
  }

  @Test
  void createCartItemWithNullParamsThrowsException() {
    assertThrows(CartValidationException.class, () -> CartItem.create(null));
  }

  @Test
  void createCartItemWithNullCartIdThrowsException() {
    CreateCartItemParams params = new CreateCartItemParams(null, ProductId.from("product-123"), Quantity.of(1));
    assertThrows(CartValidationException.class, () -> CartItem.create(params));
  }

  @Test
  void createCartItemWithNullProductIdThrowsException() {
    CreateCartItemParams params = new CreateCartItemParams(CartId.generate(), null, Quantity.of(1));
    assertThrows(CartValidationException.class, () -> CartItem.create(params));
  }

  @Test
  void createCartItemWithNullQuantityThrowsException() {
    CreateCartItemParams params = new CreateCartItemParams(CartId.generate(), ProductId.from("product-123"), null);
    assertThrows(CartValidationException.class, () -> CartItem.create(params));
  }
  
  @Test
  void reconstructCartItemSucceeds() {
    // Given
    CartItemId itemId = CartItemId.generate();
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-456");
    String productName = "Test Product";
    ItemPrice unitPrice = ItemPrice.from("19.99");
    Quantity quantity = Quantity.of(3);
    BigDecimal discount = new BigDecimal("1.50");
    CartTimeStamps timeStamps = CartTimeStamps.now();

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
    CartItem item = CartItem.reconstruct(params);

    // Then
    assertThat(item).isNotNull();
    assertThat(item.getId()).isEqualTo(itemId);
    assertThat(item.getCartId()).isEqualTo(cartId);
    assertThat(item.getProductId()).isEqualTo(productId);
    assertThat(item.getProductName()).isEqualTo(productName);
    assertThat(item.getUnitPrice()).isEqualTo(unitPrice);
    assertThat(item.getQuantity()).isEqualTo(quantity);
    assertThat(item.getDiscountPerUnit()).isEqualByComparingTo(discount);
    assertThat(item.getTimeStamps()).isEqualTo(timeStamps);
  }

  @Test
  void reconstructCartItemWithNullParamsThrowsException() {
    assertThrows(CartValidationException.class, () -> CartItem.reconstruct(null));
  }

  @Test
  void reconstructCartItemWithNullIdThrowsException() {
    ReconstructCartItemParams params = ReconstructCartItemParams.builder()
        .id(null)
        .cartId(CartId.generate())
        .productId(ProductId.from("product-123"))
        .quantity(Quantity.of(1))
        .build();
    assertThrows(CartValidationException.class, () -> CartItem.reconstruct(params));
  }

  @Test
  void updateQuantitySucceeds() {
    // Given
    CartItem item = createTestCartItem();
    Quantity newQuantity = Quantity.of(5);

    // When
    item.updateQuantity(newQuantity);

    // Then
    assertThat(item.getQuantity()).isEqualTo(newQuantity);
    assertThat(item.getQuantityValue()).isEqualTo(5);
  }

  @Test
  void updateQuantityWithIntegerValueSucceeds() {
    // Given
    CartItem item = createTestCartItem();

    // When
    item.updateQuantity(7);

    // Then
    assertThat(item.getQuantityValue()).isEqualTo(7);
  }

  @Test
  void updateQuantityWithNullThrowsException() {
    CartItem item = createTestCartItem();
    assertThrows(CartValidationException.class, () -> item.updateQuantity((Quantity) null));
  }

  @Test
  void increaseQuantitySucceeds() {
    // Given
    CartItem item = createTestCartItem(3);

    // When
    item.increaseQuantity(2);

    // Then
    assertThat(item.getQuantityValue()).isEqualTo(5);
  }

  @Test
  void increaseQuantityWithZeroOrNegativeThrowsException() {
    CartItem item = createTestCartItem();

    assertThrows(InvalidQuantityException.class, () -> item.increaseQuantity(0));
    assertThrows(InvalidQuantityException.class, () -> item.increaseQuantity(-1));
  }

  @Test
  void decreaseQuantitySucceeds() {
    // Given
    CartItem item = createTestCartItem(5);

    // When
    item.decreaseQuantity(2);

    // Then
    assertThat(item.getQuantityValue()).isEqualTo(3);
  }

  @Test
  void decreaseQuantityWithZeroOrNegativeThrowsException() {
    CartItem item = createTestCartItem();

    assertThrows(InvalidQuantityException.class, () -> item.decreaseQuantity(0));
    assertThrows(InvalidQuantityException.class, () -> item.decreaseQuantity(-1));
  }

  @Test
  void decreaseQuantityBelowMinimumThrowsException() {
    CartItem item = createTestCartItem(2);

    assertThrows(InvalidQuantityException.class, () -> item.decreaseQuantity(5));
  }

  @Test
  void updatePriceSucceeds() {
    // Given
    CartItem item = createTestCartItem();
    ItemPrice newPrice = ItemPrice.from("25.99");

    // When
    item.updatePrice(newPrice);

    // Then
    assertThat(item.getUnitPrice()).isEqualTo(newPrice);
  }

  @Test
  void updatePriceWithNullThrowsException() {
    CartItem item = createTestCartItem();
    assertThrows(CartValidationException.class, () -> item.updatePrice(null));
  }

  @Test
  void calculateSubtotalSucceeds() {
    // Given
    CartItem item = createTestCartItem(3);
    item.updatePrice(ItemPrice.from("10.50"));

    // When
    ItemPrice subtotal = item.calculateSubtotal();

    // Then - 3 * 10.50 = 31.50
    assertThat(subtotal.value()).isEqualByComparingTo(new BigDecimal("31.50"));
  }

  @Test
  void calculateSubtotalWithNullPriceReturnsNone() {
    // Given
    CartItem item = createTestCartItem();

    // When
    ItemPrice subtotal = item.calculateSubtotal();

    // Then
    assertThat(subtotal).isEqualTo(ItemPrice.NONE);
  }

  @Test
  void canMergeWithItemsOfSameProduct() {
    // Given
    CartItem item1 = createTestCartItem("product-123", 2);
    CartItem item2 = createTestCartItem("product-123", 3);
    CartItem item3 = createTestCartItem("product-456", 1);

    // Then
    assertThat(item1.canMergeWith(item2)).isTrue();
    assertThat(item1.canMergeWith(item3)).isFalse();
  }

  @Test
  void mergeWithItemSucceeds() {
    // Given
    CartItem item1 = createTestCartItem("product-123", 2);
    CartItem item2 = createTestCartItem("product-123", 3);

    // When
    item1.mergeWith(item2);

    // Then
    assertThat(item1.getQuantityValue()).isEqualTo(5);
  }

  @Test
  void mergeWithDifferentProductThrowsException() {
    // Given
    CartItem item1 = createTestCartItem("product-123", 2);
    CartItem item2 = createTestCartItem("product-456", 3);

    // Then
    assertThrows(CartValidationException.class, () -> item1.mergeWith(item2));
  }

  @Test
  void softDeleteAndCheckDeletion() {
    // Given
    CartItem item = createTestCartItem();
    assertThat(item.isDeleted()).isFalse();

    // When
    item.softDelete();

    // Then
    assertThat(item.isDeleted()).isTrue();
    assertThat(item.getTimeStamps().getDeletedAt()).isNotNull();
  }

  @Test
  void equalsAndHashCodeBasedOnProductId() {
    // Given
    CartItem item1 = createTestCartItem("product-123", 2);
    CartItem item2 = createTestCartItem("product-123", 5); // Different quantity
    CartItem item3 = createTestCartItem("product-456", 2);

    // Then - Items with same product ID should be equal regardless of quantity
    assertThat(item1).isEqualTo(item2);
    assertThat(item1.hashCode()).isEqualTo(item2.hashCode());

    // Items with different product ID should not be equal
    assertThat(item1).isNotEqualTo(item3);
  }

  @Test
  void toStringContainsItemInfo() {
    CartItem item = createTestCartItem("product-123", 2);
    item.updatePrice(ItemPrice.from("15.99"));

    String itemString = item.toString();

    assertThat(itemString).contains("CartItem");
    assertThat(itemString).contains("product-123");
    assertThat(itemString).contains("2");
    assertThat(itemString).contains("15.99");
  }

  // Helper methods
  private CartItem createTestCartItem() {
    return createTestCartItem("test-product-123", 1);
  }

  private CartItem createTestCartItem(int quantity) {
    return createTestCartItem("test-product-123", quantity);
  }

  private CartItem createTestCartItem(String productIdValue, int quantity) {
    CreateCartItemParams params = new CreateCartItemParams(
        CartId.generate(),
        ProductId.from(productIdValue),
        Quantity.of(quantity));
    return CartItem.create(params);
  }
}
