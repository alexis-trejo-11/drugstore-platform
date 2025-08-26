package user_service.modules.auth.core.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.usecases.logout.LogoutAllUseCase;
import user_service.modules.auth.core.application.usecases.logout.LogoutUseCase;
import user_service.modules.auth.core.ports.input.LogoutUseCases;

@Service
@RequiredArgsConstructor
public class LogoutUseCasesImpl implements LogoutUseCases {
    private final LogoutUseCase logoutUseCase;
    private final LogoutAllUseCase logoutAllUseCase;

    @Override
    public void logout(String token, UUID userId) {
        logoutUseCase.execute(token, userId);
    }

    @Override
    public void logoutAllSessions(UUID userId) {
        logoutAllUseCase.execute(userId);
    }

}
