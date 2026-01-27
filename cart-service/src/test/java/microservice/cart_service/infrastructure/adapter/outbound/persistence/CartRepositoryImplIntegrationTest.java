package microservice.cart_service.infrastructure.adapter.outbound.persistence;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.CreateCartParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.specficication.CartSearchCriteria;
import microservice.cart_service.app.cart.core.port.out.CartRepository;

@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.fail-fast=false",
    "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
public class CartRepositoryImplIntegrationTest {

  @Autowired
  private CartRepository cartRepository;

  @Test
  @Transactional
  void saveAndFindCartFlow() {
    // Given
    CustomerId customerId = CustomerId.from("customer-integration-test");
    CreateCartParams params = new CreateCartParams(customerId);
    Cart cart = Cart.create(params);

    // When - Save cart
    Cart savedCart = cartRepository.save(cart);

    // Then - Verify save
    assertThat(savedCart).isNotNull();
    assertThat(savedCart.getId()).isNotNull();
    assertThat(savedCart.getCustomerId()).isEqualTo(customerId);

    CartId cartId = savedCart.getId();

    // When - Find by ID without items
    Optional<Cart> foundCart = cartRepository.findById(cartId, false, false);

    // Then - Verify find
    assertThat(foundCart).isPresent();
    assertThat(foundCart.get().getId()).isEqualTo(cartId);
    assertThat(foundCart.get().getCustomerId()).isEqualTo(customerId);
    assertThat(foundCart.get().getItems()).isEmpty();
  }

  @Test
  @Transactional
  void findCartByIdWithItemsAndAfterwards() {
    // Given
    CustomerId customerId = CustomerId.from("customer-with-items");
    CreateCartParams params = new CreateCartParams(customerId);
    Cart cart = Cart.create(params);
    Cart savedCart = cartRepository.save(cart);
    CartId cartId = savedCart.getId();

    // When - Find with items
    Optional<Cart> cartWithItems = cartRepository.findById(cartId, true, false);

    // Then
    assertThat(cartWithItems).isPresent();
    assertThat(cartWithItems.get().getId()).isEqualTo(cartId);

    // When - Find with afterwards items
    Optional<Cart> cartWithAfterwards = cartRepository.findById(cartId, false, true);

    // Then
    assertThat(cartWithAfterwards).isPresent();
    assertThat(cartWithAfterwards.get().getId()).isEqualTo(cartId);

    // When - Find with both items and afterwards
    Optional<Cart> cartWithBoth = cartRepository.findById(cartId, true, true);

    // Then
    assertThat(cartWithBoth).isPresent();
    assertThat(cartWithBoth.get().getId()).isEqualTo(cartId);
  }

  @Test
  @Transactional
  void findByCustomerIdWithItems() {
    // Given
    CustomerId customerId = CustomerId.from("customer-find-test");
    CreateCartParams params = new CreateCartParams(customerId);
    Cart cart = Cart.create(params);
    cartRepository.save(cart);

    // When
    Optional<Cart> foundCart = cartRepository.findByCustomerIdWithItems(customerId);

    // Then
    assertThat(foundCart).isPresent();
    assertThat(foundCart.get().getCustomerId()).isEqualTo(customerId);
  }

  @Test
  @Transactional
  void existsByIdAndCustomerId() {
    // Given
    CustomerId customerId = CustomerId.from("customer-exists-test");
    CreateCartParams params = new CreateCartParams(customerId);
    Cart cart = Cart.create(params);
    Cart savedCart = cartRepository.save(cart);

    // When & Then
    assertThat(cartRepository.existsById(savedCart.getId())).isTrue();
    assertThat(cartRepository.existsByCustomerId(customerId)).isTrue();

    // Test non-existent
    CartId nonExistentId = CartId.generate();
    CustomerId nonExistentCustomerId = CustomerId.from("non-existent-customer");
    assertThat(cartRepository.existsById(nonExistentId)).isFalse();
    assertThat(cartRepository.existsByCustomerId(nonExistentCustomerId)).isFalse();
  }

  @Test
  @Transactional
  void deleteById() {
    // Given
    CustomerId customerId = CustomerId.from("customer-delete-test");
    CreateCartParams params = new CreateCartParams(customerId);
    Cart cart = Cart.create(params);
    Cart savedCart = cartRepository.save(cart);
    CartId cartId = savedCart.getId();

    // Verify cart exists
    assertThat(cartRepository.existsById(cartId)).isTrue();

    // When
    cartRepository.deleteById(cartId);

    // Then
    assertThat(cartRepository.existsById(cartId)).isFalse();
  }

  @Test
  @Transactional
  void searchCartsWithCriteria() {
    // Given
    CustomerId customerId1 = CustomerId.from("customer-search-1");
    CustomerId customerId2 = CustomerId.from("customer-search-2");

    Cart cart1 = Cart.create(new CreateCartParams(customerId1));
    Cart cart2 = Cart.create(new CreateCartParams(customerId2));

    cartRepository.save(cart1);
    cartRepository.save(cart2);

    // When - Search with criteria
    CartSearchCriteria criteria = CartSearchCriteria.builder()
        .customerId(customerId1.value())
        .build();

    Page<Cart> searchResults = cartRepository.search(criteria, PageRequest.of(0, 10));

    // Then
    assertThat(searchResults).isNotNull();
    assertThat(searchResults.getContent()).isNotEmpty();
  }

  @Test
  @Transactional
  void updateCartPreservesData() {
    // Given
    CustomerId customerId = CustomerId.from("customer-update-test");
    CreateCartParams params = new CreateCartParams(customerId);
    Cart cart = Cart.create(params);
    Cart savedCart = cartRepository.save(cart);
    CartId originalId = savedCart.getId();

    // When - Update cart (trigger update timestamp)
    savedCart.clear(); // This will update the timestamp
    Cart updatedCart = cartRepository.save(savedCart);

    // Then
    assertThat(updatedCart.getId()).isEqualTo(originalId);
    assertThat(updatedCart.getCustomerId()).isEqualTo(customerId);
    assertThat(updatedCart.getTimeStamps().getUpdatedAt())
        .isAfterOrEqualTo(savedCart.getTimeStamps().getCreatedAt());
  }

  @Test
  @Transactional
  void findNonExistentCartReturnsEmpty() {
    // Given
    CartId nonExistentId = CartId.generate();
    CustomerId nonExistentCustomerId = CustomerId.from("non-existent-customer");

    // When & Then
    Optional<Cart> foundById = cartRepository.findById(nonExistentId, false, false);
    Optional<Cart> foundByCustomerId = cartRepository.findByCustomerIdWithItems(nonExistentCustomerId);

    assertThat(foundById).isEmpty();
    assertThat(foundByCustomerId).isEmpty();
  }
}
