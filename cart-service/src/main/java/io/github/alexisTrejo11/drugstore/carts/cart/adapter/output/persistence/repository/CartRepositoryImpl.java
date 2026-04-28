package io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.mapper.CartModelMapper;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.models.CartModel;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.specification.CartSpecificationBuilder;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.specficication.CartSearchCriteria;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.CartRepository;

@Repository
public class CartRepositoryImpl implements CartRepository {
  private final CartJpaRepository cartJpaRepository;
  private final CartModelMapper cartModelMapper;
  private final CartSpecificationBuilder cartSpecificationBuilder;

  @Autowired
  public CartRepositoryImpl(
      CartJpaRepository cartJpaRepository,
      CartModelMapper cartModelMapper,
      CartSpecificationBuilder cartSpecificationBuilder) {
    this.cartJpaRepository = cartJpaRepository;
    this.cartModelMapper = cartModelMapper;
    this.cartSpecificationBuilder = cartSpecificationBuilder;
  }

  @Override
  public Cart save(Cart cart) {
    CartModel cartModel = cartModelMapper.toModel(cart);
    CartModel savedModel = cartJpaRepository.save(cartModel);
    return cartModelMapper.toDomain(savedModel, false, false);
  }

  @Override
  public Page<Cart> search(CartSearchCriteria criteria, Pageable pageable) {
    var specification = cartSpecificationBuilder.build(criteria);
    Page<CartModel> cartModelsPage = cartJpaRepository.findAll(specification, pageable);
    return cartModelsPage.map(cartModel -> cartModelMapper.toDomain(cartModel, criteria.includeCartItems(), criteria.includeAfterwardItems()));
  }

  @Override
  public Optional<Cart> findById(CartId id, boolean requireItem, boolean requireAfterwards) {
    if (requireItem && requireAfterwards) {
      return cartJpaRepository.findByIdWithCartItemsAndAfterwards(id.value())
          .map(model -> cartModelMapper.toDomain(model, true, true));
    } else if (requireItem) {
      return cartJpaRepository.findByIdWithCartItems(id.value())
          .map(model -> cartModelMapper.toDomain(model, true, false));
    } else if (requireAfterwards) {
      return cartJpaRepository.findByIdWithAfterwards(id.value())
          .map(model -> cartModelMapper.toDomain(model, false, true));
    } else {
      return cartJpaRepository.findById(id.value())
          .map(model -> cartModelMapper.toDomain(model, false, false));
    }
  }

  @Override
  public Optional<Cart> findByCustomerIdWithItems(CustomerId customerId) {
    return cartJpaRepository.findByCustomerIdWithCartItemsAndAfterwards(customerId.value())
        .map(model -> cartModelMapper.toDomain(model, true, true));
  }

  @Override
  public void deleteById(CartId id) {
    cartJpaRepository.deleteById(id.value());
  }

  @Override
  public boolean existsById(CartId id) {
    return cartJpaRepository.existsById(id.value());
  }

  @Override
  public boolean existsByCustomerId(CustomerId customerId) {
    return cartJpaRepository.existsByCustomerId(customerId.value());
  }
}
