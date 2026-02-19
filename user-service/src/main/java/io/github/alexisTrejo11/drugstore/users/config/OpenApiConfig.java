package io.github.alexisTrejo11.drugstore.users.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * OpenAPI/Swagger configuration for User Service API documentation.
 * <p>
 * Configures API metadata, security schemes, and server information for
 * comprehensive API documentation via Swagger UI.
 * </p>
 * 
 * <p>
 * Access Swagger UI at: <code>/swagger-ui/index.html</code>
 * </p>
 * <p>
 * Access OpenAPI spec at: <code>/v3/api-docs</code>
 * </p>
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "User Service API", version = "2.0", description = """
    RESTful API for managing user accounts in the Drugstore Microservices ecosystem.

    ## Features
    - User registration and account management
    - User authentication and authorization
    - Role-based access control (CUSTOMER, EMPLOYEE, ADMIN)
    - User status management (PENDING, ACTIVE, INACTIVE, SUSPENDED, DELETED)
    - Comprehensive user query capabilities

    ## Authentication
    Most endpoints require JWT Bearer token authentication. Admin operations require ADMIN role in the token.

    ## Error Handling
    All endpoints return standardized error responses with appropriate HTTP status codes:
    - **400**: Malformed request or validation errors
    - **401**: Missing or invalid authentication token
    - **403**: Insufficient permissions (requires ADMIN role)
    - **422**: Business logic validation failed
    - **500**: Internal server error
    """, contact = @Contact(name = "API Support", email = "support@drugstore.com"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")), servers = {
    @Server(description = "Local Development Server", url = "http://localhost:8087"),
    @Server(description = "Staging Server", url = "https://staging-api.drugstore.com"),
    @Server(description = "Production Server", url = "https://api.drugstore.com")
})
@SecurityScheme(name = "bearerAuth", description = "JWT Bearer token authentication. Obtain token from auth-service via /api/v1/auth/login endpoint.", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
  // Configuration is defined via annotations above
}
