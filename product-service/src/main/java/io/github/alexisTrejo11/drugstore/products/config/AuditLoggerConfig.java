package io.github.alexisTrejo11.drugstore.products.config;

import io.github.alexisTrejo11.drugstore.products.config.log.AuditLogInterceptor;
import io.github.alexisTrejo11.drugstore.products.config.log.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class AuditLoggerConfig implements WebMvcConfigurer {

  @Autowired
  private AuditLogger auditLogger;

  @Value("${spring.application.name:product-service}")
  private String serviceName;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    AuditLogInterceptor interceptor = new AuditLogInterceptor(auditLogger, serviceName) {

      @Override
      protected String extractUserId(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
            && !"anonymousUser".equals(auth.getPrincipal())) {
          return auth.getName();
        }

        String userIdHeader = request.getHeader("X-User-ID");
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
          return userIdHeader;
        }

        return "anonymous";
      }

      @Override
      protected String sanitizeEndpoint(String endpoint) {
        if (endpoint == null) {
          return "";
        }

        return endpoint
            .replaceAll("/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}", "/{uuid}")
            .replaceAll("/\\d+", "/{id}");
      }

      @Override
      protected String getClientIp(HttpServletRequest request) {
        String ip = super.getClientIp(request);
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
          return "127.0.0.1";
        }
        return ip;
      }
    };

    registry.addInterceptor(interceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/error/**");
  }
}
