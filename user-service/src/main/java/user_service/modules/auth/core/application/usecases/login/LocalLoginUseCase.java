package user_service.modules.auth.core.application.usecases.login;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.LoginDTO;
import user_service.modules.auth.core.application.dto.SessionResponse;
import user_service.modules.auth.core.application.helping_services.SessionService;
import user_service.modules.auth.core.domain.exception.User2FANotEnableError;
import user_service.modules.auth.core.domain.session.Session;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.password.PasswordEncoder;
import user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalLoginUseCase {
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public Result<SessionResponse> execute(LoginDTO loginDTO) {
        var userResult = authenticateUser(loginDTO);
        if (!userResult.isSuccess()) {
            return Result.error("User not found with given credentials", 404);
        }
        User user = userResult.getData();

        user.validateNotDisabled();
        if (user.isTwoFactorEnabled()) {
            throw new User2FANotEnableError();
        }

        userRepository.updateLastLoginAsync(user.getId());

        Session session = sessionService.createUserSession(user, loginDTO.metadata());
        return Result.success(SessionResponse.from(session), "Login successful");
    }

    private Result<User> authenticateUser(LoginDTO loginDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(loginDTO.identifierField());

        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByPhoneNumber(loginDTO.identifierField());
        }

        if (optionalUser.isEmpty()) {
            return Result.error("User not found with given credentials", 404);
        }

        User user = optionalUser.get();
        boolean isPasswordCorrect = passwordEncoder.verifyPassword(loginDTO.password(), user.getHashedPassword());

        if (!isPasswordCorrect) {
            return Result.error("Incorrect password", 401);
        }

        return Result.success(user);
    }
}
