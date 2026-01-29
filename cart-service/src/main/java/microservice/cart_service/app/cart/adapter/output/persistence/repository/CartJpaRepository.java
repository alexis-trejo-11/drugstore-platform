package microservice.cart_service.app.cart.adapter.output.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import microservice.cart_service.app.cart.adapter.output.persistence.models.CartModel;

public interface CartJpaRepository extends JpaRepository<CartModel, String>, JpaSpecificationExecutor<CartModel> {
	boolean existsById(String cartId);

	boolean existsByCustomerId(String customerId);

	Optional<CartModel> findByCustomerId(String customerId);

	@Query("SELECT c FROM CartModel c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.product WHERE c.id = :cartId")
	Optional<CartModel> findByIdWithCartItems(@Param("cartId") String cartId);

	@Query("SELECT c FROM CartModel c LEFT JOIN FETCH c.afterwardItems ai LEFT JOIN FETCH ai.product WHERE c.id = :cartId")
	Optional<CartModel> findByIdWithAfterwards(@Param("cartId") String cartId);

	@Query("SELECT DISTINCT c FROM CartModel c " +
			"LEFT JOIN FETCH c.cartItems ci " +
			"LEFT JOIN FETCH ci.product " +
			"LEFT JOIN FETCH c.afterwardItems ai " +
			"LEFT JOIN FETCH ai.product " +
			"WHERE c.id = :cartId")
	Optional<CartModel> findByIdWithCartItemsAndAfterwards(@Param("cartId") String cartId);

	// Fetch cart by customerId with cartItems
	@Query("SELECT c FROM CartModel c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.product WHERE c.customerId = :customerId")
	Optional<CartModel> findByCustomerIdWithCartItems(@Param("customerId") String customerId);

	// Fetch cart by customerId with afterwards
	@Query("SELECT c FROM CartModel c LEFT JOIN FETCH c.afterwardItems ai LEFT JOIN FETCH ai.product WHERE c.customerId = :customerId")
	Optional<CartModel> findByCustomerIdWithAfterwards(@Param("customerId") String customerId);

	// Fetch cart by customerId with both cartItems and afterwards
	@Query("SELECT DISTINCT c FROM CartModel c " +
			"LEFT JOIN FETCH c.cartItems ci " +
			"LEFT JOIN FETCH ci.product " +
			"LEFT JOIN FETCH c.afterwardItems ai " +
			"LEFT JOIN FETCH ai.product " +
			"WHERE c.customerId = :customerId")
	Optional<CartModel> findByCustomerIdWithCartItemsAndAfterwards(@Param("customerId") String customerId);
}
