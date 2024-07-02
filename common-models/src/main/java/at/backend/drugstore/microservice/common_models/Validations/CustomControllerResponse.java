package at.backend.drugstore.microservice.common_models.Validations;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
public class ControllerResponse {
    private String key;
    private Object value;

    public ControllerResponse(HttpStatus key, Object value) {
        this.key = key.getReasonPhrase();
        this.value = value;
    }
}

