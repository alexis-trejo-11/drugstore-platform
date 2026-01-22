package libs_kernel.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a service-level error occurs.
 * Used for internal server errors that are recoverable or expected.
 * HTTP Status: 500 INTERNAL_SERVER_ERROR (default) or specified status
 */
public class ServiceException extends BaseException {

  private static final String DEFAULT_ERROR_CODE = "SERVICE_ERROR";

  public ServiceException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_ERROR_CODE);
  }

  public ServiceException(String message, String errorCode) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_ERROR_CODE, cause);
  }

  public ServiceException(String message, String errorCode, Throwable cause) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR, errorCode, cause);
  }

  public ServiceException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  /**
   * Factory method for external service failures.
   */
  public static ServiceException externalServiceFailure(String serviceName, String operation, Throwable cause) {
    ServiceException ex = new ServiceException(
        String.format("External service '%s' failed during '%s' operation", serviceName, operation),
        "EXTERNAL_SERVICE_FAILURE",
        cause);
    return (ServiceException) ex.withContext("service", serviceName)
        .withContext("operation", operation);
  }

  /**
   * Factory method for database operation failures.
   */
  public static ServiceException databaseError(String operation, Throwable cause) {
    ServiceException ex = new ServiceException(
        String.format("Database error during '%s' operation", operation),
        "DATABASE_ERROR",
        cause);
    return (ServiceException) ex.withContext("operation", operation);
  }

  /**
   * Factory method for unavailable service.
   */
  public static ServiceException unavailable(String serviceName) {
    return new ServiceException(
        String.format("Service '%s' is currently unavailable", serviceName),
        HttpStatus.SERVICE_UNAVAILABLE,
        "SERVICE_UNAVAILABLE");
  }
}
