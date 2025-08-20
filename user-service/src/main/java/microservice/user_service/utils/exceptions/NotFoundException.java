package microservice.user_service.utils.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends DrugstoreException {
    public NotFoundException(String resourceName, String identifier) {
        super(resourceName + " with ID " + identifier + " not found.",
                HttpStatus.NOT_FOUND, "PH-404");
    }
}