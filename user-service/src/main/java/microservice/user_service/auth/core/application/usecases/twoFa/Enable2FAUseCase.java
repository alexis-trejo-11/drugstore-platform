package microservice.user_service.auth.core.application.usecases.twoFa;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import microservice.user_service.users.core.ports.output.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Enable2FAUseCase {
    private final UserRepository userRepository;

    public void execute(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isTwoFactorEnabled()) {
            throw new RuntimeException("User Already Have 2FA Auth");
        }

        // TODO: ADD IMPL
        user.enableTwoFactor("");

        userRepository.save(user);
    }
}
