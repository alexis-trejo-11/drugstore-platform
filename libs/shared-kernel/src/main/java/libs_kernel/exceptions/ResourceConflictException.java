package libs_kernel.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there's a conflict with the current state of a
 * resource.
 * Examples: duplicate entries, concurrent modifications, state conflicts.
 * HTTP Status: 409 CONFLICT
 */
public class ResourceConflictException extends BaseException {

  private static final String DEFAULT_ERROR_CODE = "RESOURCE_CONFLICT";

  public ResourceConflictException(String message) {
    super(message, HttpStatus.CONFLICT, DEFAULT_ERROR_CODE);
  }

  public ResourceConflictException(String message, String errorCode) {
    super(message, HttpStatus.CONFLICT, errorCode);
  }

  /**
   * Factory method for duplicate resource conflicts.
   */
  public static ResourceConflictException duplicate(String resourceName, String fieldName, Object fieldValue) {
    ResourceConflictException ex = new ResourceConflictException(
        String.format("%s with %s '%s' already exists", resourceName, fieldName, fieldValue),
        "DUPLICATE_RESOURCE");
    return (ResourceConflictException) ex.withContext("resource", resourceName)
        .withContext("field", fieldName)
        .withContext("value", fieldValue);
  }

  /**
   * Factory method for state conflict exceptions.
   */
  public static ResourceConflictException stateConflict(String resourceName, String currentState,
      String expectedState) {
    ResourceConflictException ex = new ResourceConflictException(
        String.format("Cannot perform operation on %s. Current state: '%s', expected: '%s'",
            resourceName, currentState, expectedState),
        "STATE_CONFLICT");
    return (ResourceConflictException) ex.withContext("resource", resourceName)
        .withContext("currentState", currentState)
        .withContext("expectedState", expectedState);
  }
}
