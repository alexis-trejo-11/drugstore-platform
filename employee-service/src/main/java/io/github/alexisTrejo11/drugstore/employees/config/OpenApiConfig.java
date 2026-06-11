package io.github.alexisTrejo11.drugstore.employees.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Value("${server.port:8080}")
  private String port;

  @Value("${server.host:localhost}")
  private String host;

  private String getInternalUrl() {
    return String.format("http://%s:%s", host, port);
  }

  @Bean
  public OpenAPI employeeServiceOpenAPI() {
    // "/" resolves against the browser origin where Swagger UI is opened — avoids wrong
    // scheme/port (e.g. https://localhost vs http://localhost:8084) and “Failed to fetch”.
    Server sameOrigin = new Server()
        .url("/")
        .description("Same origin as Swagger UI (recommended for Try it out)");

    Server directHttp = new Server()
        .url(getInternalUrl())
        .description("HTTP using configured server.host / server.port (e.g. inside-container port)");

    Server nginxServer = new Server()
        .url("https://localhost")
        .description("HTTPS via Nginx on host (ports 80/443 in Docker Compose)");

    Server prodServer = new Server()
        .url("https://api.ecommerce.com/employee-service")
        .description("Production");

    return new OpenAPI()
        .info(new Info()
            .title("Employee Service API")
            .description("Drugstore Microservice for managing employees in the e-commerce platform")
            .version("2.0.0")
            .contact(new Contact()
                .name("Alexis Trejo")
                .email("marcoalexispt.02@gmail.com")
                .url("https://ecommerce.com"))
            .license(new License()
                .name("Private")
                .url("https://ecommerce.com/license")))
        .servers(List.of(sameOrigin, directHttp, nginxServer, prodServer))
        .components(new Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token for authentication")));
  }
}