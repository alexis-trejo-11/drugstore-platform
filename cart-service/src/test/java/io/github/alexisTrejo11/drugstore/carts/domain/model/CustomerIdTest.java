package io.github.alexisTrejo11.drugstore.carts.domain.model;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValueObjectException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;

public class CustomerIdTest {

  @Test
  void fromStringSucceeds() {
    // Given
    String customerIdValue = "customer-123";

    // When
    CustomerId customerId = CustomerId.from(customerIdValue);

    // Then
    assertThat(customerId.value()).isEqualTo(customerIdValue);
  }

  @Test
  void fromUuidSucceeds() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When
    CustomerId customerId = CustomerId.from(uuid);

    // Then
    assertThat(customerId.value()).isEqualTo(uuid.toString());
  }

  @Test
  void fromNullStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> CustomerId.from((String) null));
  }

  @Test
  void fromBlankStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> CustomerId.from(" "));
    assertThrows(CartValueObjectException.class, () -> CustomerId.from(""));
  }

  @Test
  void constructorWithNullValueThrowsException() {
    assertThrows(CartValueObjectException.class, () -> new CustomerId(null));
  }

  @Test
  void equalsAndHashCodeWork() {
    // Given
    String customerIdValue = "customer-456";
    CustomerId customerId1 = CustomerId.from(customerIdValue);
    CustomerId customerId2 = CustomerId.from(customerIdValue);
    CustomerId customerId3 = CustomerId.from("customer-789");

    // Then
    assertThat(customerId1).isEqualTo(customerId2);
    assertThat(customerId1.hashCode()).isEqualTo(customerId2.hashCode());
    assertThat(customerId1).isNotEqualTo(customerId3);
  }

  @Test
  void acceptsAlphanumericValues() {
    // When & Then
    assertThat(CustomerId.from("customer123").value()).isEqualTo("customer123");
    assertThat(CustomerId.from("123").value()).isEqualTo("123");
    assertThat(CustomerId.from("customer-123-abc").value()).isEqualTo("customer-123-abc");
  }
}
