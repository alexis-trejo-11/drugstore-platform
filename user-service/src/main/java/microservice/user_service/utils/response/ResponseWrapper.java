package microservice.user_service.utils.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
    public boolean isSuccess;
    public String message;
    public T data;
    public LocalDateTime timestamp;
    public Map<String, ?> metadata;
    public ErrorDetails errorDetails;

    public ResponseWrapper<T> success(T data, String message, Map<String, ?> metadata) {
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }

    public ResponseWrapper<T> created(T data, String entity, Map<String, ?> metadata) {
        String message = String.format("%s created successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }

    public ResponseWrapper<T> created(T data, String entity) {
        return created(data, entity, Map.of());
    }

    public ResponseWrapper<T> created(T data) {
        return created(data, "Entity", Map.of());
    }

    public ResponseWrapper<T> found(T data, String entity, Map<String, ?> metadata) {
        String message = String.format("%s found successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }

    public ResponseWrapper<T> found(T data, String entity) {
        String message = String.format("%s found successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(Map.of());
        return response;
    }

    public ResponseWrapper<Void> notFound(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(true);
        response.setMessage("Entity not found");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(Map.of());
        return response;
    }

    public ResponseWrapper<Void> unauthorized(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Unauthorized access");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(Map.of());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<Void> forbidden(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Forbidden access");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(Map.of());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<Void> conflict(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Conflict occurred");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(Map.of());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<Void> unprocessableEntity(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Unprocessable entity");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(Map.of());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<Void> badRequest(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Bad request");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(Map.of());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<T> success(T data) {
        return success(data, "Operation successful");
    }


    public ResponseWrapper<T> success(T data, String message) {
        return success(data, "Operation successful", Map.of());
    }

    public ResponseWrapper<T> success() {
        return success(null, "Operation successful");
    }

    public ResponseWrapper<T> error(String message, Map<String, ?> metadata, ErrorDetails errorDetails) {
        var response = new ResponseWrapper<T>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorDetails(errorDetails);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }


    public ResponseWrapper<T> error(String message, Map<String, ?> metadata) {
        return error(message, metadata, null);
    }

    public ResponseWrapper<T> error(String message) {
        return error(message, Map.of(), null);
    }


    public ResponseWrapper<T> error(String message, ErrorDetails errorDetails) {
        return error(message, Map.of(), errorDetails);
    }
}

