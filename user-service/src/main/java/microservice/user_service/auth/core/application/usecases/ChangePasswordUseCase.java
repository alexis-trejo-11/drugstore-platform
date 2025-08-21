package microservice.user_service.auth.core.application.usecases;

import lombok.RequiredArgsConstructor;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.domain.service.PasswordValidator;
import microservice.user_service.users.core.ports.output.UserRepository;
import microservice.user_service.utils.password.PasswordEncoder;
import microservice.user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChangePasswordUseCase {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public Result<Void> execute(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundError(userId));

        if (!passwordEncoder.verifyPassword(currentPassword, user.getHashedPassword())) {
            return Result.error("Current password is incorrect.", 400);
        }
        PasswordValidator.validatePasswordStrength(newPassword);
        String newHashPassword = passwordEncoder.hashPassword(newPassword);

        user.setHashedPassword(newHashPassword);
        userRepository.save(user);

        return Result.success();
    }
}
