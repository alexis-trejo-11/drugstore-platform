package io.github.alexisTrejo11.drugstore.products.config.security;

import io.github.alexisTrejo11.drugstore.products.config.security.ApiSecurityResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  private final ApiSecurityResponseWriter responseWriter;

  public RestAccessDeniedHandler(ApiSecurityResponseWriter responseWriter) {
    this.responseWriter = responseWriter;
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    String detail = accessDeniedException != null && accessDeniedException.getMessage() != null
        ? accessDeniedException.getMessage()
        : "";
    responseWriter.writeForbidden(
        response, "You do not have permission to access this resource", detail);
  }
}
