package user_service.modules.auth.core.domain.exception;

import user_service.utils.exceptions.ValidationException;

public class User2faAlreadyEnableError extends ValidationException {
    public User2faAlreadyEnableError() {
        super("User Already Have 2FA Auth", "USER_ALREADY_ENABLE_2FA");
    }
}
