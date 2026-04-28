package microservice.order_service.external.users.application.service.decorator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.order_service.external.users.application.command.CreateUserCommand;
import microservice.order_service.external.users.application.command.UpdateUserCommand;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.external.users.application.service.UserServiceImpl;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoggingUserServiceDecorator implements UserService {
    private final UserServiceImpl delegate;

    @Override
    public User getUserByID(UserID userID) {
        try {
            User user = delegate.getUserByID(userID);
            log.debug("Found user id={} email={}", userID.value(), user.getEmail());
            return user;
        } catch (Exception ex) {
            log.warn("Failed to get user by ID {}: {}", userID.value(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user = delegate.getUserByEmail(email);
            log.debug("Found user email={} id={}", email, user.getId().value());
            return user;
        } catch (Exception ex) {
            log.warn("Failed to get user by email {}: {}", email, ex.getMessage());
            throw ex;
        }
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        try {
            User user = delegate.getUserByPhoneNumber(phoneNumber);
            log.debug("Found user phone={} id={}", phoneNumber, user.getId().value());
            return user;
        } catch (Exception ex) {
            log.warn("Failed to get user by phone {}: {}", phoneNumber, ex.getMessage());
            throw ex;
        }
    }

    @Override
    public User createUser(CreateUserCommand command) {
        log.info("Create user: email={} phone={}", command.email(), command.phoneNumber());
        try {
            User created = delegate.createUser(command);
            log.info("User created: id={} email={}", created.getId().value(), created.getEmail());
            return created;
        } catch (Exception ex) {
            log.error("Failed to create user email={}: {}", command.email(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        log.info("Update user: id={}", command.id().value());
        try {
            User updated = delegate.updateUser(command);
            log.info("User updated: id={} email={}", updated.getId().value(), updated.getEmail());
            return updated;
        } catch (Exception ex) {
            log.error("Failed to update user id={}: {}", command.id().value(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteUser(UserID userID) {
        log.info("Delete user: id={}", userID.value());
        try {
            delegate.deleteUser(userID);
            log.info("User deleted: id={}", userID.value());
        } catch (Exception ex) {
            log.error("Failed to delete user id={}: {}", userID.value(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void restoreUser(UserID userID) {
        log.info("Restore user: id={}", userID.value());
        try {
            delegate.restoreUser(userID);
            log.info("User restored: id={}", userID.value());
        } catch (Exception ex) {
            log.error("Failed to restore user id={}: {}", userID.value(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public boolean existsByID(UserID userID) {
        log.debug("Check exists user id={}", userID.value());
        return delegate.existsByID(userID);
    }

    public void validateUserCredentials(String email, String phoneNumber) {
        log.debug("Validate credentials email={} phone={}", email, phoneNumber);
        delegate.validateUserCredentials(email, phoneNumber);
    }
}

