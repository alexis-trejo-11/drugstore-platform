package io.github.alexisTrejo11.drugstore.address.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI addressServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Address Service API")
                        .description("Drugstore Microservice for managing addresses in the e-commerce platform")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Alexis Trejo")
                                .email("marcoalexispt.02@gmail.com")
                                .url("https://ecommerce.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://ecommerce.com/license")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local development server"),
                        new Server().url("https://api.ecommerce.com/address-service").description("Production server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication")));
    }
}