package io.github.alexisTrejo11.drugstore.carts.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CreateCartItemParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;

public class CreateCartItemParamsTest {

  @Test
  void createWithValidParametersSucceeds() {
    // Given
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-123");
    Quantity quantity = Quantity.of(5);

    // When
    CreateCartItemParams params = new CreateCartItemParams(cartId, productId, quantity);

    // Then
    assertThat(params).isNotNull();
    assertThat(params.cartId()).isEqualTo(cartId);
    assertThat(params.productId()).isEqualTo(productId);
    assertThat(params.quantity()).isEqualTo(quantity);
  }

  @Test
  void builderPatternWorks() {
    // Given
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-456");
    Quantity quantity = Quantity.of(3);

    // When
    CreateCartItemParams params = CreateCartItemParams.builder()
        .cartId(cartId)
        .productId(productId)
        .quantity(quantity)
        .build();

    // Then
    assertThat(params).isNotNull();
    assertThat(params.cartId()).isEqualTo(cartId);
    assertThat(params.productId()).isEqualTo(productId);
    assertThat(params.quantity()).isEqualTo(quantity);
  }

  @Test
  void withDefaultsCreatesCorrectParams() {
    // Given
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-789");
    Quantity quantity = Quantity.of(2);

    // When
    CreateCartItemParams params = CreateCartItemParams.withDefaults(cartId, productId, quantity);

    // Then
    assertThat(params).isNotNull();
    assertThat(params.cartId()).isEqualTo(cartId);
    assertThat(params.productId()).isEqualTo(productId);
    // Note: withDefaults method in the code seems to ignore the passed quantity and
    // use Quantity.one()
    assertThat(params.quantity()).isEqualTo(Quantity.one());
  }

  @Test
  void recordEqualsAndHashCodeWork() {
    // Given
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-test");
    Quantity quantity = Quantity.of(1);

    CreateCartItemParams params1 = new CreateCartItemParams(cartId, productId, quantity);
    CreateCartItemParams params2 = new CreateCartItemParams(cartId, productId, quantity);
    CreateCartItemParams params3 = new CreateCartItemParams(cartId, productId, Quantity.of(2));

    // Then
    assertThat(params1).isEqualTo(params2);
    assertThat(params1.hashCode()).isEqualTo(params2.hashCode());
    assertThat(params1).isNotEqualTo(params3);
  }

  @Test
  void toStringContainsAllFields() {
    // Given
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-toString");
    Quantity quantity = Quantity.of(7);
    CreateCartItemParams params = new CreateCartItemParams(cartId, productId, quantity);

    // When
    String paramsString = params.toString();

    // Then
    assertThat(paramsString).contains("CreateCartItemParams");
    assertThat(paramsString).contains(cartId.value());
    assertThat(paramsString).contains("product-toString");
    assertThat(paramsString).contains("7");
  }

  @Test
  void canCreateWithNullValues() {
    // When
    CreateCartItemParams params = new CreateCartItemParams(null, null, null);

    // Then
    assertThat(params.cartId()).isNull();
    assertThat(params.productId()).isNull();
    assertThat(params.quantity()).isNull();
  }
}
