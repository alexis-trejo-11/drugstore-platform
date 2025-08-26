package user_service.modules.auth.core.application.usecases.register;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import user_service.modules.auth.core.application.helping_services.TokenService;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.tokens.interfaces.TokenType;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateUserUseCase {
    private UserRepository userRepository;
    private TokenService tokenService;

    public void execute(UUID userId, String activationCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundError(userId));

        tokenService.validateToken(activationCode, TokenType.ACTIVATION, userId);
        user.activate();

        userRepository.save(user);

        log.info("User with id {} has been activated", userId);
    }
}
