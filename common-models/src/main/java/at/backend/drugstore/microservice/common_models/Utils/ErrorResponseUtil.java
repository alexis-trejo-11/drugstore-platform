package at.backend.drugstore.microservice.common_models.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseUtil {

    public static <T> ResponseWrapper<T> createErrorResponse(HttpStatus status, String errorMessage) {
        ResponseWrapper<T> responseWrapper = new ResponseWrapper<>();
        responseWrapper.setMessage(errorMessage);
        responseWrapper.setStatus(status);
        return responseWrapper;
    }

    public static Map<String, String> getErrorMessages(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}

