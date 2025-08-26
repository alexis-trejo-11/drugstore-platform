package user_service.modules.auth.core.application.usecases.password;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.helping_services.TokenService;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.service.PasswordValidator;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.password.PasswordEncoder;
import user_service.utils.tokens.interfaces.TokenType;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public void execute(UUID userId, String token, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundError(userId));

        tokenService.validateToken(token, TokenType.ACTIVATION, user.getId());

        PasswordValidator.validatePasswordStrength(newPassword);

        String newHashPassword = passwordEncoder.hashPassword(newPassword);
        user.setHashedPassword(newHashPassword);

        userRepository.save(user);
    }
}
