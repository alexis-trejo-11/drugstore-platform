package microservice.users.core.domain.exceptions;

import microservice.users.utils.exceptions.BusinessRuleException;

public class UserStatusTransitionError extends BusinessRuleException {
    public UserStatusTransitionError(String message) {
        super(message, "USER_STATUS_TRANSITION_ERROR");
    }
}
