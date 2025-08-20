package microservice.user_service.users.core.domain.exceptions;

import microservice.user_service.utils.exceptions.BusinessRuleException;

public class WeakPlainPasswordError extends BusinessRuleException {
    public WeakPlainPasswordError(String message) {
        super(message, "WEAK_PLAIN_PASSWORD_ERROR");
    }
}
