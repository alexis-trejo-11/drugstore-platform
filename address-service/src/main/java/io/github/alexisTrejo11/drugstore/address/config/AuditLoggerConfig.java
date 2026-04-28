package io.github.alexisTrejo11.drugstore.address.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import libs_kernel.log.audit.AuditLogInterceptor;
import libs_kernel.log.audit.AuditLogger;

@Configuration
public class AuditLoggerConfig implements WebMvcConfigurer {

  @Autowired
  private AuditLogger auditLogger;

  @Value("${spring.application.name:address-service}")
  private String serviceName;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    AuditLogInterceptor interceptor = new AuditLogInterceptor(auditLogger, serviceName) {

      @Override
      protected String extractUserId(HttpServletRequest request) {
        // Prioridad 1: Spring Security Context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
            && !"anonymousUser".equals(auth.getPrincipal())) {
          return auth.getName();
        }

        // Prioridad 2: Header X-User-ID (para testing)
        String userIdHeader = request.getHeader("X-User-ID");
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
          return userIdHeader;
        }

        return "anonymous";
      }

      @Override
      protected String sanitizeEndpoint(String endpoint) {
        if (endpoint == null)
          return "";

        // Limpiar UUIDs y IDs numéricos
        String sanitized = endpoint
            .replaceAll("/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}", "/{uuid}")
            .replaceAll("/\\d+", "/{id}");

        return sanitized;
      }

      @Override
      protected String extractOperation(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase();
        String endpoint = request.getRequestURI();

        // Operaciones específicas para addresses
        if (endpoint.contains("/addresses")) {
          if (endpoint.contains("/admin")) {
            return switch (method) {
              case "GET" -> "GET_ADMIN_ADDRESS";
              case "POST" -> "CREATE_ADMIN_ADDRESS";
              case "PUT", "PATCH" -> "UPDATE_ADMIN_ADDRESS";
              case "DELETE" -> "DELETE_ADMIN_ADDRESS";
              default -> method + "_ADMIN_ADDRESS";
            };
          } else if (endpoint.contains("/user")) {
            return switch (method) {
              case "GET" -> "GET_USER_ADDRESS";
              case "POST" -> "CREATE_USER_ADDRESS";
              case "PUT", "PATCH" -> "UPDATE_USER_ADDRESS";
              case "DELETE" -> "DELETE_USER_ADDRESS";
              default -> method + "_USER_ADDRESS";
            };
          }
        }

        // Operación genérica si no hay match
        return method + "_" + endpoint.replaceAll("/api/v2/", "")
            .replaceAll("/", "_")
            .replaceAll("[^a-zA-Z0-9_]", "")
            .toUpperCase();
      }

      @Override
      protected String getClientIp(HttpServletRequest request) {
        String ip = super.getClientIp(request);

        // Normalizar IPv6 localhost
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
          return "127.0.0.1";
        }

        return ip;
      }
    };

    registry.addInterceptor(interceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/v2/health/**",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/error/**");
  }
}