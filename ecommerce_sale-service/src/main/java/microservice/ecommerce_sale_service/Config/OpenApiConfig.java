package microservice.ecommerce_sale_service.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceSaleServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecommerce Sale Service API")
                        .description("Ecommerce Sale Microservice that handles all Ecommerce Sale Requests")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Codmind")
                                .url("https://codmind.com")
                                .email("apis@codmind.com"))
                        .termsOfService("http://codmind.com/terms")
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}