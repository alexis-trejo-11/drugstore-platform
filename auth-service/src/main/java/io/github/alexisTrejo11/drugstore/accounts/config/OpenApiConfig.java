package io.github.alexisTrejo11.drugstore.accounts.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global OpenAPI 3 configuration: API metadata, JWT bearer scheme, and server defaults.
 */
@Configuration
public class OpenApiConfig {

  public static final String BEARER_SCHEME = "bearerAuth";

  @Bean
  public OpenAPI authServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Drugstore Auth Service")
                .version("2.0")
                .description(
                    """
                    Authentication and account flows for the Drugstore platform: registration, \
                    login (including 2FA), session refresh, password lifecycle, and TOTP configuration.

                    **Response envelope:** All JSON responses use `ResponseWrapper<T>` with \
                    `message`, `data`, `timestamp`, and on failures `error` \
                    (`errorCode`, `errorMessage`, `errorType`, optional `details`).

                    **Rate limiting:** Sensitive routes use stricter per-IP limits \
                    (see `RateLimitProfile`). HTTP **429** with code `RATE_LIMIT_EXCEEDED` when exceeded.

                    **Authentication:** Endpoints marked with a lock require \
                    `Authorization: Bearer <access_token>` unless noted as public.""")
                .contact(new Contact().name("Drugstore Platform")))
        .addServersItem(new Server().url("/").description("Current origin (e.g. gateway or direct)"))
        .components(
            new Components()
                .addSecuritySchemes(
                    BEARER_SCHEME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description(
                            "Obtain an access token via POST `/api/v2/auth/login` or "
                                + "`/api/v2/auth/session/refresh`, then send "
                                + "`Authorization: Bearer <access_token>`.")));
  }
}
