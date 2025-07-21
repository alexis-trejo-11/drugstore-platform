package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.repositories;

import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.CartRepository;
import microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models.CartModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final CartJpaRepository cartJpaRepository;

    @Override
    public Cart save(Cart cart) {
        CartModel cartModel = CartModel.fromEntity(cart);
        CartModel savedModel = cartJpaRepository.save(cartModel);
        return savedModel.toEntity();
    }

    @Override
    public Optional<Cart> findById(CartId id) {
        return cartJpaRepository.findById(id.getValue())
                .map(CartModel::toEntity);
    }

    @Override
    public Optional<Cart> findByCustomerId(CustomerId customerId) {
        return cartJpaRepository.findByCustomerId(customerId.getValue())
                .map(CartModel::toEntity);
    }

    @Override
    public void deleteById(CartId id) {
        cartJpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(CartId id) {
        return cartJpaRepository.existsById(id.getValue());
    }

    @Override
    public boolean existsByClientId(CartId clientId) {
        return cartJpaRepository.existsByCartId(clientId.getValue());
    }
}