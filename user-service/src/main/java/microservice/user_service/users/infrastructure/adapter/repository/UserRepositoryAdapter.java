package microservice.user_service.users.infrastructure.adapter.repository;

import lombok.RequiredArgsConstructor;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.users.core.domain.models.enums.UserStatus;
import microservice.user_service.users.core.ports.output.UserRepository;
import microservice.user_service.users.infrastructure.adapter.persistence.jpa.UserJpaRepository;
import microservice.user_service.users.infrastructure.adapter.persistence.mappers.ModelMapper;
import microservice.user_service.users.infrastructure.adapter.persistence.models.UserModel;
import microservice.user_service.utils.page.PageInput;
import microservice.user_service.utils.page.PageMapper;
import microservice.user_service.utils.page.PageResponse;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final ModelMapper<User, UserModel> userModelMapper;
    private final PageMapper pageMapper;

    private static final String USER_CACHE = "users";
    private static final String USER_EMAIL_CACHE = "usersByEmail";
    private static final String USER_PHONE_CACHE = "usersByPhone";

    @Override
    @Cacheable(value = USER_CACHE, key = "#id")
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(userModelMapper::toEntity);
    }

    @Override
    @Cacheable(value = USER_EMAIL_CACHE, key = "#email")
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userModelMapper::toEntity);
    }

    @Override
    @Cacheable(value = USER_PHONE_CACHE, key = "#phoneNumber")
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userJpaRepository.findByPhoneNumber(phoneNumber).map(userModelMapper::toEntity);
    }

    @Override
    public PageResponse<User> ListByRole(UserRole role, PageInput pageInput) {
        Pageable pageable = pageMapper.toPageable(pageInput);
        Page<UserModel> userModelPage = userJpaRepository.findByRole(role, pageable);
        return pageMapper.toPageResponse(userModelPage, userModelMapper::toEntity);
    }

    @Override
    public PageResponse<User> ListByStatus(UserStatus status, PageInput pageInput) {
        Pageable pageable = pageMapper.toPageable(pageInput);
        Page<UserModel> userModelPage = userJpaRepository.findByStatus(status, pageable);
        return pageMapper.toPageResponse(userModelPage, userModelMapper::toEntity);
    }

    @Override
    public PageResponse<User> search(String searchCriteriaJSON, PageInput pageInput) {
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    @Override
    @Cacheable(value = "userExists", key = "'email:' + #email")
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    @Cacheable(value = "userExists", key = "'phone:' + #phoneNumber")
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userJpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Cacheable(value = "userExists", key = "'id:' + #id")
    public boolean existsById(UUID id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = USER_CACHE, key = "#id"),
            @CacheEvict(value = USER_EMAIL_CACHE, allEntries = true),
            @CacheEvict(value = USER_PHONE_CACHE, allEntries = true)
    })
    public void updateLastLogin(UUID id) {
        userJpaRepository.updateLastLogin(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = USER_CACHE, key = "#user.id"),
            @CacheEvict(value = USER_EMAIL_CACHE, key = "#user.email"),
            @CacheEvict(value = USER_PHONE_CACHE, key = "#user.phoneNumber"),
            @CacheEvict(value = "userExists", allEntries = true)
    }, put = {
            @CachePut(value = USER_CACHE, key = "#result.id", condition = "#result != null"),
            @CachePut(value = USER_EMAIL_CACHE, key = "#result.email", condition = "#result != null"),
            @CachePut(value = USER_PHONE_CACHE, key = "#result.phoneNumber", condition = "#result != null")
    })
    public User save(User user) {
        System.out.println("Saving user: " + user.getId());
        UserModel userModel = userModelMapper.fromEntity(user);
        UserModel savedUserModel = userJpaRepository.save(userModel);
        return userModelMapper.toEntity(savedUserModel);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = USER_CACHE, key = "#id"),
            @CacheEvict(value = USER_EMAIL_CACHE, allEntries = true),
            @CacheEvict(value = USER_PHONE_CACHE, allEntries = true),
            @CacheEvict(value = "userExists", allEntries = true)
    })
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }

    @CacheEvict(value = { USER_CACHE, USER_EMAIL_CACHE, USER_PHONE_CACHE, "userExists" }, allEntries = true)
    public void clearAllCache() {
    }
}