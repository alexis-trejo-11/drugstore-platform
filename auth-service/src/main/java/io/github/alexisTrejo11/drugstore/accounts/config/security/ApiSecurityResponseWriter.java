package io.github.alexisTrejo11.drugstore.accounts.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import libs_kernel.response.Error;
import libs_kernel.response.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class ApiSecurityResponseWriter {

  private final ObjectMapper objectMapper;

  public ApiSecurityResponseWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void writeUnauthorized(HttpServletResponse response, String message, String detail)
      throws IOException {
    Error error = new Error();
    error.setErrorCode("UNAUTHORIZED");
    error.setErrorMessage(message);
    error.setErrorType("AuthenticationException");
    if (detail != null && !detail.isBlank()) {
      error.setDetails(Map.of("detail", detail));
    }
    write(response, HttpStatus.UNAUTHORIZED, message, error);
  }

  public void writeForbidden(HttpServletResponse response, String message, String detail)
      throws IOException {
    Error error = new Error();
    error.setErrorCode("FORBIDDEN");
    error.setErrorMessage(message);
    error.setErrorType("AccessDeniedException");
    if (detail != null && !detail.isBlank()) {
      error.setDetails(Map.of("detail", detail));
    }
    write(response, HttpStatus.FORBIDDEN, message, error);
  }

  private void write(HttpServletResponse response, HttpStatus status, String message, Error error)
      throws IOException {
    ResponseWrapper<Void> body = ResponseWrapper.error(message, error);
    response.setStatus(status.value());
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getOutputStream(), body);
  }
}