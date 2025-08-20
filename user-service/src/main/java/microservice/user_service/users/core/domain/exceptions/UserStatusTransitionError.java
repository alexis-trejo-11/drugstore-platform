package microservice.user_service.users.core.domain.exceptions;

import microservice.user_service.utils.exceptions.BusinessRuleException;

public class UserStatusTransitionError extends BusinessRuleException {
    public UserStatusTransitionError(String message) {
        super(message, "USER_STATUS_TRANSITION_ERROR");
    }
}
