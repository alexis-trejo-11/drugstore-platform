package microservice.users.utils.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends DrugstoreException {
    public ConflictException(String message, String errorCode) {
        super(message, HttpStatus.CONFLICT, errorCode);
    }
}
