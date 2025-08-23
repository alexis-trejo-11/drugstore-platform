package microservice.user_service.utils.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message, String errorCode) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
    }
}