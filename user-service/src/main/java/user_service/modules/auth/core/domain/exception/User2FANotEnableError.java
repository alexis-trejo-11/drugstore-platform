package user_service.modules.auth.core.domain.exception;

import user_service.utils.exceptions.ValidationException;

public class User2FANotEnableError extends ValidationException {
    public User2FANotEnableError() {
        super("User Don't Have 2FA Auth", "USER_2FA_NOT_ENABLE");
    }

}
