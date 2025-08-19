package microservice.auth.app.shared.exceptions;

import microservice.users.utils.exceptions.DrugstoreException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends DrugstoreException {
    public NotFoundException(String resourceName, String identifier) {
        super(resourceName + " with ID " + identifier + " not found.",
                HttpStatus.NOT_FOUND, "PH-404");
    }
}