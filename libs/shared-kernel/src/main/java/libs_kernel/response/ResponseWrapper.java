package libs_kernel.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {
    public boolean isSuccess;
    public String message;
    public T data;
    public LocalDateTime timestamp;
    public Map<String, ?> metadata;
    public ErrorDetails errorDetails;

    public static <T> ResponseWrapper<T> success(T data, String message, Map<String, ?> metadata) {
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }

    public static <T> ResponseWrapper<T> created(T data, String entity, Map<String, ?> metadata) {
        String message = String.format("%s created successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }

    public static <T> ResponseWrapper<T> created(T data, String entity) {
        return ResponseWrapper.created(data, entity, null);
    }

    public static <T> ResponseWrapper<T> created(T data) {
        return ResponseWrapper.created(data, "Entity", null);
    }

    public static  <T> ResponseWrapper<T> found(T data, String entity, Map<String, ?> metadata) {
        String message = String.format("%s found successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }

    public static <T> ResponseWrapper<T> found(T data, String entity) {
        String message = String.format("%s found successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public ResponseWrapper<Void> notFound(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(true);
        response.setMessage("Entity not found");
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public ResponseWrapper<Void> unauthorized(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Unauthorized access");
        response.setTimestamp(LocalDateTime.now());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<Void> forbidden(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Forbidden access");
        response.setTimestamp(LocalDateTime.now());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<Void> conflict(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Conflict occurred");
        response.setTimestamp(LocalDateTime.now());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public ResponseWrapper<Void> unprocessableEntity(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Unprocessable entity");
        response.setTimestamp(LocalDateTime.now());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public static ResponseWrapper<Void> badRequest(ErrorDetails errorDetails) {
        var response = new ResponseWrapper<Void>();
        response.setSuccess(false);
        response.setMessage("Bad request");
        response.setTimestamp(LocalDateTime.now());
        response.setErrorDetails(errorDetails);
        return response;
    }

    public static <T> ResponseWrapper<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ResponseWrapper<T> success(T data, String message) {
        return success(data, "Operation successful", Map.of());
    }

    public static <T> ResponseWrapper<T> success( String message) {
        return success(null, "Operation successful", Map.of());
    }

    public static <T> ResponseWrapper<T> success() {
        return success(null, "Operation successful");
    }

    public static <T> ResponseWrapper<T> error(String message, Map<String, ?> metadata, ErrorDetails errorDetails) {
        var response = new ResponseWrapper<T>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorDetails(errorDetails);
        response.setTimestamp(LocalDateTime.now());
        response.setMetadata(metadata);
        return response;
    }

    public static <T> ResponseWrapper<T> error(String message, Map<String, ?> metadata) {
        return ResponseWrapper.error(message, metadata, null);
    }

    public static <T> ResponseWrapper<T> error(String message) {
        return ResponseWrapper.error(message, Map.of(), null);
    }

    public static <T> ResponseWrapper<T> error(String message, ErrorDetails errorDetails) {
        return ResponseWrapper.error(message, Map.of(), errorDetails);
    }
}
