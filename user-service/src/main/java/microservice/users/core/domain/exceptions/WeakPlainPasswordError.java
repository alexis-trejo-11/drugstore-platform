package microservice.users.core.domain.exceptions;

import microservice.users.utils.exceptions.BusinessRuleException;

public class WeakPlainPasswordError extends BusinessRuleException {
    public WeakPlainPasswordError(String message) {
        super(message, "WEAK_PLAIN_PASSWORD_ERROR");
    }
}
