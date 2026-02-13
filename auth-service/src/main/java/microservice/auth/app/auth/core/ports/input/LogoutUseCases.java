package microservice.auth.app.auth.core.ports.input;

import libs_kernel.response.Result;
import microservice.auth.app.User;
import microservice.auth.app.auth.core.application.command.*;
import microservice.auth.app.auth.core.application.result.SessionResult;

public interface LogoutUseCases {
	 void logout(LogoutCommand command);
	 void logoutAll(LogoutAllCommand userId);
}
