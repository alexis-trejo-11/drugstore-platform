package at.backend.drugstore.microservice.common_models.Validations;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@Data
public class CustomControllerResponse {
    private Map<String, Object> map;

    public CustomControllerResponse() {
        this.map = new HashMap<>();
    }

    public void add(String key, Object value) {
        map.put(key, value);
    }

    public CustomControllerResponse(String key, Object value) {
        this.map = new HashMap<>();
        this.map.put(key, value);
    }

    public CustomControllerResponse(HttpStatus key, Object value) {
        this.map = new HashMap<>();
        this.map.put(key.getReasonPhrase(), value);
    }

}