package at.backend.drugstore.microservice.common_models.Utils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseWrapper<T> {
    private boolean success;
    private T data;
    private String message;
    private int statusCode;

    public ResponseWrapper(boolean success, T data, String message, int statusCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }

}

