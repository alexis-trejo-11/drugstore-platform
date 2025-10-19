package microservice.store_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for Store Service
 *
 * This configuration enables comprehensive API documentation with JWT authentication support.
 * All endpoints require Bearer token authentication with ADMIN role unless specified otherwise.
 *
 * @author API Drugstore Team
 * @version 2.0
 * @since 2025
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Store Service API",
        version = "2.0.0",
        description = """
            # Store Service REST API Documentation
            
            ## Overview
            This microservice manages pharmacy store operations including:
            - **Store Management**: Create, update, and delete store entities
            - **Location Services**: Manage store locations and geolocation data
            - **Schedule Management**: Configure and update store operating hours
            - **Status Control**: Activate, deactivate, and set maintenance modes
            - **Advanced Search**: Filter and search stores by multiple criteria
            
            ## Authentication & Authorization
            All endpoints require JWT Bearer token authentication with **ADMIN role**.
            
            ### How to Authenticate:
            1. Obtain a JWT token from the auth-service
            2. Click the **Authorize** button (🔓) at the top of this page
            3. Enter your token in the format: `Bearer <your-jwt-token>`
            4. Click **Authorize** to apply the token to all requests
            
            ### Token Requirements:
            - Valid JWT token issued by the authentication service
            - Token must contain ADMIN role claim
            - Token must not be expired
            
            ## Response Format
            All endpoints return responses in the following standard format:
            ```json
            {
              "isSuccess": true/false,
              "message": "Description of the result",
              "data": { ... },
              "timestamp": "ISO-8601 datetime"
            }
            ```
            
            ## Error Handling
            Standard HTTP status codes are used:
            - **200**: Success
            - **201**: Created
            - **400**: Bad Request (validation errors)
            - **401**: Unauthorized (invalid/missing token)
            - **403**: Forbidden (insufficient permissions)
            - **404**: Not Found
            - **409**: Conflict (duplicate resources)
            - **500**: Internal Server Error
            
            ## Pagination
            List endpoints support pagination with the following parameters:
            - `page`: Page number (0-based, default: 0)
            - `size`: Items per page (default: 10, max: 100)
            - `sort`: Sort field and direction (e.g., "name,asc")
            
            ## Best Practices
            - Always include the Bearer token in the Authorization header
            - Validate input data before sending requests
            - Handle rate limiting appropriately
            - Use appropriate HTTP methods (POST for create, PUT/PATCH for update, DELETE for removal)
            
            ## Support
            For issues or questions, contact the development team.
            """,
        contact = @Contact(
            name = "API Drugstore Development Team",
            email = "support@drugstore-api.com",
            url = "https://github.com/drugstore-api"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Local Development Server",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "Staging Environment",
            url = "https://staging-api.drugstore.com"
        ),
        @Server(
            description = "Production Environment",
            url = "https://api.drugstore.com"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = """
        JWT Bearer Token Authentication
        
        **Required for all endpoints in this API.**
        
        ### How to obtain a token:
        1. Send a POST request to `/api/v1/auth/login` with valid credentials
        2. Extract the JWT token from the response
        3. Use the token in the Authorization header: `Bearer <token>`
        
        ### Token Structure:
        The JWT token should contain:
        - User identification (subject)
        - Role claims (must include ADMIN role)
        - Expiration time
        - Issuer information
        
        ### Security Notes:
        - Tokens are stateless and validated cryptographically
        - Keep your token secure and do not share it
        - Tokens expire after a configured time period
        - Use HTTPS in production environments
        
        ### Example:
        ```
        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        ```
        """,
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    /**
     * OpenAPI configuration is applied via annotations.
     * No additional bean configuration needed for basic setup.
     *
     * The @SecurityScheme annotation defines the JWT bearer authentication scheme.
     * The @OpenAPIDefinition provides metadata about the API.
     *
     * All controllers annotated with @SecurityRequirement(name = "bearerAuth")
     * will automatically require JWT authentication.
     */
}

