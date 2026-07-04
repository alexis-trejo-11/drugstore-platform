package io.github.alexisTrejo11.drugstore.products.config.security;

import io.github.alexisTrejo11.drugstore.products.config.security.ApiSecurityResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ApiSecurityResponseWriter responseWriter;

  public RestAuthenticationEntryPoint(ApiSecurityResponseWriter responseWriter) {
    this.responseWriter = responseWriter;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    String detail = authException != null && authException.getMessage() != null
        ? authException.getMessage()
        : "";
    responseWriter.writeUnauthorized(
        response, "Authentication required", detail);
  }
}
