package at.backend.drugstore.microservice.common_models.Validations;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ControllerValidation {
    public static CustomControllerResponse handleValidationError(BindingResult bindingResult) {
        Map<String, Object> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return mapFieldNames(errors);
    }

    private static CustomControllerResponse mapFieldNames(Map<String, Object> errors) {
        Map<String, Object> mappedErrors = new HashMap<>();
        errors.forEach((key, value) -> {
            String fieldName = key.substring(key.lastIndexOf('.') + 1);
            mappedErrors.put(fieldName, value);
        });
        return new CustomControllerResponse("Validation Failed", mappedErrors);
    }


}
