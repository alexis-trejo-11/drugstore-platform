package microservice.cart_service.domain.model;

import microservice.cart_service.app.cart.core.domain.exception.CartItemNotFoundException;
import microservice.cart_service.app.cart.core.domain.exception.CartOperationException;
import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.CartItem;
import microservice.cart_service.app.cart.core.domain.model.CreateCartParams;
import microservice.cart_service.app.cart.core.domain.model.CreateCartItemParams;
import microservice.cart_service.app.cart.core.domain.model.ReconstructCartParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ItemPrice;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartDomainTest {

  @Test
  void createCartWithValidParamsSucceeds() {
    // Given
    CustomerId customerId = CustomerId.from("customer-123");
    CreateCartParams params = new CreateCartParams(customerId);

    // When
    Cart cart = Cart.create(params);

    // Then
    assertThat(cart).isNotNull();
    assertThat(cart.getId()).isNotNull();
    assertThat(cart.getCustomerId()).isEqualTo(customerId);
    assertThat(cart.getItems()).isEmpty();
    assertThat(cart.getAfterwardsItems()).isEmpty();
    assertThat(cart.getTimeStamps()).isNotNull();
    assertThat(cart.getTimeStamps().getCreatedAt()).isNotNull();
    assertThat(cart.isEmpty()).isTrue();
    assertThat(cart.getTotalItemCount()).isEqualTo(0);
    assertThat(cart.getUniqueProductCount()).isEqualTo(0);
  }

  @Test
  void createCartWithNullParamsThrowsException() {
    assertThrows(CartValidationException.class, () -> Cart.create(null));
  }

  @Test
  void createCartWithNullCustomerIdThrowsException() {
    CreateCartParams params = new CreateCartParams(null);
    assertThrows(CartValidationException.class, () -> Cart.create(params));
  }

  @Test
  void reconstructCartSucceeds() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-456");
    CartTimeStamps timeStamps = CartTimeStamps.now();
    List<CartItem> items = new ArrayList<>();

    ReconstructCartParams params = new ReconstructCartParams(cartId, customerId, items, timeStamps);

    // When
    Cart cart = Cart.reconstruct(params);

    // Then
    assertThat(cart).isNotNull();
    assertThat(cart.getId()).isEqualTo(cartId);
    assertThat(cart.getCustomerId()).isEqualTo(customerId);
    assertThat(cart.getItems()).isEmpty();
    assertThat(cart.getTimeStamps()).isEqualTo(timeStamps);
  }

  @Test
  void addItemToCartSucceeds() {
    // Given
    Cart cart = createTestCart();
    CartItem item = createTestCartItem(cart.getId(), "product-1", 2);

    // When
    cart.addItem(item);

    // Then
    assertThat(cart.getItems()).hasSize(1);
    assertThat(cart.getTotalItemCount()).isEqualTo(2);
    assertThat(cart.getUniqueProductCount()).isEqualTo(1);
    assertThat(cart.containsProduct(ProductId.from("product-1"))).isTrue();
  }

  @Test
  void addItemWithSameProductMergesQuantities() {
    // Given
    Cart cart = createTestCart();
    CartItem item1 = createTestCartItem(cart.getId(), "product-1", 2);
    CartItem item2 = createTestCartItem(cart.getId(), "product-1", 3);

    // When
    cart.addItem(item1);
    cart.addItem(item2);

    // Then
    assertThat(cart.getItems()).hasSize(1);
    assertThat(cart.getTotalItemCount()).isEqualTo(5);
    assertThat(cart.getUniqueProductCount()).isEqualTo(1);
  }

  @Test
  void addNullItemThrowsException() {
    Cart cart = createTestCart();
    assertThrows(CartValidationException.class, () -> cart.addItem(null));
  }

  @Test
  void addMultipleItemsSucceeds() {
    // Given
    Cart cart = createTestCart();
    List<CartItem> items = Arrays.asList(
        createTestCartItem(cart.getId(), "product-1", 1),
        createTestCartItem(cart.getId(), "product-2", 2),
        createTestCartItem(cart.getId(), "product-3", 3));

    // When
    cart.addItems(items);

    // Then
    assertThat(cart.getItems()).hasSize(3);
    assertThat(cart.getTotalItemCount()).isEqualTo(6);
    assertThat(cart.getUniqueProductCount()).isEqualTo(3);
  }

  @Test
  void updateItemQuantitySucceeds() {
    // Given
    Cart cart = createTestCart();
    ProductId productId = ProductId.from("product-1");
    cart.addItem(createTestCartItem(cart.getId(), "product-1", 2));

    // When
    cart.updateItemQuantity(productId, Quantity.of(5));

    // Then
    CartItem item = cart.findItemByProductIdOrThrow(productId);
    assertThat(item.getQuantityValue()).isEqualTo(5);
    assertThat(cart.getTotalItemCount()).isEqualTo(5);
  }

  @Test
  void updateQuantityOfNonExistentItemThrowsException() {
    Cart cart = createTestCart();
    ProductId productId = ProductId.from("non-existent-product");

    assertThrows(CartItemNotFoundException.class,
        () -> cart.updateItemQuantity(productId, Quantity.of(5)));
  }

  @Test
  void removeItemSucceeds() {
    // Given
    Cart cart = createTestCart();
    ProductId productId = ProductId.from("product-1");
    cart.addItem(createTestCartItem(cart.getId(), "product-1", 2));

    // When
    boolean removed = cart.removeItem(productId);

    // Then
    assertThat(removed).isTrue();
    assertThat(cart.getItems()).isEmpty();
    assertThat(cart.getTotalItemCount()).isEqualTo(0);
    assertThat(cart.containsProduct(productId)).isFalse();
  }

  @Test
  void removeNonExistentItemReturnsFalse() {
    Cart cart = createTestCart();
    ProductId productId = ProductId.from("non-existent-product");

    boolean removed = cart.removeItem(productId);
    assertThat(removed).isFalse();
  }

  @Test
  void removeMultipleItemsSucceeds() {
    // Given
    Cart cart = createTestCart();
    cart.addItem(createTestCartItem(cart.getId(), "product-1", 1));
    cart.addItem(createTestCartItem(cart.getId(), "product-2", 2));
    cart.addItem(createTestCartItem(cart.getId(), "product-3", 3));

    List<ProductId> productsToRemove = Arrays.asList(
        ProductId.from("product-1"),
        ProductId.from("product-3"));

    // When
    int removedCount = cart.removeItems(productsToRemove);

    // Then
    assertThat(removedCount).isEqualTo(2);
    assertThat(cart.getItems()).hasSize(1);
    assertThat(cart.containsProduct(ProductId.from("product-2"))).isTrue();
  }

  @Test
  void clearCartRemovesAllItems() {
    // Given
    Cart cart = createTestCart();
    cart.addItem(createTestCartItem(cart.getId(), "product-1", 1));
    cart.addItem(createTestCartItem(cart.getId(), "product-2", 2));

    // When
    cart.clear();

    // Then
    assertThat(cart.getItems()).isEmpty();
    assertThat(cart.isEmpty()).isTrue();
  }

  @Test
  void calculateTotalPriceSucceeds() {
    // Given
    Cart cart = createTestCart();

    CartItem item1 = createTestCartItemWithPrice(cart.getId(), "product-1", 2, new BigDecimal("10.50"));
    CartItem item2 = createTestCartItemWithPrice(cart.getId(), "product-2", 3, new BigDecimal("5.00"));

    cart.addItem(item1);
    cart.addItem(item2);

    // When
    ItemPrice totalPrice = cart.calculateTotal();

    // Then - (2 * 10.50) + (3 * 5.00) = 21.00 + 15.00 = 36.00
    assertThat(totalPrice.value()).isEqualByComparingTo(new BigDecimal("36.00"));
  }

  @Test
  void softDeleteAndRestoreCart() {
    // Given
    Cart cart = createTestCart();
    assertThat(cart.isDeleted()).isFalse();
    assertThat(cart.getTimeStamps().getDeletedAt()).isNull();

    // When - soft delete
    cart.softDelete();

    // Then
    assertThat(cart.isDeleted()).isTrue();
    assertThat(cart.getTimeStamps().getDeletedAt()).isNotNull();

    // When - restore
    cart.restore();

    // Then
    assertThat(cart.isDeleted()).isFalse();
    assertThat(cart.getTimeStamps().getDeletedAt()).isNull();
  }

  @Test
  void moveItemsToAfterwardsSucceeds() {
    // Given
    Cart cart = createTestCart();
    cart.addItem(createTestCartItem(cart.getId(), "product-1", 1));
    cart.addItem(createTestCartItem(cart.getId(), "product-2", 2));
    cart.addItem(createTestCartItem(cart.getId(), "product-3", 3));

    List<ProductId> productsToMove = Arrays.asList(
        ProductId.from("product-1"),
        ProductId.from("product-3"));

    // When
    cart.moveItemsToAfterwards(productsToMove);

    // Then
    assertThat(cart.getItems()).hasSize(1);
    assertThat(cart.getAfterwardsItems()).hasSize(2);
    assertThat(cart.containsProduct(ProductId.from("product-2"))).isTrue();
    assertThat(cart.containsProduct(ProductId.from("product-1"))).isFalse();
  }

  @Test
  void returnItemsFromAfterwardsSucceeds() {
    // Given
    Cart cart = createTestCart();
    cart.addItem(createTestCartItem(cart.getId(), "product-1", 1));
    cart.addItem(createTestCartItem(cart.getId(), "product-2", 2));

    List<ProductId> productsToMove = Arrays.asList(ProductId.from("product-1"));
    cart.moveItemsToAfterwards(productsToMove);

    assertThat(cart.getItems()).hasSize(1);
    assertThat(cart.getAfterwardsItems()).hasSize(1);

    // When
    cart.returnItemsFromAfterwards(productsToMove);

    // Then
    assertThat(cart.getItems()).hasSize(2);
    assertThat(cart.getAfterwardsItems()).hasSize(0);
    assertThat(cart.containsProduct(ProductId.from("product-1"))).isTrue();
  }

  @Test
  void cartIsFullWhenAtMaxCapacity() {
    // Given
    Cart cart = createTestCart();

    // Add 100 items (MAX_ITEMS_PER_CART)
    for (int i = 1; i <= 100; i++) {
      cart.addItem(createTestCartItem(cart.getId(), "product-" + i, 1));
    }

    // Then
    assertThat(cart.isFull()).isTrue();
    assertThat(cart.getUniqueProductCount()).isEqualTo(100);
  }

  @Test
  void equalsAndHashCodeWork() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-123");

    Cart cart1 = Cart.reconstruct(ReconstructCartParams.withEmptyItems(cartId, customerId, CartTimeStamps.now()));
    Cart cart2 = Cart.reconstruct(ReconstructCartParams.withEmptyItems(cartId, customerId, CartTimeStamps.now()));

    // Then
    assertThat(cart1).isEqualTo(cart2);
    assertThat(cart1.hashCode()).isEqualTo(cart2.hashCode());
  }

  @Test
  void toStringContainsCartInfo() {
    Cart cart = createTestCart();
    String cartString = cart.toString();

    assertThat(cartString).contains("Cart");
    assertThat(cartString).contains(cart.getId().value());
  }

  // Helper methods
  private Cart createTestCart() {
    CustomerId customerId = CustomerId.from("test-customer-123");
    CreateCartParams params = new CreateCartParams(customerId);
    return Cart.create(params);
  }

  private CartItem createTestCartItem(CartId cartId, String productIdValue, int quantity) {
    CreateCartItemParams params = new CreateCartItemParams(
        cartId,
        ProductId.from(productIdValue),
        Quantity.of(quantity));
    return CartItem.create(params);
  }

  private CartItem createTestCartItemWithPrice(CartId cartId, String productIdValue, int quantity, BigDecimal price) {
    CartItem item = createTestCartItem(cartId, productIdValue, quantity);
    item.updatePrice(ItemPrice.create(price));
    return item;
  }
}
