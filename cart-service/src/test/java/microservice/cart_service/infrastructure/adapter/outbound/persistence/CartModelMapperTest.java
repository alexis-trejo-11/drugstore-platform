package microservice.cart_service.infrastructure.adapter.outbound.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import microservice.cart_service.app.cart.adapter.output.persistence.mapper.CartItemModelMapper;
import microservice.cart_service.app.cart.adapter.output.persistence.mapper.CartModelMapper;
import microservice.cart_service.app.cart.adapter.output.persistence.models.CartModel;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.CreateCartParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

@ExtendWith(MockitoExtension.class)
public class CartModelMapperTest {

  @Mock
  private CartItemModelMapper itemMapper;

  private CartModelMapper cartModelMapper;

  @BeforeEach
  void setUp() {
    cartModelMapper = new CartModelMapper(itemMapper);
  }

  @Test
  void toModel_WithValidDomain_ReturnsCartModel() {
    // Given
    Cart domain = createTestDomainCart();
    when(itemMapper.toModels(anyList())).thenReturn(Collections.emptyList());

    // When
    CartModel result = cartModelMapper.toModel(domain);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(domain.getId().value());
    assertThat(result.getCustomerId()).isEqualTo(domain.getCustomerId().value());
    assertThat(result.getCreatedAt()).isEqualTo(domain.getTimeStamps().getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(domain.getTimeStamps().getUpdatedAt());
    assertThat(result.getCartItems()).isNotNull();
  }

  @Test
  void toModel_WithNullDomain_ReturnsNull() {
    // When
    CartModel result = cartModelMapper.toModel(null);

    // Then
    assertThat(result).isNull();
  }

  @Test
  void toDomain_WithValidModel_ReturnsCart() {
    // Given
    CartModel model = createTestCartModel();
    when(itemMapper.toDomains(anySet())).thenReturn(Collections.emptyList());

    // When
    Cart result = cartModelMapper.toDomain(model, false, false);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId().value()).isEqualTo(model.getId());
    assertThat(result.getCustomerId().value()).isEqualTo(model.getCustomerId());
    assertThat(result.getTimeStamps().getCreatedAt()).isEqualTo(model.getCreatedAt());
    assertThat(result.getTimeStamps().getUpdatedAt()).isEqualTo(model.getUpdatedAt());
    assertThat(result.getItems()).isEmpty();
  }

  @Test
  void toDomain_WithNullModel_ReturnsNull() {
    // When
    Cart result = cartModelMapper.toDomain(null, false, false);

    // Then
    assertThat(result).isNull();
  }

  @Test
  void toModels_WithValidDomainList_ReturnsCartModelList() {
    // Given
    List<Cart> domains = List.of(createTestDomainCart(), createTestDomainCart());
    when(itemMapper.toModels(anyList())).thenReturn(Collections.emptyList());

    // When
    List<CartModel> result = cartModelMapper.toModels(domains);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(1)).isNotNull();
  }

  @Test
  void toModels_WithNullList_ReturnsEmptyList() {
    // When
    List<CartModel> result = cartModelMapper.toModels(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void toDomains_WithValidModelList_ReturnsCartList() {
    // Given
    List<CartModel> models = List.of(createTestCartModel(), createTestCartModel());
    when(itemMapper.toDomains(anyList())).thenReturn(Collections.emptyList());

    // When
    List<Cart> result = cartModelMapper.toDomains(models, false, false);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(1)).isNotNull();
  }

  @Test
  void toDomains_WithNullList_ReturnsEmptyList() {
    // When
    List<Cart> result = cartModelMapper.toDomains(null, false, false);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void roundTripMapping_PreservesData() {
    // Given
    Cart originalDomain = createTestDomainCart();
    when(itemMapper.toModels(anyList())).thenReturn(Collections.emptyList());
    when(itemMapper.toDomains(anyList())).thenReturn(Collections.emptyList());

    // When
    CartModel model = cartModelMapper.toModel(originalDomain);
    Cart reconstructedDomain = cartModelMapper.toDomain(model, false, false);

    // Then
    assertThat(reconstructedDomain.getId().value()).isEqualTo(originalDomain.getId().value());
    assertThat(reconstructedDomain.getCustomerId().value()).isEqualTo(originalDomain.getCustomerId().value());
    assertThat(reconstructedDomain.getTimeStamps().getCreatedAt())
        .isEqualTo(originalDomain.getTimeStamps().getCreatedAt());
    assertThat(reconstructedDomain.getTimeStamps().getUpdatedAt())
        .isEqualTo(originalDomain.getTimeStamps().getUpdatedAt());
  }

  // Helper methods
  private Cart createTestDomainCart() {
    CustomerId customerId = CustomerId.from("customer-123");
    CreateCartParams params = new CreateCartParams(customerId);
    return Cart.create(params);
  }

  private CartModel createTestCartModel() {
    CartModel model = new CartModel();
    model.setId("cart-id-123");
    model.setCustomerId("customer-123");
    model.setCreatedAt(LocalDateTime.now().minusDays(1));
    model.setUpdatedAt(LocalDateTime.now());
    model.setCartItems(new ArrayList<>());
    model.setAfterwardItems(new HashSet<>());
    return model;
  }
}
