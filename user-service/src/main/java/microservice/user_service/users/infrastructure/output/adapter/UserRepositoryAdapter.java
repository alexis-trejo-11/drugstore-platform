package microservice.user_service.users.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;
import microservice.user_service.users.infrastructure.output.persistence.mappers.UserMapper;
import microservice.user_service.users.infrastructure.output.persistence.repositories.UserJpaRepository;
import microservice.user_service.utils.page.Page;
import microservice.user_service.utils.page.PageInput;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Page<User> ListByRole(String role, PageInput pageInput) {
        return null;
    }

    @Override
    public Page<User> ListByStatus(String status, PageInput pageInput) {
        return null;
    }

    @Override
    public Page<User> search(String searchCriteriaJSON, PageInput pageInput) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return false;
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
