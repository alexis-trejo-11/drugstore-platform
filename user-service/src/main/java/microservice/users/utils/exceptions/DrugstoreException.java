package microservice.users.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DrugstoreException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;

    public DrugstoreException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

}
