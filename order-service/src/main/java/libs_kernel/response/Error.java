package libs_kernel.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {
  private String errorCode;
  private String errorMessage;
  private String errorType;
  private Map<String, ?> details;

  public Error() {
  }

  public Error(String errorCode, String errorMessage, String errorType, Map<String, ?> details) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.errorType = errorType;
    this.details = details;
  }

  public void setErrorType(String errorType) {
    this.errorType = errorType;
  }

  public Map<String, ?> getDetails() {
    return details;
  }

  public void setDetails(Map<String, ?> details) {
    this.details = details;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorType() {
    return errorType;
  }

}
