package io.github.alexisTrejo11.drugstore.payments.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 / Swagger configuration.
 *
 * Swagger UI available at: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON spec at:    http://localhost:8080/v3/api-docs
 *
 * Security note: exclude Swagger endpoints in your SecurityFilterChain:
 *   .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment & Sale Service API")
                        .description("""
                            Microservice responsible for payment processing and sale tracking.

                            **Payment flow:**
                            1. `POST /api/v1/payments` → Initiate payment, receive `clientSecret`
                            2. Frontend confirms with `stripe.confirmPayment(clientSecret)`
                            3. Stripe calls `POST /api/v1/webhooks/stripe` → Payment completed → Sale created

                            **Refund flow:**
                            1. `POST /api/v1/payments/refund` → Full or partial refund via Stripe
                            """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("eCommerce Platform Team")
                                .email("platform@ecommerce.com"))
                        .license(new License()
                                .name("Internal — Not for public distribution")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development"),
                        new Server()
                                .url("https://api-staging.ecommerce.com")
                                .description("Staging"),
                        new Server()
                                .url("https://api.ecommerce.com")
                                .description("Production")
                ));
    }
}