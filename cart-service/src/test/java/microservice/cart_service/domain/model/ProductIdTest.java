package microservice.cart_service.domain.model;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import microservice.cart_service.app.cart.core.domain.exception.CartValueObjectException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

public class ProductIdTest {

  @Test
  void fromStringSucceeds() {
    // Given
    String productIdValue = "product-123";

    // When
    ProductId productId = ProductId.from(productIdValue);

    // Then
    assertThat(productId.value()).isEqualTo(productIdValue);
  }

  @Test
  void fromUuidSucceeds() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When
    ProductId productId = ProductId.from(uuid);

    // Then
    assertThat(productId.value()).isEqualTo(uuid.toString());
  }

  @Test
  void fromNullStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ProductId.from((String) null));
  }

  @Test
  void fromBlankStringThrowsException() {
    assertThrows(CartValueObjectException.class, () -> ProductId.from(" "));
    assertThrows(CartValueObjectException.class, () -> ProductId.from(""));
  }

  @Test
  void constructorWithNullValueThrowsException() {
    assertThrows(CartValueObjectException.class, () -> new ProductId(null));
  }

  @Test
  void equalsAndHashCodeWork() {
    // Given
    String productIdValue = "product-456";
    ProductId productId1 = ProductId.from(productIdValue);
    ProductId productId2 = ProductId.from(productIdValue);
    ProductId productId3 = ProductId.from("product-789");

    // Then
    assertThat(productId1).isEqualTo(productId2);
    assertThat(productId1.hashCode()).isEqualTo(productId2.hashCode());
    assertThat(productId1).isNotEqualTo(productId3);
  }

  @Test
  void acceptsVariousStringFormats() {
    // When & Then
    assertThat(ProductId.from("product123").value()).isEqualTo("product123");
    assertThat(ProductId.from("123456").value()).isEqualTo("123456");
    assertThat(ProductId.from("prod-123-abc").value()).isEqualTo("prod-123-abc");
    assertThat(ProductId.from(UUID.randomUUID().toString()).value()).isNotBlank();
  }
}
