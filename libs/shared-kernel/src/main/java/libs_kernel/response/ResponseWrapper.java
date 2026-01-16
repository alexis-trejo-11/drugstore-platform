package libs_kernel.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Generic response wrapper for standardizing API responses across microservices.
 * <p>
 * This class provides a consistent structure for all API responses, including both
 * successful and error responses. It encapsulates the response data, status, message,
 * timestamp, and error details in a uniform format.
 * </p>
 *
 * <h3>Response Structure:</h3>
 * <pre>
 * {
 *   "isSuccess": true/false,
 *   "message": "Descriptive message",
 *   "data": { ... },
 *   "timestamp": "2025-10-08T10:30:00",
 *   "error": { ... } // Only present in error responses
 * }
 * </pre>
 *
 * <h3>Usage Examples:</h3>
 * <pre>
 * // Success with data
 * ResponseWrapper&lt;User&gt; response = ResponseWrapper.success(user, "User retrieved");
 *
 * // Created entity
 * ResponseWrapper&lt;Product&gt; response = ResponseWrapper.created(product, "Product");
 *
 * // Error response
 * ResponseWrapper&lt;Void&gt; response = ResponseWrapper.badRequest(error);
 * </pre>
 *
 * @param <T> The type of data contained in the response
 * @author Alexis Trejo
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {
    /**
     * Human-readable message describing the response.
     * <p>
     * Examples: "User created successfully", "Validation failed", "Entity not found"
     * </p>
     */
    private String message;

    /**
     * The actual response payload data.
     * <p>
     * This field is null in error responses and when no data is returned.
     * The type is generic to support any kind of response data.
     * </p>
     */
    private T data;

    /**
     * Timestamp when the response was created.
     * <p>
     * Automatically set to the current time when the response is generated.
     * </p>
     */
    private LocalDateTime timestamp;

    /**
     * Detailed error information for failed operations.
     * <p>
     * This field is only populated when {@code isSuccess} is {@code false}.
     * Contains validation errors, stack traces, or other error-specific information.
     * </p>
     */
    public Error error;

    /**
     * Creates a successful response with data and a custom message.
     *
     * @param <T> The type of data in the response
     * @param data The response payload
     * @param message Custom success message
     * @return A success ResponseWrapper containing the data and message
     * @example
     * <pre>
     * User user = userService.getUser(id);
     * return ResponseWrapper.success(user, "User fetched successfully");
     * </pre>
     */
    public static <T> ResponseWrapper<T> success(T data, String message) {
        var response = new ResponseWrapper<T>();
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a successful response with a default message and no data.
     *
     * @param <T> The type of data in the response
     * @param message Custom success message
     * @return A success ResponseWrapper with no data
     * @example
     * <pre>
     * return ResponseWrapper.success("Operation completed");
     * </pre>
     */
    public static <T> ResponseWrapper<T> success(String message) {
        return success(null, message);
    }

    /**
     * Creates a successful response with a default message and no data.
     * <p>
     * Convenience method when no data or custom message is needed.
     * Uses "Operation completed successfully" as the default message.
     * </p>
     *
     * @param <T> The type of data in the response
     * @return A success ResponseWrapper with no data and default message
     * @example
     * <pre>
     * return ResponseWrapper.success();
     * // Message: "Operation completed successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> success() {
        return success(null, "Operation completed successfully");
    }

    /**
     * Creates a successful response for entity retrieval with metadata.
     * <p>
     * This method is useful when returning search results or paginated data
     * that includes additional metadata information.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The retrieved entity or entities
     * @param entity The name of the entity type (e.g., "User", "Product", "Orders")
     * @param metadata Additional metadata (pagination info, filters, etc.)
     * @return A success ResponseWrapper with data and formatted message
     * @example
     * <pre>
     * Map&lt;String, Object&gt; metadata = Map.of("page", 1, "totalPages", 10);
     * return ResponseWrapper.found(users, "Users", metadata);
     * </pre>
     */
    public static  <T> ResponseWrapper<T> found(T data, String entity, Map<String, ?> metadata) {
        String message = String.format("%s found successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a successful response for entity retrieval.
     * <p>
     * Generates a standardized message indicating the entity was found successfully.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The retrieved entity
     * @param entity The name of the entity type (e.g., "User", "Product")
     * @return A success ResponseWrapper with data and formatted message
     * @example
     * <pre>
     * Product product = productService.findById(id);
     * return ResponseWrapper.found(product, "Product");
     * // Message: "Product found successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> found(T data, String entity) {
        String message = String.format("%s found successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a successful response for entity creation.
     * <p>
     * Used when a new entity has been successfully created.
     * Typically corresponds to HTTP 201 Created status.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The newly created entity
     * @param entity The name of the entity type (e.g., "Order", "Customer")
     * @return A success ResponseWrapper with the created entity and formatted message
     * @example
     * <pre>
     * Order newOrder = orderService.create(orderRequest);
     * return ResponseWrapper.created(newOrder, "Order");
     * // Message: "Order created successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> created(T data, String entity) {
        String message = String.format("%s created successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a successful response for entity creation with default entity name.
     * <p>
     * Convenience method when the specific entity name is not important.
     * Uses "Entity" as the default entity type name.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The newly created entity
     * @return A success ResponseWrapper with the created entity
     * @example
     * <pre>
     * return ResponseWrapper.created(newEntity);
     * // Message: "Entity created successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> created(T data) {
        return ResponseWrapper.created(data, "Entity");
    }

    /**
     * Creates a successful response for entity update.
     * <p>
     * Used when an existing entity has been successfully updated.
     * Typically corresponds to HTTP 200 OK status.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The updated entity
     * @param entity The name of the entity type (e.g., "Profile", "Address")
     * @return A success ResponseWrapper with the updated entity and formatted message
     * @example
     * <pre>
     * User updatedUser = userService.update(id, updateRequest);
     * return ResponseWrapper.updated(updatedUser, "User");
     * // Message: "User updated successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> updated(T data, String entity) {
        String message = String.format("%s updated successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a successful response for entity update with default entity name.
     * <p>
     * Convenience method when the specific entity name is not important.
     * Uses "Entity" as the default entity type name.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The updated entity
     * @return A success ResponseWrapper with the updated entity
     * @example
     * <pre>
     * return ResponseWrapper.updated(updatedEntity);
     * // Message: "Entity updated successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> updated(T data) {
        return ResponseWrapper.updated(data, "Entity");
    }

    /**
     * Creates a successful response for entity deletion.
     * <p>
     * Used when an entity has been successfully deleted.
     * Typically corresponds to HTTP 200 OK or 204 No Content status.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The deleted entity or deletion confirmation
     * @param entity The name of the entity type (e.g., "Address", "Cart")
     * @return A success ResponseWrapper with deletion confirmation and formatted message
     * @example
     * <pre>
     * orderService.delete(purchaseOrderId);
     * return ResponseWrapper.deleted(null, "Order");
     * // Message: "Order deleted successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> deleted(T data, String entity) {
        String message = String.format("%s deleted successfully", entity);
        var response = new ResponseWrapper<T>();
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a successful response for entity deletion with default entity name.
     * <p>
     * Convenience method when the specific entity name is not important.
     * Uses "Entity" as the default entity type name.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param data The deleted entity or deletion confirmation
     * @return A success ResponseWrapper with deletion confirmation
     * @example
     * <pre>
     * return ResponseWrapper.deleted(null);
     * // Message: "Entity deleted successfully"
     * </pre>
     */
    public static <T> ResponseWrapper<T> deleted(T data) {
        return ResponseWrapper.deleted(data, "Entity");
    }

    /**
     * Creates an error response for entity not found scenarios.
     * <p>
     * Used when a requested entity cannot be found in the system.
     * Typically corresponds to HTTP 404 Not Found status.
     * </p>
     * <p>
     * <strong>Note:</strong> This method currently has a bug where {@code isSuccess}
     * is set to {@code true} but should be {@code false} for error responses.
     * </p>
     *
     * @param error Additional error information and context
     * @return An error ResponseWrapper with not found message
     * @example
     * <pre>
     * Error details = new Error("USER_NOT_FOUND", "No user with id: 123");
     * return ResponseWrapper.notFound(details);
     * </pre>
     */
    public ResponseWrapper<Void> notFound(Error error) {
        var response = new ResponseWrapper<Void>();
        response.setMessage("Entity not found");
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates an error response for unauthorized access attempts.
     * <p>
     * Used when a user is not authenticated or authentication token is invalid.
     * Typically corresponds to HTTP 401 Unauthorized status.
     * </p>
     *
     * @param error Additional error information and context
     * @return An error ResponseWrapper with unauthorized message
     * @example
     * <pre>
     * Error details = new Error("AUTH_REQUIRED", "Valid token required");
     * return ResponseWrapper.unauthorized(details);
     * </pre>
     */
    public ResponseWrapper<Void> unauthorized(Error error) {
        var response = new ResponseWrapper<Void>();
        response.setMessage("Unauthorized access");
        response.setTimestamp(LocalDateTime.now());
        response.setError(error);
        return response;
    }

    /**
     * Creates an error response for forbidden access attempts.
     * <p>
     * Used when a user is authenticated but lacks sufficient permissions.
     * Typically corresponds to HTTP 403 Forbidden status.
     * </p>
     *
     * @param error Additional error information and context
     * @return An error ResponseWrapper with forbidden message
     * @example
     * <pre>
     * Error details = new Error("INSUFFICIENT_PERMISSIONS",
     *     "Admin role required for this action");
     * return ResponseWrapper.forbidden(details);
     * </pre>
     */
    public ResponseWrapper<Void> forbidden(Error error) {
        var response = new ResponseWrapper<Void>();
        response.setMessage("Forbidden access");
        response.setTimestamp(LocalDateTime.now());
        response.setError(error);
        return response;
    }

    /**
     * Creates an error response for resource conflicts.
     * <p>
     * Used when the request conflicts with the current state of the resource.
     * Common scenarios include duplicate entries or version conflicts.
     * Typically corresponds to HTTP 409 Conflict status.
     * </p>
     *
     * @param error Additional error information and context
     * @return An error ResponseWrapper with conflict message
     * @example
     * <pre>
     * Error details = new Error("DUPLICATE_EMAIL",
     *     "User with this email already exists");
     * return ResponseWrapper.conflict(details);
     * </pre>
     */
    public ResponseWrapper<Void> conflict(Error error) {
        var response = new ResponseWrapper<Void>();
        response.setMessage("Conflict occurred");
        response.setTimestamp(LocalDateTime.now());
        response.setError(error);
        return response;
    }

    /**
     * Creates an error response for unprocessable entity errors.
     * <p>
     * Used when the request is syntactically correct but semantically invalid.
     * Common for business logic validation failures.
     * Typically corresponds to HTTP 422 Unprocessable Entity status.
     * </p>
     *
     * @param error Additional error information and context
     * @return An error ResponseWrapper with unprocessable entity message
     * @example
     * <pre>
     * Error details = new Error("INVALID_BUSINESS_RULE",
     *     "Order total must be greater than shipping cost");
     * return ResponseWrapper.unprocessableEntity(details);
     * </pre>
     */
    public ResponseWrapper<Void> unprocessableEntity(Error error) {
        var response = new ResponseWrapper<Void>();
        response.setMessage("Unprocessable entity");
        response.setTimestamp(LocalDateTime.now());
        response.setError(error);
        return response;
    }

    /**
     * Creates an error response for bad request errors.
     * <p>
     * Used when the request is malformed or contains invalid parameters.
     * Common for validation errors, missing required fields, or invalid data formats.
     * Typically corresponds to HTTP 400 Bad Request status.
     * </p>
     *
     * @param error Additional error information including validation errors
     * @return An error ResponseWrapper with bad request message
     * @example
     * <pre>
     * Error details = new Error("VALIDATION_FAILED",
     *     Map.of("email", "Invalid email format", "age", "Must be positive"));
     * return ResponseWrapper.badRequest(details);
     * </pre>
     */
    public static ResponseWrapper<Void> badRequest(Error error) {
        var response = new ResponseWrapper<Void>();
        response.setMessage("Bad request");
        response.setTimestamp(LocalDateTime.now());
        response.setError(error);
        return response;
    }

    /**
     * Creates a generic error response with custom message and error details.
     * <p>
     * This is the most flexible error method, allowing custom error messages
     * for scenarios not covered by the specific error methods.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param message Custom error message
     * @param error Additional error information and context
     * @return An error ResponseWrapper with the specified message and details
     * @example
     * <pre>
     * Error details = new Error("EXTERNAL_SERVICE_ERROR",
     *     "Payment gateway unavailable");
     * return ResponseWrapper.error("Payment processing failed", details);
     * </pre>
     */
    public static <T> ResponseWrapper<T> error(String message, Error error) {
        var response = new ResponseWrapper<T>();
        response.setMessage(message);
        response.setError(error);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a generic error response with custom message and no error details.
     * <p>
     * Convenience method for simple error responses that don't require
     * detailed error information.
     * </p>
     *
     * @param <T> The type of data in the response
     * @param message Custom error message
     * @return An error ResponseWrapper with the specified message
     * @example
     * <pre>
     * return ResponseWrapper.error("An unexpected error occurred");
     * </pre>
     */
    public static <T> ResponseWrapper<T> error(String message) {
        return ResponseWrapper.error(message, null);
    }

    public static <T> ResponseWrapper<T> error( Map<String, String> errors) {
        return ResponseWrapper.error(errors.toString(), null);
    }

}
