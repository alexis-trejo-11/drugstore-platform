package io.github.alexisTrejo11.drugstore.carts.cart.core.port.output;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.specficication.CartSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CartRepository {
  Cart save(Cart cart);

  Page<Cart> search(CartSearchCriteria criteria, Pageable pageable);

  Optional<Cart> findById(CartId id, boolean requireItem, boolean requireAfterwards);

  Optional<Cart> findByCustomerIdWithItems(CustomerId customerId);

  void deleteById(CartId id);

  boolean existsById(CartId id);

  boolean existsByCustomerId(CustomerId customerId);
}
