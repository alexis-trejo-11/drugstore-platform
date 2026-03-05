package io.github.alexisTrejo11.drugstore.carts.domain.model;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CartItem;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CreateCartItemParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.ReconstructCartParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartTimeStamps;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;

public class ReconstructCartParamsTest {

  @Test
  void createWithValidParametersSucceeds() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-123");
    List<CartItem> items = createTestItems(cartId);
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // When
    ReconstructCartParams params = new ReconstructCartParams(cartId, customerId, items, null, timeStamps);

    // Then
    assertThat(params).isNotNull();
    assertThat(params.id()).isEqualTo(cartId);
    assertThat(params.customerId()).isEqualTo(customerId);
    assertThat(params.items()).isEqualTo(items);
    assertThat(params.timeStamps()).isEqualTo(timeStamps);
  }

  @Test
  void withEmptyItemsCreatesCorrectParams() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-456");
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // When
    ReconstructCartParams params = ReconstructCartParams.withEmptyItems(cartId, customerId, timeStamps);

    // Then
    assertThat(params).isNotNull();
    assertThat(params.id()).isEqualTo(cartId);
    assertThat(params.customerId()).isEqualTo(customerId);
    assertThat(params.items()).isNotNull();
    assertThat(params.items()).isEmpty();
    assertThat(params.timeStamps()).isEqualTo(timeStamps);
  }

  @Test
  void recordEqualsAndHashCodeWork() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-test");
    List<CartItem> items = createTestItems(cartId);
    CartTimeStamps timeStamps = CartTimeStamps.now();

    ReconstructCartParams params1 = new ReconstructCartParams(cartId, customerId, items, null, timeStamps);
    ReconstructCartParams params2 = new ReconstructCartParams(cartId, customerId, items, null, timeStamps);
    ReconstructCartParams params3 = new ReconstructCartParams(CartId.generate(), customerId, items, null, timeStamps);

    // Then
    assertThat(params1).isEqualTo(params2);
    assertThat(params1.hashCode()).isEqualTo(params2.hashCode());
    assertThat(params1).isNotEqualTo(params3);
  }

  @Test
  void toStringContainsAllFields() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-toString");
    List<CartItem> items = createTestItems(cartId);
    CartTimeStamps timeStamps = CartTimeStamps.now();
    ReconstructCartParams params = new ReconstructCartParams(cartId, customerId, items, null, timeStamps);

    // When
    String paramsString = params.toString();

    // Then
    assertThat(paramsString).contains("ReconstructCartParams");
    assertThat(paramsString).contains(cartId.value());
    assertThat(paramsString).contains("customer-toString");
  }

  @Test
  void canCreateWithNullItems() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-null-items");
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // When
    ReconstructCartParams params = new ReconstructCartParams(cartId, customerId, null, null, timeStamps);

    // Then
    assertThat(params.items()).isNull();
    assertThat(params.id()).isEqualTo(cartId);
    assertThat(params.customerId()).isEqualTo(customerId);
    assertThat(params.timeStamps()).isEqualTo(timeStamps);
  }

  @Test
  void canCreateWithNullTimeStamps() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-null-timestamps");
    List<CartItem> items = createTestItems(cartId);
    CartTimeStamps timeStamps = null;

    // When
    ReconstructCartParams params = new ReconstructCartParams(cartId, customerId, items, null, timeStamps);

    // Then
    assertThat(params.id()).isEqualTo(cartId);
    assertThat(params.customerId()).isEqualTo(customerId);
    assertThat(params.items()).isEqualTo(items);
    assertThat(params.timeStamps()).isNull();
  }

  @Test
  void withEmptyItemsStaticMethodProducesEmptyList() {
    // Given
    CartId cartId = CartId.generate();
    CustomerId customerId = CustomerId.from("customer-empty");
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // When
    ReconstructCartParams params = ReconstructCartParams.withEmptyItems(cartId, customerId, timeStamps);

    // Then
    assertThat(params.items()).isNotNull();
    assertThat(params.items()).hasSize(0);
    assertThat(params.items()).isInstanceOf(List.class);
  }

  // Helper method
  private List<CartItem> createTestItems(CartId cartId) {
    CartItem item1 = CartItem.create(new CreateCartItemParams(
        cartId,
        ProductId.from("product-1"),
        Quantity.of(2)));

    CartItem item2 = CartItem.create(new CreateCartItemParams(
        cartId,
        ProductId.from("product-2"),
        Quantity.of(1)));

    return Arrays.asList(item1, item2);
  }
}
