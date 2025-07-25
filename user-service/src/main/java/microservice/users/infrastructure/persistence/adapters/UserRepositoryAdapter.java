package microservice.users.infrastructure.persistence.adapters;

import lombok.RequiredArgsConstructor;
import microservice.users.core.models.User;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.UserId;
import microservice.users.core.ports.output.UserRepository;
import microservice.users.infrastructure.persistence.mappers.UserMapper;
import microservice.users.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        var userModel = userMapper.toModel(user);
        var savedModel = userJpaRepository.save(userModel);
        return userMapper.toDomain(savedModel);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value())
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.value())
                .map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UserId id) {
        userJpaRepository.deleteById(id.value());
    }

    @Override
    public boolean existsById(UserId id) {
        return userJpaRepository.existsById(id.value());
    }
}
