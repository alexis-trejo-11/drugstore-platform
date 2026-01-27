package microservice.cart_service.domain.model;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import microservice.cart_service.app.cart.core.domain.exception.CartValueObjectException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;

public class CartIdTest {

  @Test
  void generateCartIdSucceeds() {
    // When
    CartId cartId = CartId.generate();

    // Then
    assertThat(cartId).isNotNull();
    assertThat(cartId.value()).isNotNull();
    assertThat(cartId.value()).isNotBlank();
  }

  @Test
  void fromStringSucceeds() {
    // Given
    String validUuid = UUID.randomUUID().toString();

    // When
    CartId cartId = CartId.from(validUuid);

    // Then
    assertThat(cartId.value()).isEqualTo(validUuid);
  }

  @Test
  void fromUuidSucceeds() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When
    CartId cartId = CartId.from(uuid);

    // Then
    assertThat(cartId.value()).isEqualTo(uuid.toString());
  }

  @Test
  void fromNullStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> CartId.from((String) null));
  }

  @Test
  void fromBlankStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> CartId.from(" "));
    assertThrows(CartValueObjectException.class, () -> CartId.from(""));
  }

  @Test
  void constructorWithNullValueThrowsException() {
    assertThrows(CartValueObjectException.class, () -> new CartId(null));
  }

  @Test
  void equalsAndHashCodeWork() {
    // Given
    String uuid = UUID.randomUUID().toString();
    CartId cartId1 = CartId.from(uuid);
    CartId cartId2 = CartId.from(uuid);
    CartId cartId3 = CartId.generate();

    // Then
    assertThat(cartId1).isEqualTo(cartId2);
    assertThat(cartId1.hashCode()).isEqualTo(cartId2.hashCode());
    assertThat(cartId1).isNotEqualTo(cartId3);
  }

  @Test
  void toStringReturnsValue() {
    // Given
    String uuid = UUID.randomUUID().toString();
    CartId cartId = CartId.from(uuid);

    // When
    String result = cartId.toString();

    // Then
    assertThat(result).contains(uuid);
  }
}
