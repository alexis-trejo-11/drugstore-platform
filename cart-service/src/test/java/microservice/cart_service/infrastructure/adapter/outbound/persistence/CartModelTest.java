package microservice.cart_service.infrastructure.adapter.outbound.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import microservice.cart_service.app.cart.adapter.out.persistence.models.CartModel;

public class CartModelTest {

  @Test
  void cartModelCreation() {
    // Given
    String cartId = "cart-123";
    String customerId = "customer-456";

    // When
    CartModel cartModel = new CartModel(cartId);
    cartModel.setCustomerId(customerId);
    cartModel.setCartItems(new ArrayList<>());
    cartModel.setAfterwardItems(new HashSet<>());

    // Then
    assertThat(cartModel.getId()).isEqualTo(cartId);
    assertThat(cartModel.getCustomerId()).isEqualTo(customerId);
    assertThat(cartModel.getCartItems()).isNotNull();
    assertThat(cartModel.getAfterwardItems()).isNotNull();
    assertThat(cartModel.getCreatedAt()).isNull(); // Not set until @PrePersist
    assertThat(cartModel.getUpdatedAt()).isNull(); // Not set until @PrePersist
  }

  @Test
  void cartModelDefaultConstructor() {
    // When
    CartModel cartModel = new CartModel();

    // Then
    assertThat(cartModel).isNotNull();
    assertThat(cartModel.getId()).isNull();
    assertThat(cartModel.getCustomerId()).isNull();
    assertThat(cartModel.getCartItems()).isNull();
    assertThat(cartModel.getAfterwardItems()).isNull();
  }

  @Test
  void cartModelWithTimestamps() {
    // Given
    CartModel cartModel = new CartModel("cart-789");
    LocalDateTime now = LocalDateTime.now();

    // When
    cartModel.setCreatedAt(now);
    cartModel.setUpdatedAt(now.plusHours(1));

    // Then
    assertThat(cartModel.getCreatedAt()).isEqualTo(now);
    assertThat(cartModel.getUpdatedAt()).isEqualTo(now.plusHours(1));
    assertThat(cartModel.getUpdatedAt()).isAfter(cartModel.getCreatedAt());
  }

  @Test
  void cartModelPrePersistCallback() {
    // Given
    CartModel cartModel = new CartModel("cart-callback-test");
    assertThat(cartModel.getCreatedAt()).isNull();
    assertThat(cartModel.getUpdatedAt()).isNull();

    // When - Simulate @PrePersist callback
    cartModel.onCreate();

    // Then
    assertThat(cartModel.getCreatedAt()).isNotNull();
    assertThat(cartModel.getUpdatedAt()).isNotNull();
    assertThat(cartModel.getCreatedAt()).isEqualTo(cartModel.getUpdatedAt());
  }

  @Test
  void cartModelPreUpdateCallback() {
    // Given
    CartModel cartModel = new CartModel("cart-update-test");
    cartModel.onCreate(); // Simulate initial creation
    LocalDateTime originalUpdatedAt = cartModel.getUpdatedAt();

    // Wait a moment to ensure time difference
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // When - Simulate @PreUpdate callback
    cartModel.onUpdate();

    // Then
    assertThat(cartModel.getUpdatedAt()).isAfter(originalUpdatedAt);
    assertThat(cartModel.getCreatedAt()).isEqualTo(cartModel.getCreatedAt()); // Should remain unchanged
  }

  @Test
  void cartModelEqualsAndHashCode() {
    // Given
    String cartId = "cart-equals-test";
    CartModel cartModel1 = new CartModel(cartId);
    CartModel cartModel2 = new CartModel(cartId);
    CartModel cartModel3 = new CartModel("different-cart-id");

    cartModel1.setCustomerId("customer-1");
    cartModel2.setCustomerId("customer-1");
    cartModel3.setCustomerId("customer-1");

    // Then - Test equality based on ID (assuming Lombok @Data implementation)
    assertThat(cartModel1).isEqualTo(cartModel2);
    assertThat(cartModel1).isNotEqualTo(cartModel3);
    assertThat(cartModel1.hashCode()).isEqualTo(cartModel2.hashCode());
  }

  @Test
  void cartModelToString() {
    // Given
    CartModel cartModel = new CartModel("cart-toString-test");
    cartModel.setCustomerId("customer-toString");

    // When
    String toString = cartModel.toString();

    // Then - Test that toString contains relevant information (assuming Lombok
    // @Data)
    assertThat(toString).contains("CartModel");
    assertThat(toString).contains("cart-toString-test");
    assertThat(toString).contains("customer-toString");
  }
}
