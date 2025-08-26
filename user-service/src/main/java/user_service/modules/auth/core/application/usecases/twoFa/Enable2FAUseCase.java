package user_service.modules.auth.core.application.usecases.twoFa;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.domain.exception.User2faAlreadyEnableError;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Enable2FAUseCase {
    private final UserRepository userRepository;

    public void execute(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundError(userId));

        if (user.isTwoFactorEnabled()) {
            throw new User2faAlreadyEnableError();
        }

        // TODO: ADD IMPL
        user.enableTwoFactor("");

        userRepository.save(user);
    }
}
