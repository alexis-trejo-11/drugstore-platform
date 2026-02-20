package io.github.alexisTrejo11.drugstore.carts.domain.model;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValueObjectException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ItemPrice;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemPriceTest {

  @Test
  void createWithValidPriceSucceeds() {
    // Given
    BigDecimal priceValue = new BigDecimal("19.99");

    // When
    ItemPrice price = ItemPrice.create(priceValue);

    // Then
    assertThat(price.value()).isEqualByComparingTo(priceValue);
  }

  @Test
  void fromStringSucceeds() {
    // When
    ItemPrice price = ItemPrice.from("25.50");

    // Then
    assertThat(price.value()).isEqualByComparingTo(new BigDecimal("25.50"));
  }

  @Test
  void fromDoubleSucceeds() {
    // When
    ItemPrice price = ItemPrice.from(15.99);

    // Then
    assertThat(price.value()).isEqualByComparingTo(new BigDecimal("15.99"));
  }

  @Test
  void zeroReturnsZeroPrice() {
    // When
    ItemPrice price = ItemPrice.zero();

    // Then
    assertThat(price.value()).isEqualByComparingTo(BigDecimal.ZERO);
  }



  @Test
  void createWithNullValueThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ItemPrice.create(null));
  }

  @Test
  void fromNullStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ItemPrice.from((String) null));
  }

  @Test
  void fromBlankStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ItemPrice.from(" "));
    assertThrows(CartValueObjectException.class, () -> ItemPrice.from(""));
  }

  @Test
  void fromInvalidStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ItemPrice.from("invalid"));
    assertThrows(CartValueObjectException.class, () -> ItemPrice.from("abc.def"));
  }

  @Test
  void negativeValueThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ItemPrice.create(new BigDecimal("-10.50")));
  }

  @Test
  void valueAboveMaximumThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ItemPrice.create(new BigDecimal("999999.999")));
  }

  @Test
  void tooManyDecimalPlacesThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ItemPrice.create(new BigDecimal("19.999")));
  }

  @Test
  void multiplyByQuantitySucceeds() {
    // Given
    ItemPrice price = ItemPrice.from("10.50");
    Quantity quantity = Quantity.of(3);

    // When
    ItemPrice total = price.multiply(quantity);

    // Then
    assertThat(total.value()).isEqualByComparingTo(new BigDecimal("31.50"));
  }

  @Test
  void multiplyByIntegerSucceeds() {
    // Given
    ItemPrice price = ItemPrice.from("7.25");

    // When
    ItemPrice total = price.multiply(4);

    // Then
    assertThat(total.value()).isEqualByComparingTo(new BigDecimal("29.00"));
  }

  @Test
  void addPricesSucceeds() {
    // Given
    ItemPrice price1 = ItemPrice.from("10.50");
    ItemPrice price2 = ItemPrice.from("5.25");

    // When
    ItemPrice sum = price1.add(price2);

    // Then
    assertThat(sum.value()).isEqualByComparingTo(new BigDecimal("15.75"));
  }

  @Test
  void subtractPricesSucceeds() {
    // Given
    ItemPrice price1 = ItemPrice.from("20.00");
    ItemPrice price2 = ItemPrice.from("7.50");

    // When
    ItemPrice difference = price1.subtract(price2);

    // Then
    assertThat(difference.value()).isEqualByComparingTo(new BigDecimal("12.50"));
  }

  @Test
  void isZeroReturnsCorrectResult() {
    // Given
    ItemPrice zeroPrice = ItemPrice.zero();
    ItemPrice nonZeroPrice = ItemPrice.from("10.50");

    // Then
    assertThat(zeroPrice.isZero()).isTrue();
    assertThat(nonZeroPrice.isZero()).isFalse();
  }

  @Test
  void isPositiveReturnsCorrectResult() {
    // Given
    ItemPrice zeroPrice = ItemPrice.zero();
    ItemPrice positivePrice = ItemPrice.from("10.50");

    // Then
    assertThat(zeroPrice.isPositive()).isFalse();
    assertThat(positivePrice.isPositive()).isTrue();
  }

  @Test
  void compareToWorksCorrectly() {
    // Given
    ItemPrice price1 = ItemPrice.from("10.50");
    ItemPrice price2 = ItemPrice.from("15.75");
    ItemPrice price3 = ItemPrice.from("10.50");

    // Then
    assertThrows(CartValueObjectException.class, () -> price1.subtract(price2));
    assertThat(price2.subtract(price1).isPositive()).isTrue();
    assertThat(price1.subtract(price3).isZero()).isTrue();
  }

  @Test
  void constantsAreCorrect() {
    assertThat(ItemPrice.MIN_PRICE).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(ItemPrice.MAX_PRICE).isEqualByComparingTo(new BigDecimal("999999.99"));
    assertThat(ItemPrice.SCALE).isEqualTo(2);
    assertThat(ItemPrice.NONE.value()).isNull();
  }

  @Test
  void equalsAndHashCodeWork() {
    // Given
    ItemPrice price1 = ItemPrice.from("19.99");
    ItemPrice price2 = ItemPrice.from("19.99");
    ItemPrice price3 = ItemPrice.from("25.50");

    // Then
    assertThat(price1).isEqualTo(price2);
    assertThat(price1.hashCode()).isEqualTo(price2.hashCode());
    assertThat(price1).isNotEqualTo(price3);
  }

  @Test
  void toStringContainsPriceValue() {
    ItemPrice price = ItemPrice.from("19.99");
    String priceString = price.toString();

    assertThat(priceString).contains("19.99");
  }
}
