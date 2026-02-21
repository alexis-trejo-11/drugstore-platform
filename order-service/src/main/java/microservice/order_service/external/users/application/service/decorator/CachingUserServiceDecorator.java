package microservice.order_service.external.users.application.service.decorator;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.order_service.external.users.application.command.CreateUserCommand;
import microservice.order_service.external.users.application.command.UpdateUserCommand;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CachingUserServiceDecorator implements UserService {
  private static final String CACHE_BY_ID = "users:id";
  private static final String CACHE_BY_EMAIL = "users:email";
  private static final String CACHE_BY_PHONE = "users:phone";

  private final CacheManager cacheManager;
  private final LoggingUserServiceDecorator delegate;

  private Cache cacheById() {
    return cacheManager.getCache(CACHE_BY_ID);
  }

  private Cache cacheByEmail() {
    return cacheManager.getCache(CACHE_BY_EMAIL);
  }

  private Cache cacheByPhone() {
    return cacheManager.getCache(CACHE_BY_PHONE);
  }

  @Override
  public User getUserByID(UserID userID) {
    String key = userID.value();
    Cache cache = cacheById();
    if (cache != null) {
      Cache.ValueWrapper wrapper = cache.get(key);
      if (wrapper != null) {
        log.debug("Cache hit [id]={}", key);
        return (User) wrapper.get();
      }
    }

    log.debug("Cache miss [id]={}, delegating to service", key);
    User user = delegate.getUserByID(userID);
    putInCaches(user);
    return user;
  }

  @Override
  public User getUserByEmail(String email) {
    Cache cache = cacheByEmail();
    if (cache != null) {
      Cache.ValueWrapper wrapper = cache.get(email);
      if (wrapper != null) {
        log.debug("Cache hit [email]={}", email);
        return (User) wrapper.get();
      }
    }

    log.debug("Cache miss [email]={}, delegating to service", email);
    User user = delegate.getUserByEmail(email);
    putInCaches(user);
    return user;
  }

  @Override
  public User getUserByPhoneNumber(String phoneNumber) {
    Cache cache = cacheByPhone();
    if (cache != null) {
      Cache.ValueWrapper wrapper = cache.get(phoneNumber);
      if (wrapper != null) {
        log.debug("Cache hit [phone]={}", phoneNumber);
        return (User) wrapper.get();
      }
    }

    log.debug("Cache miss [phone]={}, delegating to service", phoneNumber);
    User user = delegate.getUserByPhoneNumber(phoneNumber);
    putInCaches(user);
    return user;
  }

  @Override
  public User createUser(CreateUserCommand command) {
    log.info("Creating user (caching decorator) email={} phone={}", command.email(), command.phoneNumber());
    User created = delegate.createUser(command);
    try {
      putInCaches(created);
    } catch (Exception ex) {
      log.warn("Failed to put created user into cache id={}: {}", created.getId().value(), ex.getMessage());
    }
    return created;
  }

  @Override
  public User updateUser(UpdateUserCommand command) {
    log.info("Updating user (caching decorator) id={}", command.id().value());

    // fetch existing to know previous email/phone for eviction if needed
    User before = null;
    try {
      before = delegate.getUserByID(command.id());
    } catch (Exception ex) {
      log.debug("Unable to fetch existing user before update id={} : {}", command.id().value(), ex.getMessage());
    }

    User updated = delegate.updateUser(command);

    // evict previous keys if changed
    try {
      if (before != null) {
        evictCachesFor(before);
      }
      putInCaches(updated);
    } catch (Exception ex) {
      log.warn("Failed to update caches for user id={}: {}", updated.getId().value(), ex.getMessage());
    }

    return updated;
  }

  @Override
  public void deleteUser(UserID userID) {
    log.info("Deleting user (caching decorator) id={}", userID.value());
    // try to fetch user to evict email/phone after deletion
    User before = null;
    try {
      before = delegate.getUserByID(userID);
    } catch (Exception ex) {
      log.debug("User not found before delete id={} : {}", userID.value(), ex.getMessage());
    }

    delegate.deleteUser(userID);

    try {
      if (before != null) {
        evictCachesFor(before);
      } else {
        // evict by id at least
        Cache cache = cacheById();
        if (cache != null)
          cache.evict(userID.value());
      }
    } catch (Exception ex) {
      log.warn("Failed to evict caches after delete id={}: {}", userID.value(), ex.getMessage());
    }
  }

  @Override
  public void restoreUser(UserID userID) {
    log.info("Restoring user (caching decorator) id={}", userID.value());
    delegate.restoreUser(userID);
    try {
      // After restore, fetch fresh user and put into caches
      User restored = delegate.getUserByID(userID);
      putInCaches(restored);
    } catch (Exception ex) {
      log.warn("Failed to refresh caches after restore id={}: {}", userID.value(), ex.getMessage());
    }
  }

  @Override
  public boolean existsByID(UserID userID) {
    // we don't cache this boolean separately; rely on cached user by id
    Cache cache = cacheById();
    if (cache != null) {
      Cache.ValueWrapper wrapper = cache.get(userID.value());
      if (wrapper != null) {
        log.debug("Cache-based exists check for id={} -> true", userID.value());
        return true;
      }
    }
    return delegate.existsByID(userID);
  }

  // validateUserCredentials is not part of the interface, so do not annotate with
  // @Override
  public void validateUserCredentials(String email, String phoneNumber) {
    // credentials validation should call underlying service (no caching)
    delegate.validateUserCredentials(email, phoneNumber);
  }

  private void putInCaches(User user) {
    if (user == null)
      return;
    try {
      Cache byId = cacheById();
      if (byId != null)
        byId.put(user.getId().value(), user);

      if (user.getEmail() != null) {
        Cache byEmail = cacheByEmail();
        if (byEmail != null)
          byEmail.put(user.getEmail(), user);
      }

      if (user.getPhoneNumber() != null) {
        Cache byPhone = cacheByPhone();
        if (byPhone != null)
          byPhone.put(user.getPhoneNumber(), user);
      }

      log.debug("Put user into caches id={} email={} phone={}", user.getId().value(), user.getEmail(),
          user.getPhoneNumber());
    } catch (Exception ex) {
      log.warn("Error while putting user into caches id={}: {}", user.getId().value(), ex.getMessage());
    }
  }

  private void evictCachesFor(User user) {
    if (user == null)
      return;
    try {
      Cache byId = cacheById();
      if (byId != null)
        byId.evict(user.getId().value());

      if (user.getEmail() != null) {
        Cache byEmail = cacheByEmail();
        if (byEmail != null)
          byEmail.evict(user.getEmail());
      }

      if (user.getPhoneNumber() != null) {
        Cache byPhone = cacheByPhone();
        if (byPhone != null)
          byPhone.evict(user.getPhoneNumber());
      }

      log.debug("Evicted user caches id={} email={} phone={}", user.getId().value(), user.getEmail(),
          user.getPhoneNumber());
    } catch (Exception ex) {
      log.warn("Error while evicting caches for user id={}: {}", user.getId().value(), ex.getMessage());
    }
  }
}
