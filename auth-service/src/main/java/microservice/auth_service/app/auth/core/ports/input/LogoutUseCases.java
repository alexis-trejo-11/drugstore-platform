package microservice.auth_service.app.auth.core.ports.input;

import microservice.auth_service.app.auth.core.application.command.*;

public interface LogoutUseCases {
	 void logout(LogoutCommand command);
	 void logoutAll(LogoutAllCommand userId);
}
