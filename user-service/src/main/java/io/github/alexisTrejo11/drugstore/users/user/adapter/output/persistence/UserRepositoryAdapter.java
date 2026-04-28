package io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.jpa.UserJpaRepository;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.mappers.UserModelMapper;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.models.UserModel;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserStatus;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

@Repository
@Primary
public class UserRepositoryAdapter implements UserRepository {
	private final UserJpaRepository userJpaRepository;
	private final UserModelMapper userModelMapper;

	@Autowired
	public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserModelMapper userModelMapper) {
		this.userJpaRepository = userJpaRepository;
		this.userModelMapper = userModelMapper;
	}

	private static final String USER_CACHE = "users";
	private static final String USER_EMAIL_CACHE = "usersByEmail";
	private static final String USER_PHONE_CACHE = "usersByPhone";

	@Override
	@Cacheable(value = USER_CACHE, key = "#id.value()")
	public Optional<User> findById(UserId id) {
		return userJpaRepository.findById(id.value())
				.map(userModelMapper::toEntity);
	}

	@Override
	@Cacheable(value = USER_EMAIL_CACHE, key = "#email.value()")
	public Optional<User> findByEmail(Email email) {
		return userJpaRepository.findByEmail(email.value()).map(userModelMapper::toEntity);
	}

	@Override
	@Cacheable(value = USER_PHONE_CACHE, key = "#phoneNumber.value()")
	public Optional<User> findByPhoneNumber(PhoneNumber phoneNumber) {
		return userJpaRepository.findByPhoneNumber(phoneNumber.value())
				.map(userModelMapper::toEntity);
	}

	@Override
	public Page<User> ListByRole(UserRole role, Pageable pageable) {
		Page<UserModel> userModelPage = userJpaRepository.findByRole(role, pageable);
		return userModelMapper.toDomainPage(userModelPage);
	}

	@Override
	public Page<User> ListByStatus(UserStatus status, Pageable pageable) {
		Page<UserModel> userModelPage = userJpaRepository.findByStatus(status, pageable);
		return userModelMapper.toDomainPage(userModelPage);
	}

	@Override
	public Page<User> search(String searchCriteriaJSON, Pageable pageable) {
		throw new UnsupportedOperationException("Unimplemented method 'search'");
	}

	@Override
	@Cacheable(value = "userExists", key = "'email:' + #email.value()")
	public boolean existsByEmail(Email email) {
		return userJpaRepository.existsByEmail(email.value());
	}

	@Override
	@Cacheable(value = "userExists", key = "'phone:' + #phoneNumber.value()")
	public boolean existsByPhoneNumber(PhoneNumber phoneNumber) {
		return userJpaRepository.existsByPhoneNumber(phoneNumber.value());
	}

	@Override
	@Cacheable(value = "userExists", key = "'id:' + #id")
	public boolean existsById(UserId id) {
		return userJpaRepository.existsById(id.value());
	}

	@Async("taskExecutor")
	@Override
	@Caching(evict = {
			@CacheEvict(value = USER_CACHE, key = "#id"),
			@CacheEvict(value = USER_EMAIL_CACHE, allEntries = true),
			@CacheEvict(value = USER_PHONE_CACHE, allEntries = true)
	})
	public void updateLastLoginAsync(UserId id) {
		userJpaRepository.updateLastLogin(id.value());
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
	public void deleteById(UserId id) {
		userJpaRepository.deleteById(
				id.value());
	}

	@CacheEvict(value = {USER_CACHE, USER_EMAIL_CACHE, USER_PHONE_CACHE, "userExists"}, allEntries = true)
	public void clearAllCache() {
	}
}