package libs_kernel.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * HTTP Status: 404 NOT_FOUND
 */
public class ResourceNotFoundException extends BaseException {

  private static final String DEFAULT_ERROR_CODE = "RESOURCE_NOT_FOUND";

  public ResourceNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND, DEFAULT_ERROR_CODE);
  }

  public ResourceNotFoundException(String message, String errorCode) {
    super(message, HttpStatus.NOT_FOUND, errorCode);
  }

  public ResourceNotFoundException(String resourceName, String identifier, Object identifierValue) {
    super(
        String.format("%s with %s '%s' not found", resourceName, identifier, identifierValue),
        HttpStatus.NOT_FOUND,
        DEFAULT_ERROR_CODE);
    this.withContext("resource", resourceName)
        .withContext("identifier", identifier)
        .withContext("identifierValue", identifierValue);
  }

  /**
   * Factory method for creating not found exception by ID.
   */
  public static ResourceNotFoundException byId(String resourceName, Object id) {
    return new ResourceNotFoundException(resourceName, "ID", id);
  }

  /**
   * Factory method for creating not found exception by any field.
   */
  public static ResourceNotFoundException byField(String resourceName, String fieldName, Object fieldValue) {
    return new ResourceNotFoundException(resourceName, fieldName, fieldValue);
  }
}
