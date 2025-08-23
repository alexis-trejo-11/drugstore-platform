package microservice.user_service.auth.core.application.usecases.login;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.SessionService;
import microservice.user_service.auth.core.application.dto.LoginDTO;
import microservice.user_service.auth.core.application.dto.SessionResponse;
import microservice.user_service.auth.core.domain.session.Session;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;
import microservice.user_service.utils.password.PasswordEncoder;
import microservice.user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
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
            return Result.error(
                    "Two-factor authentication is enabled for this user. Please complete the two-factor authentication process.",
                    403);
        }

        userRepository.updateLastLogin(user.getId());
        Session session = sessionService.createUserSession(user, loginDTO.metadata());
        SessionResponse sessionResponse = SessionResponse.from(session);
        return Result.success(sessionResponse, "Login successful");
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
