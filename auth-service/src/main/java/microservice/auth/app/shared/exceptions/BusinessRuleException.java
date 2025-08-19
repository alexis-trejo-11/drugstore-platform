package microservice.auth.app.shared.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends DrugstoreException {
    public BusinessRuleException(String message, String errorCode) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
    }
}