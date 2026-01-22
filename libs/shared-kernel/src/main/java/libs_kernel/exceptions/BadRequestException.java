package libs_kernel.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Exception thrown when the request is malformed or contains invalid data.
 * HTTP Status: 400 BAD_REQUEST
 */
public class BadRequestException extends BaseException {

  private static final String DEFAULT_ERROR_CODE = "BAD_REQUEST";

  public BadRequestException(String message) {
    super(message, HttpStatus.BAD_REQUEST, DEFAULT_ERROR_CODE);
  }

  public BadRequestException(String message, String errorCode) {
    super(message, HttpStatus.BAD_REQUEST, errorCode);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, HttpStatus.BAD_REQUEST, DEFAULT_ERROR_CODE, cause);
  }

  /**
   * Factory method for invalid parameter exceptions.
   */
  public static BadRequestException invalidParameter(String parameterName, Object value, String reason) {
    BadRequestException ex = new BadRequestException(
        String.format("Invalid parameter '%s': %s", parameterName, reason),
        "INVALID_PARAMETER");
    return (BadRequestException) ex.withContext("parameter", parameterName)
        .withContext("value", value)
        .withContext("reason", reason);
  }

  /**
   * Factory method for missing required parameter exceptions.
   */
  public static BadRequestException missingParameter(String parameterName) {
    BadRequestException ex = new BadRequestException(
        String.format("Required parameter '%s' is missing", parameterName),
        "MISSING_PARAMETER");
    return (BadRequestException) ex.withContext("parameter", parameterName);
  }

  /**
   * Factory method for invalid format exceptions.
   */
  public static BadRequestException invalidFormat(String fieldName, String expectedFormat) {
    BadRequestException ex = new BadRequestException(
        String.format("Invalid format for '%s'. Expected format: %s", fieldName, expectedFormat),
        "INVALID_FORMAT");
    return (BadRequestException) ex.withContext("field", fieldName)
        .withContext("expectedFormat", expectedFormat);
  }
}
