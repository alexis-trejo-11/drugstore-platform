package microservice.order_service.external.users.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.domain.ports.output.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> findByID(String userID) {
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

    @Override
    public void delete(User user) {

    }

    @Override
    public void restore(User user) {

    }
}
