package libs_kernel.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Base exception class for all application exceptions.
 * Provides structured error information with context for robust error handling.
 */
@Getter
public abstract class BaseException extends RuntimeException {

  private final HttpStatus httpStatus;
  private final String errorCode;
  private final String errorType;
  private final LocalDateTime timestamp;
  private final Map<String, Object> context;

  protected BaseException(String message, HttpStatus httpStatus, String errorCode) {
    super(message);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.errorType = this.getClass().getSimpleName();
    this.timestamp = LocalDateTime.now();
    this.context = new HashMap<>();
  }

  protected BaseException(String message, HttpStatus httpStatus, String errorCode, Throwable cause) {
    super(message, cause);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.errorType = this.getClass().getSimpleName();
    this.timestamp = LocalDateTime.now();
    this.context = new HashMap<>();
  }

  /**
   * Add contextual information to the exception for debugging purposes.
   *
   * @param key   The context key
   * @param value The context value
   * @return This exception instance for method chaining
   */
  public BaseException withContext(String key, Object value) {
    this.context.put(key, value);
    return this;
  }

  /**
   * Add multiple context entries at once.
   *
   * @param contextMap Map of context entries
   * @return This exception instance for method chaining
   */
  public BaseException withContext(Map<String, Object> contextMap) {
    if (contextMap != null) {
      this.context.putAll(contextMap);
    }
    return this;
  }

  /**
   * Get context as string map for error response.
   */
  public Map<String, String> getContextAsStringMap() {
    Map<String, String> stringMap = new HashMap<>();
    context.forEach((key, value) -> stringMap.put(key, value != null ? value.toString() : null));
    return stringMap;
  }
}
