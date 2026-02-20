package io.github.alexisTrejo11.drugstore.carts.domain.model;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.ReconstructCartItemParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartItemId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartTimeStamps;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ItemPrice;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;

public class ReconstructCartItemParamsTest {

  @Test
  void createWithValidParametersSucceeds() {
    // Given
    CartItemId itemId = CartItemId.generate();
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-123");
    String productName = "Test Product";
    ItemPrice unitPrice = ItemPrice.from("19.99");
    Quantity quantity = Quantity.of(3);
    BigDecimal discountPerUnit = new BigDecimal("2.50");
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // When
    ReconstructCartItemParams params = new ReconstructCartItemParams(
        itemId, cartId, productId, productName, unitPrice, quantity, discountPerUnit, timeStamps);

    // Then
    assertThat(params).isNotNull();
    assertThat(params.id()).isEqualTo(itemId);
    assertThat(params.cartId()).isEqualTo(cartId);
    assertThat(params.productId()).isEqualTo(productId);
    assertThat(params.productName()).isEqualTo(productName);
    assertThat(params.unitPrice()).isEqualTo(unitPrice);
    assertThat(params.quantity()).isEqualTo(quantity);
    assertThat(params.discountPerUnit()).isEqualByComparingTo(discountPerUnit);
    assertThat(params.timeStamps()).isEqualTo(timeStamps);
  }

  @Test
  void builderPatternWorks() {
    // Given
    CartItemId itemId = CartItemId.generate();
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-456");
    String productName = "Builder Product";
    ItemPrice unitPrice = ItemPrice.from("25.00");
    Quantity quantity = Quantity.of(2);
    BigDecimal discountPerUnit = new BigDecimal("1.00");
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // When
    ReconstructCartItemParams params = ReconstructCartItemParams.builder()
        .id(itemId)
        .cartId(cartId)
        .productId(productId)
        .productName(productName)
        .unitPrice(unitPrice)
        .quantity(quantity)
        .discountPerUnit(discountPerUnit)
        .timeStamps(timeStamps)
        .build();

    // Then
    assertThat(params).isNotNull();
    assertThat(params.id()).isEqualTo(itemId);
    assertThat(params.cartId()).isEqualTo(cartId);
    assertThat(params.productId()).isEqualTo(productId);
    assertThat(params.productName()).isEqualTo(productName);
    assertThat(params.unitPrice()).isEqualTo(unitPrice);
    assertThat(params.quantity()).isEqualTo(quantity);
    assertThat(params.discountPerUnit()).isEqualByComparingTo(discountPerUnit);
    assertThat(params.timeStamps()).isEqualTo(timeStamps);
  }

  @Test
  void builderWithPartialParametersSucceeds() {
    // Given
    CartItemId itemId = CartItemId.generate();
    ProductId productId = ProductId.from("product-partial");
    Quantity quantity = Quantity.of(1);

    // When
    ReconstructCartItemParams params = ReconstructCartItemParams.builder()
        .id(itemId)
        .productId(productId)
        .quantity(quantity)
        .build();

    // Then
    assertThat(params).isNotNull();
    assertThat(params.id()).isEqualTo(itemId);
    assertThat(params.productId()).isEqualTo(productId);
    assertThat(params.quantity()).isEqualTo(quantity);
    assertThat(params.cartId()).isNull();
    assertThat(params.productName()).isNull();
    assertThat(params.unitPrice()).isNull();
    assertThat(params.discountPerUnit()).isNull();
    assertThat(params.timeStamps()).isNull();
  }

  @Test
  void recordEqualsAndHashCodeWork() {
    // Given
    CartItemId itemId = CartItemId.generate();
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-equals");
    String productName = "Equals Product";
    ItemPrice unitPrice = ItemPrice.from("15.50");
    Quantity quantity = Quantity.of(4);
    BigDecimal discountPerUnit = BigDecimal.ZERO;
    CartTimeStamps timeStamps = CartTimeStamps.now();

    ReconstructCartItemParams params1 = new ReconstructCartItemParams(
        itemId, cartId, productId, productName, unitPrice, quantity, discountPerUnit, timeStamps);
    ReconstructCartItemParams params2 = new ReconstructCartItemParams(
        itemId, cartId, productId, productName, unitPrice, quantity, discountPerUnit, timeStamps);
    ReconstructCartItemParams params3 = new ReconstructCartItemParams(
        CartItemId.generate(), cartId, productId, productName, unitPrice, quantity, discountPerUnit, timeStamps);

    // Then
    assertThat(params1).isEqualTo(params2);
    assertThat(params1.hashCode()).isEqualTo(params2.hashCode());
    assertThat(params1).isNotEqualTo(params3);
  }

  @Test
  void toStringContainsAllFields() {
    // Given
    CartItemId itemId = CartItemId.generate();
    CartId cartId = CartId.generate();
    ProductId productId = ProductId.from("product-toString");
    String productName = "ToString Product";
    ItemPrice unitPrice = ItemPrice.from("12.34");
    Quantity quantity = Quantity.of(5);
    BigDecimal discountPerUnit = new BigDecimal("0.50");
    CartTimeStamps timeStamps = CartTimeStamps.now();

    ReconstructCartItemParams params = new ReconstructCartItemParams(
        itemId, cartId, productId, productName, unitPrice, quantity, discountPerUnit, timeStamps);

    // When
    String paramsString = params.toString();

    // Then
    assertThat(paramsString).contains("ReconstructCartItemParams");
    assertThat(paramsString).contains(itemId.value());
    assertThat(paramsString).contains(cartId.value());
    assertThat(paramsString).contains("product-toString");
    assertThat(paramsString).contains("ToString Product");
    assertThat(paramsString).contains("12.34");
    assertThat(paramsString).contains("5");
    assertThat(paramsString).contains("0.50");
  }

  @Test
  void canCreateWithNullValues() {
    // When
    ReconstructCartItemParams params = new ReconstructCartItemParams(
        null, null, null, null, null, null, null, null);

    // Then
    assertThat(params.id()).isNull();
    assertThat(params.cartId()).isNull();
    assertThat(params.productId()).isNull();
    assertThat(params.productName()).isNull();
    assertThat(params.unitPrice()).isNull();
    assertThat(params.quantity()).isNull();
    assertThat(params.discountPerUnit()).isNull();
    assertThat(params.timeStamps()).isNull();
  }

  @Test
  void builderWithNullValuesSucceeds() {
    // When
    ReconstructCartItemParams params = ReconstructCartItemParams.builder()
        .id(null)
        .cartId(null)
        .productId(null)
        .productName(null)
        .unitPrice(null)
        .quantity(null)
        .discountPerUnit(null)
        .timeStamps(null)
        .build();

    // Then
    assertThat(params.id()).isNull();
    assertThat(params.cartId()).isNull();
    assertThat(params.productId()).isNull();
    assertThat(params.productName()).isNull();
    assertThat(params.unitPrice()).isNull();
    assertThat(params.quantity()).isNull();
    assertThat(params.discountPerUnit()).isNull();
    assertThat(params.timeStamps()).isNull();
  }
}
