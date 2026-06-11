package io.github.alexisTrejo11.drugstore.order.external.user.repository;

import java.util.Optional;

import io.github.alexisTrejo11.drugstore.order.external.user.model.User;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

public interface UserRepository {
  Optional<User> findById(UserID id);

  Optional<User> findByEmail(String email);

  Optional<User> findByPhoneNumber(String phoneNumber);
}
