package user_service.modules.auth.core.application.usecases.password;

import lombok.RequiredArgsConstructor;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.service.PasswordValidator;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.password.PasswordEncoder;
import user_service.utils.response.Result;
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
