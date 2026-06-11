package io.github.alexisTrejo11.drugstore.order.external.user.repository;

import org.springframework.stereotype.Repository;
import io.github.alexisTrejo11.drugstore.order.external.user.model.User;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

import java.util.Optional;

/**
 * Need to be implemented, I have issues with grpc proto and netwwork files. So
 * will be empty for now.
 * For testing mock the interface until the grpc issues are resolved.
 */
@Repository
public class UserGrpcClientService implements UserRepository {

  @Override
  public Optional<User> findById(UserID id) {
    return Optional.empty();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.empty();
  }

  @Override
  public Optional<User> findByPhoneNumber(String phoneNumber) {
    return Optional.empty();
  }
}
