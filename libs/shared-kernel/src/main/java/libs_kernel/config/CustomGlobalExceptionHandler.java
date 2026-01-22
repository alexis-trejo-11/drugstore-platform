package libs_kernel.config;

import libs_kernel.exceptions.*;
import libs_kernel.response.Error;
import libs_kernel.response.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler providing comprehensive error handling with
 * structured responses.
 * <p>
 * This handler covers:
 * - Custom domain exceptions (BaseException hierarchy)
 * - Legacy domain exceptions (DomainException hierarchy)
 * - Spring MVC exceptions (routing, validation, media types)
 * - Generic fallback for unexpected errors
 * </p>
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomGlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(CustomGlobalExceptionHandler.class);

  // ==================== CUSTOM BASE EXCEPTION HANDLERS ====================

  /**
   * Handle all BaseException subclasses with structured response.
   */
  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ResponseWrapper<?>> handleBaseException(BaseException ex) {
    log.warn("BaseException occurred: [{}] {} - Context: {}",
        ex.getErrorCode(), ex.getMessage(), ex.getContext());

    Error error = buildError(ex.getErrorCode(), ex.getMessage(), ex.getErrorType());
    if (!ex.getContext().isEmpty()) {
      error.setDetails(ex.getContextAsStringMap());
    }

    ResponseWrapper<?> response = ResponseWrapper.error(ex.getMessage(), error);
    return new ResponseEntity<>(response, ex.getHttpStatus());
  }

  // ==================== LEGACY DOMAIN EXCEPTION HANDLERS ====================

  /**
   * Handle rate limit exceeded exceptions.
   */
  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<ResponseWrapper<?>> handleRateLimitExceededException(RateLimitExceededException ex) {
    log.warn("Rate limit exceeded: {}", ex.getMessage());

    Error error = buildError("RATE_LIMIT_EXCEEDED", ex.getMessage(), ex.getClass().getSimpleName());
    ResponseWrapper<?> response = ResponseWrapper.error("Too Many Requests", error);

    return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
  }

  /**
   * Handle legacy DomainException hierarchy.
   */
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ResponseWrapper<?>> handleDomainException(DomainException ex) {
    log.warn("DomainException occurred: [{}] {}", ex.getErrorCode(), ex.getMessage());

    Error error = buildError(ex.getErrorCode(), ex.getMessage(), ex.getClass().getSimpleName());
    error.setDetails(Map.of("message", ex.getMessage()));

    ResponseWrapper<?> response = ResponseWrapper.error("Domain Exception Occurred", error);
    return new ResponseEntity<>(response, ex.getHttpStatus());
  }

  // ==================== SPRING VALIDATION EXCEPTION HANDLERS
  // ====================

  /**
   * Handle @Valid annotation validation failures.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResponseWrapper<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.warn("Validation failed for request: {} errors", ex.getBindingResult().getErrorCount());

    Map<String, String> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
            (existing, newMsg) -> existing + "; " + newMsg));

    Error error = buildError("VALIDATION_FAILED", "Request validation failed", "ValidationException");
    error.setDetails(fieldErrors);

    ResponseWrapper<?> response = ResponseWrapper.error("Validation Error", error);
    return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * Handle missing request parameters.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ResponseWrapper<?>> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex) {
    log.warn("Missing required parameter: {} (type: {})", ex.getParameterName(), ex.getParameterType());

    Error error = buildError("MISSING_PARAMETER",
        String.format("Required parameter '%s' of type '%s' is missing",
            ex.getParameterName(), ex.getParameterType()),
        "MissingParameterException");
    error.setDetails(Map.of(
        "parameter", ex.getParameterName(),
        "expectedType", ex.getParameterType()));

    ResponseWrapper<?> response = ResponseWrapper.error("Missing Required Parameter", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle missing path variables.
   */
  @ExceptionHandler(MissingPathVariableException.class)
  public ResponseEntity<ResponseWrapper<?>> handleMissingPathVariable(MissingPathVariableException ex) {
    log.warn("Missing path variable: {}", ex.getVariableName());

    Error error = buildError("MISSING_PATH_VARIABLE",
        String.format("Required path variable '%s' is missing", ex.getVariableName()),
        "MissingPathVariableException");
    error.setDetails(Map.of("variable", ex.getVariableName()));

    ResponseWrapper<?> response = ResponseWrapper.error("Missing Path Variable", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle type mismatch for parameters.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ResponseWrapper<?>> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
    log.warn("Type mismatch for parameter '{}': expected {}, got '{}'",
        ex.getName(), expectedType, ex.getValue());

    Error error = buildError("TYPE_MISMATCH",
        String.format("Parameter '%s' should be of type '%s'", ex.getName(), expectedType),
        "TypeMismatchException");
    error.setDetails(Map.of(
        "parameter", ex.getName(),
        "expectedType", expectedType,
        "providedValue", ex.getValue() != null ? ex.getValue().toString() : "null"));

    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Parameter Type", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle general type mismatch.
   */
  @ExceptionHandler(TypeMismatchException.class)
  public ResponseEntity<ResponseWrapper<?>> handleTypeMismatch(TypeMismatchException ex) {
    String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
    log.warn("Type mismatch: expected {}", expectedType);

    Error error = buildError("TYPE_MISMATCH",
        String.format("Expected type '%s'", expectedType),
        "TypeMismatchException");

    ResponseWrapper<?> response = ResponseWrapper.error("Type Mismatch", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // ==================== SPRING HTTP/ROUTING EXCEPTION HANDLERS
  // ====================

  /**
   * Handle HTTP method not allowed (e.g., POST to GET-only endpoint).
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ResponseWrapper<?>> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex) {
    log.warn("Method '{}' not allowed. Supported methods: {}",
        ex.getMethod(), ex.getSupportedHttpMethods());

    String supportedMethods = ex.getSupportedHttpMethods() != null
        ? ex.getSupportedHttpMethods().toString()
        : "none";

    Error error = buildError("METHOD_NOT_ALLOWED",
        String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod()),
        "MethodNotAllowedException");
    error.setDetails(Map.of(
        "method", ex.getMethod(),
        "supportedMethods", supportedMethods));

    ResponseWrapper<?> response = ResponseWrapper.error("Method Not Allowed", error);
    return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * Handle unsupported media type (Content-Type header).
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ResponseWrapper<?>> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex) {
    log.warn("Media type '{}' not supported. Supported types: {}",
        ex.getContentType(), ex.getSupportedMediaTypes());

    Error error = buildError("UNSUPPORTED_MEDIA_TYPE",
        String.format("Content type '%s' is not supported", ex.getContentType()),
        "MediaTypeNotSupportedException");
    error.setDetails(Map.of(
        "contentType", ex.getContentType() != null ? ex.getContentType().toString() : "unknown",
        "supportedTypes", ex.getSupportedMediaTypes().toString()));

    ResponseWrapper<?> response = ResponseWrapper.error("Unsupported Media Type", error);
    return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  /**
   * Handle not acceptable media type (Accept header).
   */
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<ResponseWrapper<?>> handleHttpMediaTypeNotAcceptable(
      HttpMediaTypeNotAcceptableException ex) {
    log.warn("Media type not acceptable. Supported types: {}", ex.getSupportedMediaTypes());

    Error error = buildError("NOT_ACCEPTABLE",
        "The requested media type is not supported",
        "MediaTypeNotAcceptableException");
    error.setDetails(Map.of(
        "supportedTypes", ex.getSupportedMediaTypes().toString()));

    ResponseWrapper<?> response = ResponseWrapper.error("Not Acceptable", error);
    return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
  }

  /**
   * Handle endpoint not found (bad routing) - NoHandlerFoundException.
   * Requires: spring.mvc.throw-exception-if-no-handler-found=true
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ResponseWrapper<?>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
    log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());

    Error error = buildError("ENDPOINT_NOT_FOUND",
        String.format("No endpoint found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
        "EndpointNotFoundException");
    error.setDetails(Map.of(
        "method", ex.getHttpMethod(),
        "path", ex.getRequestURL()));

    ResponseWrapper<?> response = ResponseWrapper.error("Endpoint Not Found", error);
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle resource not found (Spring 6+).
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ResponseWrapper<?>> handleNoResourceFoundException(NoResourceFoundException ex) {
    log.warn("No resource found for {} {}", ex.getHttpMethod(), ex.getResourcePath());

    Error error = buildError("RESOURCE_NOT_FOUND",
        String.format("No resource found for %s %s", ex.getHttpMethod(), ex.getResourcePath()),
        "ResourceNotFoundException");
    error.setDetails(Map.of(
        "method", ex.getHttpMethod().name(),
        "path", ex.getResourcePath()));

    ResponseWrapper<?> response = ResponseWrapper.error("Resource Not Found", error);
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  // ==================== REQUEST BODY/PARSING EXCEPTION HANDLERS
  // ====================

  /**
   * Handle malformed JSON or unreadable request body.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ResponseWrapper<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    log.warn("Failed to read request body: {}", ex.getMessage());

    String detailMessage = "Request body is missing or malformed";
    Throwable cause = ex.getCause();
    if (cause != null) {
      detailMessage = cause.getMessage();
    }

    Error error = buildError("MALFORMED_REQUEST_BODY",
        "Unable to parse request body",
        "MessageNotReadableException");
    error.setDetails(Map.of("detail", detailMessage));

    ResponseWrapper<?> response = ResponseWrapper.error("Malformed Request Body", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle missing multipart file.
   */
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ResponseWrapper<?>> handleMissingServletRequestPart(
      MissingServletRequestPartException ex) {
    log.warn("Missing request part: {}", ex.getRequestPartName());

    Error error = buildError("MISSING_REQUEST_PART",
        String.format("Required request part '%s' is missing", ex.getRequestPartName()),
        "MissingRequestPartException");
    error.setDetails(Map.of("partName", ex.getRequestPartName()));

    ResponseWrapper<?> response = ResponseWrapper.error("Missing Request Part", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle file upload size exceeded.
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ResponseWrapper<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
    log.warn("File upload size exceeded: {}", ex.getMessage());

    Error error = buildError("FILE_SIZE_EXCEEDED",
        "The uploaded file exceeds the maximum allowed size",
        "MaxUploadSizeExceededException");
    error.setDetails(Map.of(
        "maxSize", String.valueOf(ex.getMaxUploadSize())));

    ResponseWrapper<?> response = ResponseWrapper.error("File Size Exceeded", error);
    return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
  }

  // ==================== GENERIC FALLBACK HANDLER ====================

  /**
   * Generic fallback handler for all unhandled exceptions.
   * Logs the full stack trace for debugging while returning a safe message to
   * clients.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseWrapper<?>> handleGenericException(Exception ex) {
    // Log full stack trace for debugging
    log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

    Error error = buildError("INTERNAL_SERVER_ERROR",
        "An unexpected error occurred. Please try again later.",
        ex.getClass().getSimpleName());

    // In development, you might want to include more details
    // In production, keep the response generic for security
    Map<String, String> details = new HashMap<>();
    details.put("exceptionType", ex.getClass().getName());
    // Uncomment for development debugging:
    // details.put("message", ex.getMessage());
    error.setDetails(details);

    ResponseWrapper<?> response = ResponseWrapper.error("Internal Server Error", error);
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // ==================== HELPER METHODS ====================

  /**
   * Build a standardized Error object.
   */
  private Error buildError(String errorCode, String errorMessage, String errorType) {
    Error error = new Error();
    error.setErrorCode(errorCode);
    error.setErrorMessage(errorMessage);
    error.setErrorType(errorType);
    return error;
  }
}
