package libs_kernel.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * A generic response wrapper class used to standardize API responses across the application.
 * This wrapper provides a consistent structure for all API responses, including success/error status,
 * data payload, messages, and error details.
 *
 * @param <T> The type of data being wrapped in the response
 *
 * @author Alexis Trejo
 * @version 2.0
 * @since 2.0
 */
@Data
@NoArgsConstructor
@JsonRootName(value = "ResponseWrapper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {

    /**
     * Indicates whether the operation was successful or not.
     * true = successful operation, false = failed operation
     */
    @JsonProperty("success")
    private boolean success;

    /**
     * The actual data payload of the response.
     * Can be null for operations that don't return data (like delete operations).
     */
    @JsonProperty("data")
    private T data;

    /**
     * A human-readable message describing the result of the operation.
     * Should provide context about what happened.
     */
    @JsonProperty("message")
    private String message;

    /**
     * Map containing detailed error information when the operation fails.
     * Key represents the field/context, value represents the error message.
     * Only populated when success = false and detailed errors are available.
     */
    @JsonProperty("errors")
    private Map<String, String> errors;

    /**
     * Primary constructor for creating a ResponseWrapper with core fields.
     *
     * @param success Indicates if the operation was successful
     * @param data    The data payload to include in the response
     * @param message A descriptive message about the operation result
     */
    public ResponseWrapper(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    /**
     * Constructor for creating a ResponseWrapper with error details.
     *
     * @param success Indicates if the operation was successful (should be false for errors)
     * @param data    The data payload to include in the response
     * @param message A descriptive message about the operation result
     * @param errors  Map of detailed error information
     */
    public ResponseWrapper(boolean success, T data, String message, Map<String, String> errors) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errors = errors;
    }

    // ==================== SUCCESS RESPONSE BUILDERS ====================

    /**
     * Creates a success response for entity creation without returning data.
     *
     * @param entityName The name of the entity that was created
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful creation
     */
    public static <T> ResponseWrapper<T> created(String entityName) {
        String createMessage = entityName + " successfully created";
        return new ResponseWrapper<>(true, null, createMessage);
    }

    /**
     * Creates a success response for entity creation with returned data.
     *
     * @param data       The created entity data to return
     * @param entityName The name of the entity that was created
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful creation with data
     */
    public static <T> ResponseWrapper<T> created(T data, String entityName) {
        String createMessage = entityName + " successfully created";
        return new ResponseWrapper<>(true, data, createMessage);
    }

    /**
     * Creates a success response for entity update without returning data.
     *
     * @param entityName The name of the entity that was updated
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful update
     */
    public static <T> ResponseWrapper<T> updated(String entityName) {
        String updateMessage = entityName + " successfully updated";
        return new ResponseWrapper<>(true, null, updateMessage);
    }

    /**
     * Creates a success response for entity update with returned data.
     *
     * @param data       The updated entity data to return
     * @param entityName The name of the entity that was updated
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful update with data
     */
    public static <T> ResponseWrapper<T> updated(T data, String entityName) {
        String updateMessage = entityName + " successfully updated";
        return new ResponseWrapper<>(true, data, updateMessage);
    }

    /**
     * Creates a success response for entity deletion.
     *
     * @param entityName The name of the entity that was deleted
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful deletion
     */
    public static <T> ResponseWrapper<T> deleted(String entityName) {
        String deleteMessage = entityName + " successfully deleted";
        return new ResponseWrapper<>(true, null, deleteMessage);
    }

    /**
     * Creates a success response for data retrieval operations.
     *
     * @param data       The retrieved data
     * @param entityName The name of the entity that was retrieved
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful data retrieval
     */
    public static <T> ResponseWrapper<T> found(T data, String entityName) {
        String foundMessage = entityName + " data successfully retrieved";
        return new ResponseWrapper<>(true, data, foundMessage);
    }

    /**
     * Creates a generic success response with custom action and data.
     *
     * @param data       The data to return
     * @param entityName The name of the entity involved in the action
     * @param action     The action that was performed (will be appended with 'ed')
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful action completion
     */
    public static <T> ResponseWrapper<T> success(T data, String entityName, String action) {
        String successMessage = entityName + " successfully " + action + "ed";
        return new ResponseWrapper<>(true, data, successMessage);
    }

    /**
     * Creates a generic success response with custom action without data.
     *
     * @param entityName The name of the entity involved in the action
     * @param action     The action that was performed (will be appended with 'ed')
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating successful action completion
     */
    public static <T> ResponseWrapper<T> success(String entityName, String action) {
        String successMessage = entityName + " successfully " + action + "ed";
        return new ResponseWrapper<>(true, null, successMessage);
    }

    /**
     * Creates a generic success response with a custom message.
     *
     * @param message The success message
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating success with custom message
     */
    public static <T> ResponseWrapper<T> success(String message) {
        return new ResponseWrapper<>(true, null, message);
    }

    /**
     * Creates a generic success response with data and custom message.
     *
     * @param data    The data to return
     * @param message The success message
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating success with data and custom message
     */
    public static <T> ResponseWrapper<T> success(T data, String message) {
        return new ResponseWrapper<>(true, data, message);
    }

    // ==================== ERROR RESPONSE BUILDERS ====================

    /**
     * Creates an error response for "not found" scenarios.
     *
     * @param entityName The name of the entity that was not found
     * @param <T>        The type parameter for the response wrapper
     * @return ResponseWrapper indicating entity not found
     */
    public static <T> ResponseWrapper<T> notFound(String entityName) {
        String notFoundMessage = entityName + " not found";
        return new ResponseWrapper<>(false, null, notFoundMessage);
    }

    /**
     * Creates an error response for bad request scenarios.
     *
     * @param message The error message explaining what went wrong
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating bad request
     */
    public static <T> ResponseWrapper<T> badRequest(String message) {
        return new ResponseWrapper<>(false, null, message);
    }

    /**
     * Creates an error response for unauthorized access scenarios.
     *
     * @param message The error message explaining the authorization issue
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating unauthorized access
     */
    public static <T> ResponseWrapper<T> unauthorized(String message) {
        return new ResponseWrapper<>(false, null, message);
    }

    /**
     * Creates an error response for forbidden access scenarios.
     *
     * @param message The error message explaining the access restriction
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating forbidden access
     */
    public static <T> ResponseWrapper<T> forbidden(String message) {
        return new ResponseWrapper<>(false, null, message);
    }

    /**
     * Creates an error response for internal server error scenarios.
     *
     * @param message The error message explaining the server error
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating internal server error
     */
    public static <T> ResponseWrapper<T> internalServerError(String message) {
        return new ResponseWrapper<>(false, null, message);
    }

    /**
     * Creates an error response with detailed field-level errors.
     * Typically used for validation errors where multiple fields may have issues.
     *
     * @param errors Map containing field names as keys and error messages as values
     * @param <T>    The type parameter for the response wrapper
     * @return ResponseWrapper indicating validation or detailed errors
     */
    public static <T> ResponseWrapper<T> validationError(Map<String, String> errors) {
        String errorMessage = "Validation failed";
        return new ResponseWrapper<>(false, null, errorMessage, errors);
    }

    /**
     * Creates a generic error response with custom message and detailed errors.
     *
     * @param message The main error message
     * @param errors  Map containing detailed error information
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating error with detailed information
     */
    public static <T> ResponseWrapper<T> error(String message, Map<String, String> errors) {
        return new ResponseWrapper<>(false, null, message, errors);
    }

    /**
     * Creates a generic error response with just a message.
     *
     * @param message The error message
     * @param <T>     The type parameter for the response wrapper
     * @return ResponseWrapper indicating generic error
     */
    public static <T> ResponseWrapper<T> error(String message) {
        return new ResponseWrapper<>(false, null, message);
    }
}