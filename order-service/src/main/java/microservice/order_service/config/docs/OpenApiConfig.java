package microservice.order_service.config.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Drugstore API Documentation - Order Service")
                        .version("2.0")
                        .description("Documentation for the Order Service of the Drugstore Microservices Application")
                        .contact(new Contact()
                                .name("Alexis Trejo")
                                .email("marcoalexispt.02@gmail.com")));
    }
}
