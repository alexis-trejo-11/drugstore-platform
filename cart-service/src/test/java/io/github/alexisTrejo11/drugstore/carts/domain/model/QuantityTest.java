package io.github.alexisTrejo11.drugstore.carts.domain.model;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.InvalidQuantityException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuantityTest {

  @Test
  void ofValidValueSucceeds() {
    // When
    Quantity quantity = Quantity.of(5);

    // Then
    assertThat(quantity.value()).isEqualTo(5);
  }

  @Test
  void oneReturnsQuantityOfOne() {
    // When
    Quantity quantity = Quantity.one();

    // Then
    assertThat(quantity.value()).isEqualTo(1);
  }

  @Test
  void constructorWithValidValueSucceeds() {
    // When
    Quantity quantity = new Quantity(10);

    // Then
    assertThat(quantity.value()).isEqualTo(10);
  }

  @Test
  void constructorWithValueBelowMinimumThrowsException() {
    assertThrows(InvalidQuantityException.class, () -> new Quantity(0));
    assertThrows(InvalidQuantityException.class, () -> new Quantity(-1));
  }

  @Test
  void constructorWithValueAboveMaximumThrowsException() {
    assertThrows(InvalidQuantityException.class, () -> new Quantity(1000));
    assertThrows(InvalidQuantityException.class, () -> new Quantity(9999));
  }

  @Test
  void increaseQuantitySucceeds() {
    // Given
    Quantity quantity = Quantity.of(5);

    // When
    Quantity increased = quantity.increase(3);

    // Then
    assertThat(increased.value()).isEqualTo(8);
    assertThat(quantity.value()).isEqualTo(5); // Original should be unchanged
  }

  @Test
  void increaseQuantityAboveMaximumThrowsException() {
    Quantity quantity = Quantity.of(995);
    assertThrows(InvalidQuantityException.class, () -> quantity.increase(10));
  }

  @Test
  void decreaseQuantitySucceeds() {
    // Given
    Quantity quantity = Quantity.of(10);

    // When
    Quantity decreased = quantity.decrease(3);

    // Then
    assertThat(decreased.value()).isEqualTo(7);
    assertThat(quantity.value()).isEqualTo(10); // Original should be unchanged
  }

  @Test
  void decreaseQuantityBelowMinimumThrowsException() {
    Quantity quantity = Quantity.of(3);
    assertThrows(InvalidQuantityException.class, () -> quantity.decrease(5));
  }

  @Test
  void canIncreaseReturnsCorrectResult() {
    // Given
    Quantity quantity = Quantity.of(995);

    // Then
    assertThat(quantity.canIncrease(4)).isTrue();
    assertThat(quantity.canIncrease(5)).isFalse();
  }

  @Test
  void canDecreaseReturnsCorrectResult() {
    // Given
    Quantity quantity = Quantity.of(5);

    // Then
    assertThat(quantity.canDecrease(4)).isTrue();
    assertThat(quantity.canDecrease(5)).isFalse();
  }

  @Test
  void isAtMinimumReturnsCorrectResult() {
    // Given
    Quantity minQuantity = Quantity.of(1);
    Quantity regularQuantity = Quantity.of(5);

    // Then
    assertThat(minQuantity.isAtMinimum()).isTrue();
    assertThat(regularQuantity.isAtMinimum()).isFalse();
  }

  @Test
  void isAtMaximumReturnsCorrectResult() {
    // Given
    Quantity maxQuantity = Quantity.of(999);
    Quantity regularQuantity = Quantity.of(5);

    // Then
    assertThat(maxQuantity.isAtMaximum()).isTrue();
    assertThat(regularQuantity.isAtMaximum()).isFalse();
  }

  @Test
  void minAndMaxConstantsAreCorrect() {
    assertThat(Quantity.MIN_QUANTITY).isEqualTo(1);
    assertThat(Quantity.MAX_QUANTITY).isEqualTo(999);
  }

  @Test
  void equalsAndHashCodeWork() {
    // Given
    Quantity quantity1 = Quantity.of(5);
    Quantity quantity2 = Quantity.of(5);
    Quantity quantity3 = Quantity.of(10);

    // Then
    assertThat(quantity1).isEqualTo(quantity2);
    assertThat(quantity1.hashCode()).isEqualTo(quantity2.hashCode());
    assertThat(quantity1).isNotEqualTo(quantity3);
  }
}
