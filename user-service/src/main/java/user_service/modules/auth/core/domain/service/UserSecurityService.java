package user_service.modules.auth.core.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.password.PasswordEncoder;

@Service
public class UserSecurityService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;

    @Autowired
    public UserSecurityService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isPhoneNumberRegistered(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public void validateUserUniqueness(String email, String phoneNumber) {
        if (isEmailRegistered(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        if (isPhoneNumberRegistered(phoneNumber)) {
            throw new IllegalArgumentException("Phone number is already registered.");
        }
    }

    public void validateNewUserData(String email, String phoneNumber, String plainPassword) {
        validateUserUniqueness(email, phoneNumber);
        validatePasswordStrength(plainPassword);
    }

    public User processNewUser(User user) {
        String hashedPassword = passwordEncoder.hashPassword(user.getHashedPassword());
        user.setHashedPassword(hashedPassword);

        return user;
    }

    public void validatePasswordStrength(String plainPassword) {
        if (plainPassword.length() < MIN_PASSWORD_LENGTH || plainPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be between " + MIN_PASSWORD_LENGTH + " and "
                    + MAX_PASSWORD_LENGTH + " characters long.");
        }
        if (!plainPassword.matches(passwordPattern)) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

    }

    public String generateActivationToken(User user) {
        return java.util.UUID.randomUUID().toString();
    }
}
