package user_service.modules.users.core.domain.exceptions;

import org.springframework.http.HttpStatus;

import user_service.utils.exceptions.DomainException;

public class UserDisableError extends DomainException {
    public UserDisableError(String message) {
        super(message, HttpStatus.BAD_REQUEST, "USER_DISABLED");
    }

}
